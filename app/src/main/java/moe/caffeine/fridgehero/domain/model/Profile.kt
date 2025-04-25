package moe.caffeine.fridgehero.domain.model

import moe.caffeine.fridgehero.data.model.realm.RealmProfile
import moe.caffeine.fridgehero.domain.mapping.MappableModel

data class Profile(
  override val realmId: String = "",
  val firstName: String = "",
  val lastName: String = "",
  val householdComposition: Pair<Int, Int> = (0 to 0)
) : DomainModel, MappableModel<Profile, RealmProfile> {
  val fullName: String
    get() = "$firstName $lastName"

  override fun toRealmModel(): RealmProfile =
    RealmProfile().apply {
      realmObjectId = this@Profile.realmObjectId
      firstName = this@Profile.firstName
      lastName = this@Profile.lastName
      adultsInHouse = this@Profile.householdComposition.first
      childrenInHouse = this@Profile.householdComposition.second
    }

  override fun toDomainModel(): Profile = this
}
