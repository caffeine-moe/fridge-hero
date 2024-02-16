package moe.caffeine.fridgehero.ui.navigation.bar.top

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import moe.caffeine.fridgehero.home.HomeSearch

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