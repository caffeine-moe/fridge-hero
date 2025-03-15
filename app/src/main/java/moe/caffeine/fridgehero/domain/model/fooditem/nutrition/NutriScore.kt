package moe.caffeine.fridgehero.domain.model.fooditem.nutrition

enum class NutriScore(val letter: String) {
  UNKNOWN(""),
  A("a"),
  B("b"),
  C("c"),
  D("d"),
  E("e");

  companion object {
    fun enumByLetter(letter: String): NutriScore =
      NutriScore.entries.firstOrNull { it.letter == letter } ?: UNKNOWN
  }
}
