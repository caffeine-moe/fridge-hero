package moe.caffeine.fridgehero.fridge

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.model.FoodItem
import moe.caffeine.fridgehero.scanner.StartScanner


@Composable
fun Fridge(
    foodItems: StateFlow<List<FoodItem>>,
    createFoodItemFromBarcode: (String) -> Unit,
    //createCustomFoodItem: () -> Unit,
    removeFoodItem: (FoodItem, Long) -> Unit,
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
                    TODO()
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
                SwipeToDismissBox(
                    itemCard = {
                        ItemCard(foodItem.name)
                    },
                    onDismiss = {
                        removeFoodItem(foodItem, 450)
                    }
                )
            }
        }
    }
    when {
        showScanner -> {
            StartScanner { barcode ->
                showScanner = false
                Toast.makeText(
                    context,
                    "Processing barcode $barcode, please wait...",
                    Toast.LENGTH_SHORT
                ).show()
                createFoodItemFromBarcode(barcode)
            }
        }
    }
}