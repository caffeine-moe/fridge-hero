package util

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import moe.caffeine.fridgehero.user.profile.Profile
import moe.caffeine.fridgehero.user.profile.ProfileImpl

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    coerceInputValues = true
}

val client = HttpClient(CIO)

lateinit var profile : Profile