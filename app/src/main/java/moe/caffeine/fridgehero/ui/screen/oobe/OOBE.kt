package moe.caffeine.fridgehero.ui.screen.oobe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun OOBE(onCreateProfile: (String, String) -> Unit) {
  var firstName by rememberSaveable { mutableStateOf("") }
  var lastName by rememberSaveable { mutableStateOf("") }
  var clicked by rememberSaveable { mutableStateOf(false) }
  Surface {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = Icons.Default.Kitchen,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .size(80.dp)
          .padding(10.dp)
      )

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
          firstName = it.replaceFirstChar { char -> char.titlecaseChar() }
        },
        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
        label = { Text("First Name") },
        placeholder = { Text("John") },
        isError = firstName.isEmpty() && clicked
      )

      OutlinedTextField(
        value = lastName,
        onValueChange = {
          lastName = it.replaceFirstChar { char -> char.titlecaseChar() }
        },
        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
        label = { Text("Last Name") },
        placeholder = { Text("Doe") },
        isError = lastName.isEmpty() && clicked
      )

      Button(
        modifier = Modifier.padding(10.dp),
        onClick = {
          clicked = true
          when (firstName.isEmpty() || lastName.isEmpty()) {
            true -> return@Button
            else -> {
              onCreateProfile(firstName, lastName)
            }
          }
        }
      ) {
        Text("Create Profile")
      }
    }
  }
}
