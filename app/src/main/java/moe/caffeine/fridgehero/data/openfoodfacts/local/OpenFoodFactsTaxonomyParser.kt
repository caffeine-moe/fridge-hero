package moe.caffeine.fridgehero.data.openfoodfacts.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode
import org.mongodb.kbson.BsonObjectId
import java.io.InputStream

object OpenFoodFactsTaxonomyParser {

  private suspend fun loadTaxonomyInputStream(): InputStream = withContext(Dispatchers.IO) {
    javaClass.classLoader?.getResource("food_categories.txt")?.openStream()
      ?: throw Throwable("Unable to find taxonomy file")
  }

  suspend fun parse(): Map<String, OpenFoodFactsTaxonomyNode> {
    val rawTaxonomyText = loadTaxonomyInputStream().bufferedReader().use { it.readText() }
    val lines = rawTaxonomyText.lines()
      .filter { it.isNotBlank() && !it.startsWith("#") }

    val nodes = mutableMapOf<String, OpenFoodFactsTaxonomyNode>()

    // add all nodes to the structure
    lines.forEach { line ->
      when {
        //reference to a parent
        line.startsWith("< en:") -> {
          val parentName = line.substringAfter("< en:").trim()

          // create parent node if it doesn't exist
          if (!nodes.containsKey(parentName)) {
            nodes[parentName] = OpenFoodFactsTaxonomyNode(
              id = BsonObjectId(),
              name = parentName
            )
          }
        }

        //node definition
        line.startsWith("en:") -> {
          val parts = line.split(":", limit = 2)
          if (parts.size == 2) {
            val valueParts = parts[1].split(",")
            val name = valueParts.first().trim()

            // create node if it doesn't exist
            if (!nodes.containsKey(name)) {
              nodes[name] = OpenFoodFactsTaxonomyNode(
                id = BsonObjectId(),
                name = name
              )
            }
          }
        }
      }
    }

    // figure out node relations
    var currentParents = mutableListOf<String>()
    lines.forEach { line ->
      when {
        // parent reference
        line.startsWith("< en:") -> {
          val parentName = line.substringAfter("< en:").trim()

          // add to current parents
          if (!currentParents.contains(parentName)) {
            currentParents.add(parentName)
          }
        }

        // node definition
        line.startsWith("en:") && !line.contains(":en:") -> {
          val parts = line.split(":", limit = 2)
          if (parts.size == 2) {
            val valueParts = parts[1].split(",")
            val name = valueParts.first().trim()

            // propagate relationships to any parents found
            currentParents.forEach { parent ->
              nodes[parent]?.let { nodes[name]?.parents?.set(it.name, it) }

              nodes[name]?.let { nodes[parent]?.children?.set(it.name, it) }
            }

            // Reset current parents for next node
            currentParents = mutableListOf()
          }
        }
      }
    }

    return nodes
  }
}
