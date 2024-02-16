package moe.caffeine.fridgehero.user.config

import kotlinx.serialization.Serializable

@Serializable
data class ProfileConfig(
    var firstName : String = "",
    var lastName : String = "",
){
    val fullName : String get() = "$firstName + $lastName"
}