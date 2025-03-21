package moe.caffeine.fridgehero.domain.helper

fun fuzzyMatch(text: String, query: String): Boolean {
  val queryTokens = query.trim().split(Regex("\\s+"))
  val textTokens = text.trim().split(Regex("\\s+"))

  // calculate the Levenshtein distance between two strings.
  fun levenshteinDistance(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length
    val distances = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) distances[i][0] = i
    for (j in 0..n) distances[0][j] = j

    for (i in 1..m) {
      for (j in 1..n) {
        distances[i][j] = if (s1[i - 1].equals(s2[j - 1], ignoreCase = true))
          distances[i - 1][j - 1]
        else
          minOf(
            distances[i - 1][j],
            distances[i][j - 1],
            distances[i - 1][j - 1]
          ) + 1
      }
    }
    return distances[m][n]
  }

  return queryTokens.all { queryToken ->
    textTokens.any { textToken ->
      if (textToken.contains(queryToken, ignoreCase = true)) {
        true
      } else {
        val allowedDistance = if (queryToken.length < 4) 0 else queryToken.length / 4
        levenshteinDistance(queryToken, textToken) <= allowedDistance
      }
    }
  }
}
