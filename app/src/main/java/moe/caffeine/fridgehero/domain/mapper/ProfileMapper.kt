package moe.caffeine.fridgehero.domain.mapper

import moe.caffeine.fridgehero.data.model.RealmProfile
import moe.caffeine.fridgehero.domain.model.Profile

fun RealmProfile.toDomainModel(): Profile =
  Profile(_id, firstName, lastName)

fun Profile.toRealmModel(): RealmProfile =
  RealmProfile().apply {
    _id = this@toRealmModel.realmId
    firstName = this@toRealmModel.firstName
    lastName = this@toRealmModel.lastName
  }
