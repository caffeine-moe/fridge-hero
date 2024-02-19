package moe.caffeine.fridgehero.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearch() {
    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    SearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        onSearch = {
            active = false
        },
        placeholder = {
            Text("")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        },
        trailingIcon = {},
        content = {},
        active = active,
        onActiveChange = {
            //active=it
        },
        tonalElevation = 0.dp
    )
}