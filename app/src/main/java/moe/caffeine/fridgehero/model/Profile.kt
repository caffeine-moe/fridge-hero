package moe.caffeine.fridgehero.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Profile : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var firstName: String = ""
    var lastName: String = ""

    @Ignore
    val fullName: String
        get() = "$firstName $lastName"
}
