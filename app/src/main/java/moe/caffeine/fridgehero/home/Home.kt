package moe.caffeine.fridgehero.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.model.Profile

@Composable
fun Home(profile: Profile) {
    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Greeting(profile.firstName)
        ExpiringSoon()
        AvailableRecipes()
    }
}