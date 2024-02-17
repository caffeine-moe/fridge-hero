package moe.caffeine.fridgehero.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import util.profile

@Composable
fun Home(barHeight : Dp) {
    val greetings = listOf("Hey", "Hiya", "Sup", "Hello")
    Surface(
        modifier = Modifier.fillMaxSize().padding(bottom=barHeight)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(9.dp),
                text = "${greetings.random()}, ${profile.config.firstName}.",
                style = typography.titleLarge
            )
            HomeSearch()
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}