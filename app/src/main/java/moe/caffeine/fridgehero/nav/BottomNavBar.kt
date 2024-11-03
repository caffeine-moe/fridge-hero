package moe.caffeine.fridgehero.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(
    navController: NavController,
    navBarItems: List<BottomNavItem>,
    onNavigate: (title: String) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    BottomAppBar(
        modifier = Modifier.systemBarsPadding(),
        contentPadding = PaddingValues(10.dp),
    ) {
        navBarItems.forEachIndexed { index, bottomNavItem ->
            NavigationBarItem(
                modifier = Modifier.fillMaxSize(),
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    navController.popBackStack()
                    navController.navigate(bottomNavItem.title)
                    onNavigate(bottomNavItem.title)
                },
                icon = {
                    Box {
                        Icon(
                            active = selectedIndex == index,
                            activeContent = {
                                Image(
                                    bottomNavItem.selectedIcon,
                                    "${bottomNavItem.title} Button",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                )
                            },
                            inactiveContent = {
                                Image(
                                    bottomNavItem.unselectedIcon,
                                    "${bottomNavItem.title} Button",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        )
                    }
                },
                label = {
                    Text(bottomNavItem.title)
                }
            )
        }
    }
}