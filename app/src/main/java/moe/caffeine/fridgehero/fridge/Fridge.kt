package moe.caffeine.fridgehero.fridge

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun Fridge() {
    val foodItems: MutableList<String> = mutableListOf()
    (0..10).map { foodItems += "ONION" }
    Column {
        foodItems.forEach { foodItem ->
            ItemCard(foodItem)
        }
    }
}