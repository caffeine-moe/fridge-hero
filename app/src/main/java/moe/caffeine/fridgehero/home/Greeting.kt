package moe.caffeine.fridgehero.home

import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Greeting(name: String) {
    val greetings = listOf("Hey", "Hiya", "Sup", "Hello")
    Text(
        text = "${greetings.random()}, ${name}.",
        style = typography.titleLarge
    )
}