package moe.caffeine.fridgehero.oobe

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun OOBE() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
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
                    firstName = it.trim().replaceFirstChar { char -> char.titlecase() }
                },
                label = { Text("First Name") },
                placeholder = { Text("John") }
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it.trim().replaceFirstChar { char -> char.titlecase() }
                },
                label = { Text("Last Name") },
                placeholder = { Text("Doe") }
            )

            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    println("$firstName $lastName")
                }
            ) {
                Text("Create Profile")
            }
        }
    }
}