package moe.caffeine.fridgehero.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

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

  inline fun <reified T : RealmObject> updateObject(realmObject: T): Result<T> =
    try {
      Result.success(
        realm.writeBlocking {
          copyToRealm(realmObject, UpdatePolicy.ALL)
        })
    } catch (e: Exception) {
      Result.failure(e)
    }

  inline fun <reified T : RealmObject> tryWriteToLatest(
    realmObject: T,
    crossinline operation: (T) -> Unit
  ) {
    realm.writeBlocking {
      val latest = findLatest(realmObject) ?: return@writeBlocking
      operation(latest)
    }
  }

  inline fun <reified T : RealmObject> fetchObjectById(objectId: ObjectId): Result<T> {
    return try {
      Result.success(realm.query<T>("_id == $0", objectId).find().first())
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  inline fun <reified T : RealmObject> fetchAllByType(): List<T> =
    realm.query<T>().find().toList()

  inline fun <reified T : RealmObject> fetchAllByTypeAsFlow(): Flow<List<T>> =
    realm.query<T>().find().asFlow().map { results -> results.list }

  fun deleteObject(realmObject: RealmObject) {
    realm.writeBlocking {
      delete(findLatest(realmObject) ?: return@writeBlocking)
    }
  }
}
