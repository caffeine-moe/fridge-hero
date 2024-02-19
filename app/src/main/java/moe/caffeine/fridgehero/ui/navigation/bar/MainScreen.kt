package moe.caffeine.fridgehero.ui.navigation.bar

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import moe.caffeine.fridgehero.ui.navigation.bar.bottom.BottomBar
import moe.caffeine.fridgehero.ui.navigation.bar.bottom.BottomNavGraph
import moe.caffeine.fridgehero.ui.navigation.bar.bottom.TabBarItem


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val homeTab = TabBarItem(title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
    val myFridgeTab =
        TabBarItem(title = "My Fridge", selectedIcon = Icons.Filled.Kitchen, unselectedIcon = Icons.Outlined.Kitchen)
    val settingsTab =
        TabBarItem(title = "Settings", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)

    val tabBarItems = listOf(homeTab, myFridgeTab, settingsTab)
    val navController = rememberNavController()
    val barHeight = 80.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                BottomBar(tabBarItems, navController)
            },
        ) {

            BottomNavGraph(tabBarItems, navController, barHeight, homeTab.title)
        }
    }
}