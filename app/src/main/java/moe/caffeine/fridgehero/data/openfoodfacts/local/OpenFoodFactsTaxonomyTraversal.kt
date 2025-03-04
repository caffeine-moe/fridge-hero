package moe.caffeine.fridgehero.data.openfoodfacts.local

import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode

fun Map<String, OpenFoodFactsTaxonomyNode>.findAllAncestors(nodeId: String): Set<String> {
  val result = mutableSetOf<String>()
  val queue = ArrayDeque<String>()

  // add all parents to queue
  this[nodeId]?.parents?.forEach { parent ->
    queue.add(parent.key)
  }

  // find each parent of a parent in the parent queue
  while (queue.isNotEmpty()) {
    val current = queue.removeFirst()
    if (result.add(current)) {  // add to queue if not already processed
      this[current]?.parents?.forEach { parent ->
        queue.add(parent.key)
      }
    }
  }

  return result
}

//mostly the same process as above but in reverse
fun Map<String, OpenFoodFactsTaxonomyNode>.findAllDescendants(nodeId: String): Set<String> {
  val result = mutableSetOf<String>()
  val queue = ArrayDeque<String>()
  queue.add(nodeId)

  while (queue.isNotEmpty()) {
    val current = queue.removeFirst()
    result.add(current)

    this.filter { it.value.parents[nodeId] != null }.forEach { child ->
      if (child.key !in result) {
        queue.add(child.key)
      }
    }
  }

  result.remove(nodeId)

  return result
}
