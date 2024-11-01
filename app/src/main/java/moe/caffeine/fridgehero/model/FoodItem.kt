package moe.caffeine.fridgehero.model

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class FoodItem : RealmObject {
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()

    var name: String = ""
    var brand: String = ""
    var barcode: String = ""
    var expiryDate: Long = 0
    val recipes: RealmResults<Recipe> by backlinks(Recipe::ingredients)
}