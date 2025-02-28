package moe.caffeine.fridgehero.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.model.realm.RealmProfile
import moe.caffeine.fridgehero.data.model.realm.RealmRecipe

object RealmProvider {
  private val realmConfig: RealmConfiguration = RealmConfiguration.create(
    schema = setOf(
      RealmProfile::class,
      RealmFoodItem::class,
      RealmRecipe::class,
      RealmFoodCategory::class
    )
  )

  val realmInstance: Realm by lazy { Realm.open(realmConfig) }
}
