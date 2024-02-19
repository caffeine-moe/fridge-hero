package moe.caffeine.fridgehero.ui.navigation.bar.top

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import moe.caffeine.fridgehero.screen.home.HomeSearch

class TopBar {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Composable() {
        TopAppBar(
            title = {
                HomeSearch()
            },
            actions = {
                IconButton(onClick = { /* Do something */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )
    }
}