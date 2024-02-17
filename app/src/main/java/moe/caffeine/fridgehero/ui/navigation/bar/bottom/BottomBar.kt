package moe.caffeine.fridgehero.ui.navigation.bar.bottom

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }
    var componentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    BottomAppBar(
        modifier = Modifier
            .navigationBarsPadding()
        ) {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    BadgedBox(badge = {
                        if (tabBarItem.badgeAmount != null) {
                            Badge {
                                Text(tabBarItem.badgeAmount.toString())
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (selectedTabIndex == index) {
                                tabBarItem.selectedIcon
                            } else {
                                tabBarItem.unselectedIcon
                            },
                            contentDescription = tabBarItem.title
                        )
                    }
                },
                label = { Text(tabBarItem.title) })
        }
    }
}