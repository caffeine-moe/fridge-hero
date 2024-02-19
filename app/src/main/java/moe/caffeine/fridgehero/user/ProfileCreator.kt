package moe.caffeine.fridgehero.user

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.serialization.encodeToString
import moe.caffeine.fridgehero.MainActivity
import moe.caffeine.fridgehero.user.profile.Profile
import moe.caffeine.fridgehero.user.profile.ProfileImpl
import moe.caffeine.fridgehero.util.json
import java.util.*

@Composable
fun ProfileCreation() {
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    fun createProfile() {
        val profile = ProfileImpl.build {
            config {
                this@config.firstName =
                    firstName.text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                this@config.lastName =
                    lastName.text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
        }
        WriteProfileToDisk(profile, context)
    }
    OutlinedTextField(
        value = firstName,
        onValueChange = {
            firstName = it
        },
        label = { Text(text = "First Name") },
        placeholder = { Text(text = "John") },
    )
    OutlinedTextField(
        value = lastName,
        onValueChange = {
            lastName = it
        },
        label = { Text(text = "Last Name") },
        placeholder = { Text(text = "Pork") },
    )
    Button(
        modifier = Modifier.padding(36.dp),
        onClick = {
            createProfile()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
        Text("Create Profile")
    }
}

private fun WriteProfileToDisk(profile : Profile, context : Context) {
    val profileJson = json.encodeToString(profile)
    println(profileJson)
    val filename = "${profile.id}.json"
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(profileJson.toByteArray())
    }
}