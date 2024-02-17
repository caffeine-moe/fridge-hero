package util

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import moe.caffeine.fridgehero.user.profile.Profile
import moe.caffeine.fridgehero.user.profile.ProfileImpl

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

lateinit var profile : Profile