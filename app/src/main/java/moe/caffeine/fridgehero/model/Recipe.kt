package moe.caffeine.fridgehero.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class Recipe : RealmObject {
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()

    var name: String = ""
    val ingredients: RealmList<BsonObjectId> = realmListOf()
    var instructions: String = ""
}