package util

import kotlinx.serialization.json.Json
import moe.caffeine.fridgehero.user.profile.Profile

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

lateinit var profile : Profile