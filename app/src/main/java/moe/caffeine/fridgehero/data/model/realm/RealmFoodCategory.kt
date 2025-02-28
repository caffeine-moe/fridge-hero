package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class RealmFoodCategory : RealmObject {
  @PrimaryKey
  var _id: BsonObjectId = BsonObjectId()

  @Index
  var name: String = ""
  var parents: RealmList<RealmFoodCategory> = realmListOf()
  val children: RealmResults<RealmFoodCategory> by backlinks(
    RealmFoodCategory::parents,
    RealmFoodCategory::class
  )
}
