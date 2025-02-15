package moe.caffeine.fridgehero.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import moe.caffeine.fridgehero.data.model.RealmFoodItem
import moe.caffeine.fridgehero.data.model.RealmProfile
import moe.caffeine.fridgehero.data.model.RealmRecipe

object RealmProvider {
  private val realmConfig: RealmConfiguration = RealmConfiguration.create(
    schema = setOf(
      RealmProfile::class,
      RealmFoodItem::class,
      RealmRecipe::class
    )
  )

  val realmInstance: Realm by lazy { Realm.open(realmConfig) }
}
