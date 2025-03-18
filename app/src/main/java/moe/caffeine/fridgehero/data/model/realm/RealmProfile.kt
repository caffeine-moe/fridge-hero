package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.Profile
import org.mongodb.kbson.BsonObjectId

class RealmProfile : RealmObject, MappableModel<Profile, RealmProfile> {
  @PrimaryKey
  override var realmObjectId: BsonObjectId = BsonObjectId()


  var firstName: String = ""
  var lastName: String = ""

  override fun toDomainModel() = Profile(realmObjectId.toHexString(), firstName, lastName)
  override fun toRealmModel(): RealmProfile = this
}
