package moe.caffeine.fridgehero.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class Profile : RealmObject {
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()

    var firstName: String = ""
    var lastName: String = ""
    var fridge: RealmList<FoodItem> = realmListOf()

    @Ignore
    val fullName: String
        get() = "$firstName $lastName"
}
