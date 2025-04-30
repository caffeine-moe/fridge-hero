package moe.caffeine.fridgehero.domain.model.fooditem.nutrition

enum class NutrimentAvailability(val title: String = "") {
  UNAVALIABLE("Unavailable"),
  PER_SERVING("Serving"),
  PER_100G("100g"),
  FULL("Item"),
}
