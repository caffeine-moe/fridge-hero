package moe.caffeine.fridgehero.fridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FridgeSwipeToDismissBox(
    state: SwipeToDismissBoxState,
    itemCard: @Composable () -> Unit
) {
    androidx.compose.material3.SwipeToDismissBox(
        state = state,
        backgroundContent = {
            val colour = when (state.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colour),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    contentDescription = ""
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Row {
            itemCard()
        }
    }
}