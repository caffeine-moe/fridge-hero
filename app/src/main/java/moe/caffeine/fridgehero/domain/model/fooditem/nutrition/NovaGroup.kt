package moe.caffeine.fridgehero.domain.model.fooditem.nutrition

enum class NovaGroup(val number: Int) {
  UNKNOWN(0),
  UNPROCESSED(1),
  PROCESSED_INGREDIENTS(2),
  PROCESSED(3),
  ULTRA_PROCESSED(4);

  companion object {
    fun enumByNumber(number: Int): NovaGroup =
      NovaGroup.entries.firstOrNull { it.number == number } ?: UNKNOWN
  }
}
