package moe.caffeine.fridgehero.repo

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import moe.caffeine.fridgehero.model.FoodItem
import moe.caffeine.fridgehero.model.Profile
import moe.caffeine.fridgehero.model.Recipe
import org.mongodb.kbson.BsonObjectId

object MongoRealm {
    val realm: Realm = Realm.open(
        configuration = RealmConfiguration.create(
            schema = setOf(
                Profile::class,
                FoodItem::class,
                Recipe::class
            )
        )
    )

    fun updateObject(realmObject: RealmObject) {
        realm.writeBlocking {
            copyToRealm(realmObject, UpdatePolicy.ALL)
        }
    }

    inline fun <reified T : RealmObject> fetchAllByType(): List<T> =
        realm.query<T>().find().toList()

    inline fun <reified T : RealmObject> fetchAllByTypeAsFlow(): Flow<List<T>> =
        realm.query<T>().find().asFlow().map { results -> results.list }

    inline fun <reified T : RealmObject> fetchObjectById(id: BsonObjectId): T? =
        realm.query<T>("_id == $0", id).first().find()

    fun deleteObject(realmObject: RealmObject) {
        realm.writeBlocking {
            delete(findLatest(realmObject) ?: return@writeBlocking)
        }
    }
}