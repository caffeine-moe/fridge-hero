package moe.caffeine.fridgehero.domain.model

import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment

data class NutrimentBreakdown(
  val items: List<FoodItem>,
  val totals: Map<Nutriment, Double>,
  val averageNutriScore: NutriScore,
  val averageNovaGroup: NovaGroup
)
