package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser
import moe.caffeine.fridgehero.data.realm.fetchAllByType
import org.mongodb.kbson.BsonObjectId

class RealmFoodItem : RealmObject {
  @PrimaryKey
  var _id: BsonObjectId = BsonObjectId()

  var name: String = ""
  var brand: String = ""
  var barcode: String = ""
  var imageByteArray: ByteArray = byteArrayOf()
  var expiryDates: RealmList<Long> = realmListOf()
  var categoryNames: RealmSet<String> = realmSetOf()

  @Ignore
  val categories: (Realm) -> Map<String, RealmFoodCategory>
    get() = { realm ->
      categoryNames.flatMap {
        realm.fetchAllByType<RealmFoodCategory>(
          "id == $0",
          OpenFoodFactsTaxonomyParser.normalisedIdFromName(it)
        )
      }.associateBy { it.name }
    }
}
