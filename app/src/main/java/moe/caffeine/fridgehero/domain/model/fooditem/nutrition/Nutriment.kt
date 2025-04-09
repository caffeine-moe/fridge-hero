package moe.caffeine.fridgehero.domain.model.fooditem.nutrition

enum class Nutriment(val unit: String = "g") {
  CARBOHYDRATES,
  ENERGY("kcal"),
  FAT,
  FIBER,
  PROTEINS,
  SALT,
  SATURATED_FAT,
  SODIUM,
  SUGARS
}
