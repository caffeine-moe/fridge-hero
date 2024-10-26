package moe.caffeine.fridgehero.repo

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import moe.caffeine.fridgehero.model.Profile

object MongoRealm {
    private val realm: Realm = Realm.open(
        configuration = RealmConfiguration.create(
            schema = setOf(
                Profile::class
            )
        )
    )

    fun insertObject(realmObject: RealmObject) {
        realm.writeBlocking {
            copyToRealm(realmObject)
        }
    }

    fun fetchProfiles(): List<Profile> {
        return realm.query<Profile>().find().toList()
    }
}