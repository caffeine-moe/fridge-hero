package moe.caffeine.fridgehero.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.BsonObjectId

inline fun <reified T : RealmObject> Realm.updateObject(realmObject: T): Result<T> =
  try {
    Result.success(
      writeBlocking {
        copyToRealm(realmObject, UpdatePolicy.ALL)
      })
  } catch (e: Exception) {
    Result.failure(e)
  }

/*inline fun <reified T : RealmObject> Realm.tryWriteToLatest(
  realmObject: T,
  crossinline operation: (T) -> Unit
) {
  writeBlocking {
    val latest = findLatest(realmObject) ?: return@writeBlocking
    operation(latest)
  }
}*/

inline fun <reified T : RealmObject> Realm.fetchObjectById(objectId: BsonObjectId): Result<T> {
  return try {
    Result.success(query<T>("_id == $0", objectId).find().first())
  } catch (e: Exception) {
    Result.failure(e)
  }
}

inline fun <reified T : RealmObject> Realm.fetchAllByType(): List<T> =
  query<T>().find().toList()

inline fun <reified T : RealmObject> Realm.fetchAllByTypeAsFlow(): Flow<List<T>> =
  query<T>().find().asFlow().map { results -> results.list }

inline fun <reified T : RealmObject> Realm.deleteObject(realmObject: T): Result<T> =
  writeBlocking {
    findLatest(realmObject)?.let { delete(it); Result.success(it) }
      ?: Result.failure(Throwable("Object not found in database."))
  }
