package moe.caffeine.fridgehero.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import moe.caffeine.fridgehero.user.profile.Profile

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    coerceInputValues = true
}

val client = HttpClient(CIO)

lateinit var profile : Profile