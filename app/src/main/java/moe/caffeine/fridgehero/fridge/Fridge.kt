package moe.caffeine.fridgehero.fridge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Fridge() {
    val foodItems: MutableList<String> = mutableListOf()
    (0..10).map { foodItems += "ONION" }
    Column {
        foodItems.forEach { foodItem ->
            Card(
                Modifier.fillMaxWidth()
            ) {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = foodItem)
            }
        }
    }
}