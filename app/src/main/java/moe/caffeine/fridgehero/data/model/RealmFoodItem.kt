package moe.caffeine.fridgehero.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class RealmFoodItem : RealmObject {
  @PrimaryKey
  var _id: BsonObjectId = BsonObjectId()

  var name: String = ""
  var brand: String = ""
  var barcode: String = ""
  var imageByteArray: ByteArray = byteArrayOf()
  var expiryDates: RealmList<Long> = realmListOf()
}
