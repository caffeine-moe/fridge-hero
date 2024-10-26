package moe.caffeine.fridgehero.repo

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import moe.caffeine.fridgehero.model.Profile

object MongoRealm {
    val realm: Realm = Realm.open(
        configuration = RealmConfiguration.create(
            schema = setOf(
                Profile::class
            )
        )
    )

    fun updateObject(realmObject: RealmObject) {
        realm.writeBlocking {
            copyToRealm(realmObject)
        }
    }

    inline fun <reified T : RealmObject> fetchAllByType(): List<T> {
        return realm.query<T>().find().toList()
    }

    fun deleteObject(realmObject: RealmObject) {
        realm.writeBlocking {
            delete(findLatest(realmObject) ?: return@writeBlocking)
        }
    }
}