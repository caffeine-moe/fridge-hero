package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class RealmRecipe : RealmObject {
  @PrimaryKey
  var _id: BsonObjectId = BsonObjectId()

  var name: String = ""

  var ingredientIds: RealmList<BsonObjectId> = realmListOf()
  var instructions: String = ""
}
