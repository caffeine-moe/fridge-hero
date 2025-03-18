package moe.caffeine.fridgehero.domain.mapping

import io.realm.kotlin.types.RealmObject
import moe.caffeine.fridgehero.domain.model.DomainModel
import org.mongodb.kbson.BsonObjectId

interface MappableModel<DomainType : DomainModel, RealmType : RealmObject> {
  val realmObjectId: BsonObjectId
    get() = toDomainModel().realmId.let {
      if (it.isBlank()) BsonObjectId() else BsonObjectId.invoke(
        it
      )
    }

  fun toDomainModel(): DomainType
  fun toRealmModel(): RealmType
}
