package moe.caffeine.fridgehero

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
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
                    OOBE { firstName, lastName ->
                        viewModel.createProfile(firstName, lastName)
                    }
                    return@FridgeHeroTheme
                }
                val navController = rememberNavController()
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
                        ) {
                            Fridge(
                                viewModel.fridgeItems,
                                addFoodItemFromBarcode = { barcode ->
                                    viewModel.addFoodItemFromBarcode(barcode)
                                },
                                removeFoodItem = { foodItem ->
                                    viewModel.updateRemovedState(foodItem)
                                }
                            )
                        },
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
                            BottomNavBar(navController, navBarItems) { viewModel.destination = it }
                        },
                        topBar = {
                            TopAppBar(
                                modifier = Modifier
                                    .background(Color.Black),
                                title = {
                                    Text(
                                        viewModel.destination,
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                },
                            )
                        }
                    ) { paddingValues ->
                        Column(
                            Modifier.padding(paddingValues)
                        ) {
                            val context = LocalContext.current
                            LaunchedEffect(Unit) {
                                viewModel.toastMessage.collectLatest { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            BottomNavGraph(navController, viewModel.destination, navBarItems)
                        }
                    }
                }
            }
        }
    }
}