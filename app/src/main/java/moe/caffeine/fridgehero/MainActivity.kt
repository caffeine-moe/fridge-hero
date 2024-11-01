package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.outlined.Dining
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import moe.caffeine.fridgehero.fridge.Fridge
import moe.caffeine.fridgehero.home.Home
import moe.caffeine.fridgehero.model.Profile
import moe.caffeine.fridgehero.nav.BottomNavBar
import moe.caffeine.fridgehero.nav.BottomNavGraph
import moe.caffeine.fridgehero.nav.BottomNavItem
import moe.caffeine.fridgehero.oobe.OOBE
import moe.caffeine.fridgehero.recipe.Recipes
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeHeroTheme {
                val profiles = viewModel.realm.fetchAllByType<Profile>()
                profiles.ifEmpty {
                    OOBE(this)
                    return@FridgeHeroTheme
                }
                val navController = rememberNavController()
                var title by remember { mutableStateOf("Home") }
                val profile = profiles.first()
                val navBarItems =
                    listOf(
                        BottomNavItem(
                            "Home",
                            Icons.Filled.Home,
                            Icons.Outlined.Home
                        ) { Home(profile) },
                        BottomNavItem(
                            "Fridge",
                            Icons.Filled.Kitchen,
                            Icons.Outlined.Kitchen
                        ) { Fridge(viewModel) },
                        BottomNavItem(
                            "Recipes",
                            Icons.Filled.Dining,
                            Icons.Outlined.Dining,
                        ) { Recipes() }
                    )
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        bottomBar = {
                            BottomNavBar(navController, navBarItems) { title = it }
                        },
                        topBar = {
                            TopAppBar(
                                modifier = Modifier
                                    .background(Color.Black),
                                title = {
                                    Text(
                                        title,
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                },
                            )
                        }
                    ) { paddingValues ->
                        Column(
                            Modifier.padding(paddingValues)
                        ) {
                            BottomNavGraph(navController, navBarItems)
                        }
                    }
                }
            }
        }
    }
}