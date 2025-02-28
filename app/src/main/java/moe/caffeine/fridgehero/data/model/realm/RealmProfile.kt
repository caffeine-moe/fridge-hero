package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class RealmProfile : RealmObject {
  @PrimaryKey
  var _id: BsonObjectId = BsonObjectId()

  var firstName: String = ""
  var lastName: String = ""
}
