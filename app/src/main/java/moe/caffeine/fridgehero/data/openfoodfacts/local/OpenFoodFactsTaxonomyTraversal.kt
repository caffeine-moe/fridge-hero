package moe.caffeine.fridgehero.data.openfoodfacts.local

/*fun Map<String, OpenFoodFactsTaxonomyNode>.findAllAncestors(nodeId: String): Set<String> {
  val result = mutableSetOf<String>()
  val queue = ArrayDeque<String>()

  // Start with direct parents
  this[nodeId]?.parents?.forEach { parentId ->
    queue.add(parentId)
  }

  while (queue.isNotEmpty()) {
    val current = queue.removeFirst()
    if (result.add(current)) {  // Only process if not already processed
      this[current]?.parents?.forEach { parentId ->
        queue.add(parentId)
      }
    }
  }

  return result
}

fun Map<String, OpenFoodFactsTaxonomyNode>.findAllDescendants(nodeId: String): Set<String> {
  val result = mutableSetOf<String>()
  val queue = ArrayDeque<String>()
  queue.add(nodeId)

  while (queue.isNotEmpty()) {
    val current = queue.removeFirst()
    result.add(current)

    this[current]?.children?.forEach { childId ->
      if (childId !in result) {
        queue.add(childId)
      }
    }
  }

  // Remove the starting node itself
  result.remove(nodeId)

  return result
}*/
