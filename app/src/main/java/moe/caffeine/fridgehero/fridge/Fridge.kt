package moe.caffeine.fridgehero.fridge

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
import moe.caffeine.fridgehero.MainViewModel
import moe.caffeine.fridgehero.scanner.StartScanner


@Composable
fun Fridge(viewModel: MainViewModel) {
    val fridge by viewModel.foodItems.collectAsState()
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
                        viewModel.removeFromRealm(foodItem, 450)
                    }
                )
            }
        }
    }
    when {
        showScanner -> {
            StartScanner { barcode ->
                showScanner = false
                handleBarcode(barcode, viewModel, context) { }
            }
        }
    }
}