package moe.caffeine.fridgehero.data.openfoodfacts.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode
import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser.Constants.NODE_DEFINITION
import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser.Constants.PARENT_DEFINITION
import java.io.InputStream

object OpenFoodFactsTaxonomyParser {

  data object Constants {
    //lines starting with this are referring to parents of
    //a following node definition
    const val PARENT_DEFINITION = "< en:"

    //lines starting with this define individual nodes
    const val NODE_DEFINITION = "en:"
  }

  fun normalisedIdFromName(name: String) =
    name.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()

  private suspend fun loadTaxonomyInputStream(): Result<InputStream> = withContext(Dispatchers.IO) {
    javaClass.classLoader?.getResource("food_categories.txt")?.openStream()?.let {
      Result.success(it)
    } ?: Result.failure(Throwable("Unable to find taxonomy file, potentially invalid APK."))
  }

  suspend fun parse(): Result<Map<String, OpenFoodFactsTaxonomyNode>> {
    val rawTaxonomyText =
      loadTaxonomyInputStream().getOrElse { return Result.failure(it) }.bufferedReader()
        .use { it.readText() }
    val lines = rawTaxonomyText.lines()
      .filter { it.isNotBlank() && !it.startsWith("#") }

    //keep a map of the entire DAG
    val nodes = mutableMapOf<String, OpenFoodFactsTaxonomyNode>()
    var currentParents = mutableSetOf<String>()
    
    lines.forEach { line ->
      when {
        line.startsWith(PARENT_DEFINITION) -> {
          val parentName = line.substringAfter(PARENT_DEFINITION).trim()
          val parentId = normalisedIdFromName(parentName)


          if (!nodes.containsKey(parentId)) {
            nodes[parentId] = OpenFoodFactsTaxonomyNode(parentName)
          }
          currentParents.add(parentId)
        }

        line.startsWith(NODE_DEFINITION) -> {
          val parts = line.split(":", limit = 2)
          if (parts.size == 2) { //if it's not "en:something" then it's not a node definition
            val valueParts = parts[1].split(",") //we only want the canonical name
            val name = valueParts.first().trim()
            val id = normalisedIdFromName(name)

            if (!nodes.containsKey(id)) {
              nodes[id] = OpenFoodFactsTaxonomyNode(name)
            }

            // propagate relationships to any parents found
            currentParents.forEach { parent ->
              nodes[parent]?.let { nodes[id]?.parents?.set(it.id, it) }

              nodes[id]?.let { nodes[parent]?.children?.set(it.id, it) }
            }

            // reset current parents for next node
            currentParents = mutableSetOf()
          }
        }
      }
    }

    return Result.success(nodes)
  }
}
