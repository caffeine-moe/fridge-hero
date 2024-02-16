package moe.caffeine.fridgehero.ui.navigation.bar.bottom

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }
    BottomAppBar(
        modifier = Modifier.navigationBarsPadding(),
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
                            imageVector = if (selectedTabIndex == index)
                            {tabBarItem.selectedIcon} else {tabBarItem.unselectedIcon},
                            contentDescription = tabBarItem.title
                        )
                    }
                },
                label = { Text(tabBarItem.title) })
        }
    }
}