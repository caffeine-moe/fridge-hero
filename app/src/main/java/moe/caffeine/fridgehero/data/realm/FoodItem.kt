package moe.caffeine.fridgehero.data.realm

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class FoodItem : RealmObject {
  @PrimaryKey
  var _id: BsonObjectId = BsonObjectId()

  var name: String = ""
  var brand: String = ""
  var barcode: String = ""
  var imageByteArray: ByteArray = byteArrayOf()
  var expiryDates: RealmList<Long> = realmListOf()

  @Ignore
  val isRemoved: Boolean
    get() = expiryDates.isEmpty()

  @Ignore
  val imageBitmap: ImageBitmap
    get() = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
      .asImageBitmap()
}
