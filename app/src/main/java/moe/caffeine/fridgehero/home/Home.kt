package moe.caffeine.fridgehero.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import moe.caffeine.fridgehero.model.Profile

@Composable
fun Home(profile: Profile) {
    Surface {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Greeting(profile.firstName)
        }
    }
}