package moe.caffeine.fridgehero.ui.oobe

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.MainActivity
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun OOBE(onCreateProfile: (String, String) -> Unit) {
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var firstNameError by remember { mutableStateOf(false) }
  var lastNameError by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val activity = context as? Activity
  Surface {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      //BBWM LETS GO
      Text(
        style = Typography.headlineMedium,
        text = "Welcome to Fridge Hero!",
        textAlign = TextAlign.Center
      )

      Text(
        modifier = Modifier.padding(10.dp),
        style = Typography.bodyLarge,
        text = "Create a profile to get started.",
        textAlign = TextAlign.Center
      )

      OutlinedTextField(
        value = firstName,
        onValueChange = {
          firstNameError = false
          firstName = it.trim().replaceFirstChar { char -> char.titlecase() }
        },
        label = { Text("First Name") },
        placeholder = { Text("John") },
        isError = firstNameError
      )

      OutlinedTextField(
        value = lastName,
        onValueChange = {
          lastNameError = false
          lastName = it.trim().replaceFirstChar { char -> char.titlecase() }
        },
        label = { Text("Last Name") },
        placeholder = { Text("Doe") },
        isError = lastNameError
      )

      Button(
        modifier = Modifier.padding(10.dp),
        onClick = {
          firstName.ifEmpty { firstNameError = true }
          lastName.ifEmpty { lastNameError = true }
          when (firstName.isEmpty() || lastName.isEmpty()) {
            true -> return@Button
            else -> {
              onCreateProfile(firstName, lastName)
              activity?.finish()
              context.startActivity(Intent(context, MainActivity::class.java))
            }
          }
        }
      ) {
        Text("Create Profile")
      }
    }
  }
}