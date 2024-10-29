package moe.caffeine.fridgehero.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Greeting(name: String) {
    val greetings = listOf("Hey", "Hiya", "Sup", "Hello")
    Text(
        modifier = Modifier.padding(10.dp),
        text = "${greetings.random()}, ${name}.",
        style = typography.titleLarge
    )
}