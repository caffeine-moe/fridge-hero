package moe.caffeine.fridgehero.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Profile : RealmObject {
    @PrimaryKey
    var _id : ObjectId = ObjectId()
    var firstName : String = ""
    var lastName : String = ""
    val fullName : String = "$firstName $lastName"
}
