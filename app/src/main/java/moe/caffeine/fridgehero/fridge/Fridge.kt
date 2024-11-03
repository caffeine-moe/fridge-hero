package moe.caffeine.fridgehero.fridge

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import moe.caffeine.fridgehero.MainViewModel
import moe.caffeine.fridgehero.scanner.StartScanner


@Composable
fun Fridge(viewModel: MainViewModel) {
    val fridge by viewModel.foodItems.collectAsState()
    var showScanner by remember { mutableStateOf(false) }
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
                        viewModel.removeFromRealm(foodItem)
                    }
                )
            }
        }
    }
    if (showScanner) {
        StartScanner { barcode ->
            showScanner = false
            Toast.makeText(
                context,
                "Processing barcode $barcode, please wait...",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.createFoodItemFromBarcode(barcode) { result ->
                result.fold(
                    onFailure = { throwable ->
                        Toast.makeText(
                            context,
                            "ERROR: ${throwable.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSuccess = { foodItem ->
                        Toast.makeText(
                            context,
                            "Successfully scanned ${foodItem.name}!",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.addToRealm(foodItem)
                    }
                )
            }
        }
    }
}