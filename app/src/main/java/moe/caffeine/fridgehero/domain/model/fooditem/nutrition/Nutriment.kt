package moe.caffeine.fridgehero.domain.model.fooditem.nutrition

enum class Nutriment(val title: String, val unit: String = "g") {
  CARBOHYDRATES("Carbohydrates"),
  ENERGY("Energy", "kcal"),
  FAT("Fat"),
  FIBER("Fiber"),
  PROTEINS("Proteins"),
  SALT("Salt"),
  SATURATED_FAT("Saturated Fat"),
  SODIUM("Sodium"),
  SUGARS("Sugars")
}
