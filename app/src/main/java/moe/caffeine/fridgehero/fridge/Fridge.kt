package moe.caffeine.fridgehero.fridge

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.model.FoodItem


@Composable
fun Fridge(
    foodItems: StateFlow<List<FoodItem>>,
    addFoodItemFromBarcode: (String) -> Unit,
    //createCustomFoodItem: () -> Unit,
    removeFoodItem: (FoodItem) -> Unit,
) {
    val fridge by foodItems.collectAsState()
    var showScanner by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FABMenu(
                scanner = {
                    showScanner = true
                },
                custom = {
                    //
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            items(
                fridge,
                key = { it._id.toHexString() }
            ) { foodItem ->
                val state = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            removeFoodItem(foodItem)
                        }
                        true
                    },
                    positionalThreshold = { it }
                )

                AnimatedVisibility(
                    modifier = Modifier.animateItem(
                        tween(500)
                    ),
                    visible = !foodItem.isRemoved,
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                ) {
                    FridgeSwipeToDismissBox(state) {
                        ItemCard(foodItem.name)
                    }
                }
            }
        }
    }
    when {
        showScanner -> {
            //Scanner { barcode ->
            val barcode = "7622201726485"
            showScanner = false
            Toast.makeText(
                context,
                "Processing barcode $barcode, please wait...",
                Toast.LENGTH_SHORT
            ).show()
            addFoodItemFromBarcode(barcode)
            // }
        }
    }
}