package moe.caffeine.fridgehero.screen.myfridge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import moe.caffeine.fridgehero.screen.scanner.LaunchScanner

var fridgeContents : List<FoodItem> = listOf()

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyFridge(navHostController : NavHostController, barHeight : Dp) {
    var showScanner by remember { mutableStateOf(false) }
    var fridge by remember { mutableStateOf(fridgeContents) }
    LaunchedEffect(key1 = fridgeContents) {
        fridge = fridgeContents
    }

    Scaffold(
        modifier = Modifier.padding(bottom = barHeight),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showScanner = true
                }
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding
        ) {
            items(fridge) { item ->

                var isRemoved by remember { mutableStateOf(false) }

                val state = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            isRemoved = true
                            true
                        } else false

                    },
                )

                LaunchedEffect(key1 = isRemoved) {
                    if (isRemoved) {
                        delay(500)
                        fridgeContents -= item
                    }
                }

                AnimatedVisibility(
                    visible = !isRemoved,
                    exit = shrinkVertically(
                        animationSpec = tween(500),
                        shrinkTowards = Alignment.Top
                    ) + fadeOut()
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
                        ItemCard(item)
                    }

                }
            }
        }
    }
    if (showScanner) {
        LaunchScanner(navHostController)
    }
}

@Composable
fun ItemCard(item : FoodItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            val image : Painter = rememberAsyncImagePainter(item.image)
            Image(
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                painter = image,
                alignment = Alignment.CenterStart,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                val name =
                    remember { ("${item.brand} - ".takeIf { !item.name.contains(item.brand) } ?: "") + item.name }

                Text(
                    text = name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Bold,
                    style = typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.barcode,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.surface,
                    style = typography.bodySmall
                )
            }
        }
    }
}