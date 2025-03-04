package moe.caffeine.fridgehero.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.TRUE_PREDICATE
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

inline fun <reified T : RealmObject> Realm.fetchAllByType(
  query: String = TRUE_PREDICATE,
  vararg args: Any?
): List<T> =
  query<T>(query, *args).find().toList()

inline fun <reified T : RealmObject> Realm.fetchAllByTypeAsFlow(
  query: String = TRUE_PREDICATE,
  vararg args: Any?
): Flow<List<T>> =
  query<T>(query, *args).find().asFlow().map { results -> results.list }

inline fun <reified T : RealmObject> Realm.deleteObjectById(objectId: BsonObjectId): Result<T> =
  fetchObjectById<T>(objectId).fold(
    onSuccess = {
      writeBlocking {
        findLatest(it)?.let { latest -> delete(latest); Result.success(latest) }
          ?: Result.failure(Throwable("Object not found in database."))
      }
    },
    onFailure = {
      Result.failure(Throwable("Object not found in database."))
    }
  )
