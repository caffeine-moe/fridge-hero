package moe.caffeine.fridgehero.domain.model

import org.mongodb.kbson.BsonObjectId

data class Profile(
  val realmId: BsonObjectId = BsonObjectId(),
  val firstName: String = "",
  val lastName: String = "",
) {
  val fullName: String
    get() = "$firstName $lastName"
}
