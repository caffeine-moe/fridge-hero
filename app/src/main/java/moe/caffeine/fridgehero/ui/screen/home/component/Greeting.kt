package moe.caffeine.fridgehero.ui.screen.home.component

import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun Greeting(name: String) {
  val greetings by remember {
    derivedStateOf {
      listOf("Hey", "Hiya", "Sup", "Hello").random()
    }
  }
  Text(
    text = "${greetings}, ${name}.",
    style = typography.titleLarge
  )
}
