package moe.caffeine.fridgehero.repo

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.map
import moe.caffeine.fridgehero.model.Profile

object MongoRealm {
        val realm : Realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    Profile::class
                )
            )
        )

    fun insertProfile(profile : Profile) {
        realm.writeBlocking {
            copyToRealm(profile)
        }
    }

    fun fetchProfiles() : List<Profile> {
        return realm.query<Profile>().find().toList()
    }
}