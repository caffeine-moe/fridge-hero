package moe.caffeine.fridgehero.fridge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.model.FoodItem

@Composable
fun FABMenu(
    onClick: (item: FoodItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
    ) {
        AnimatedVisibility(expanded) {
            Row {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Filled.QrCodeScanner, "By Barcode")
                }
            }
        }
        AnimatedVisibility(expanded) {
            Row {
                FloatingActionButton(onClick = {
                    onClick(
                        FoodItem().apply {
                            name = "onion"
                        }
                    )
                }) {
                    Icon(Icons.Filled.Create, "By Custom")
                }
            }
        }
        FloatingActionButton(onClick = {
            expanded = !expanded
        }) {
            Icon(Icons.Filled.Add, "Add Item Button")
        }
    }
}