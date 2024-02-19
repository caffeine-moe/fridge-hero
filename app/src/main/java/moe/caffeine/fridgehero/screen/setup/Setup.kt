package moe.caffeine.fridgehero.screen.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.user.ProfileCreation

@Composable
fun Setup() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to FridgeHero",
                    style = typography.headlineLarge,
                    fontFamily = FontFamily.Default,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "You must be new here, lets create a Profile!",
                    style = typography.bodySmall,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(0.dp, 36.dp, 0.dp, 0.dp)
                )
                ProfileCreation()
            }
        }
    }
}