package moe.caffeine.fridgehero.model

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class Recipe : RealmObject {
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()

    var name: String = ""
    val ingredients: RealmResults<FoodItem> by backlinks(FoodItem::recipes)
    var instructions: String = ""
}