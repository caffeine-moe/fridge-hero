package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.types.RealmObject

class RealmNutrimentEntry : RealmObject {
  var nutriment: String = ""
  var value: Double = 0.0
}
