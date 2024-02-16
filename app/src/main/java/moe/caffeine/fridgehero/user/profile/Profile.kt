package moe.caffeine.fridgehero.user.profile

import kotlinx.serialization.Serializable
import moe.caffeine.fridgehero.user.config.ProfileConfig

@Serializable
sealed class Profile {
    abstract val id : String
    abstract val config : ProfileConfig
    abstract val fridge : List<String>
}