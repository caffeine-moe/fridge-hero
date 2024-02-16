package moe.caffeine.fridgehero.user

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import moe.caffeine.fridgehero.user.profile.Profile
import moe.caffeine.fridgehero.user.profile.ProfileImpl
import util.json

@Composable
fun fetchProfiles() : List<Profile> {
    val context = LocalContext.current
    val files: Array<String> = context.fileList()
    return files
        .filter { it.contains(".json") }
        .map { fileName ->
            val content = context.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.fold("") { some, text ->
                    "$some\n$text"
                }
            }
            json.decodeFromString<Profile>(content)
        }
}