package moe.caffeine.fridgehero.fridge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SwipeToDismissBox(
    itemCard: @Composable () -> Unit,
    onDismiss: () -> Unit
) {
    val isRemoved = remember { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                isRemoved.value = true
                onDismiss()
                true
            } else false
        },
        positionalThreshold = { it }
    )

    AnimatedVisibility(
        visible = !isRemoved.value,
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(500)
        ) + fadeOut(animationSpec = tween(500))
    ) {
        SwipeToDismissBox(
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
}