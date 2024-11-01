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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import moe.caffeine.fridgehero.MainViewModel


@Composable
fun Fridge(viewModel: MainViewModel) {
    val fridge by viewModel.foodItems.collectAsState()
    Scaffold(
        floatingActionButton = {
            FABMenu {
                //todo: implement addition
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            items(fridge) { foodItem ->
                var isRemoved by remember { mutableStateOf(false) }
                val state = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            isRemoved = true
                            true
                        } else false
                    },
                    positionalThreshold = { it }
                )

                LaunchedEffect(isRemoved) {
                    delay(500)
                    if (isRemoved) {
                        //todo: implement removal
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier.animateItem(
                        tween(500)
                    ),
                    visible = !isRemoved,
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
                            ItemCard(foodItem.name)
                        }
                    }
                }
            }
        }
    }
}