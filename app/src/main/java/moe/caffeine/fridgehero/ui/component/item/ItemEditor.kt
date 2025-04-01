package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.ImageCard
import moe.caffeine.fridgehero.ui.component.TrailingEditIcon
import moe.caffeine.fridgehero.ui.component.itemsheet.ScannerFloatingActionButton

@Composable
fun ItemEditor(
  foodItem: FoodItem,
  readOnly: Boolean = false,
  compact: Boolean = false,
  onScannerRequest: suspend () -> Unit = {},
  onImageRequest: suspend () -> Unit = {},
  onValueChanged: (FoodItem) -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  var interactedWithTitle by rememberSaveable { mutableStateOf(false) }
  var editingBarcode by remember { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current
  val barcodeFocusRequester = remember { FocusRequester() }
  var categoryEditorExpanded by rememberSaveable { mutableStateOf(false) }

  //Title Editor
  ElevatedCard {
    Box(Modifier.padding(8.dp)) {
      Column {
        OutlinedTextField(
          isError = foodItem.name.isEmpty() && interactedWithTitle,
          label = { Text("Name") },
          modifier = Modifier
            .fillMaxWidth(),
          value = foodItem.name,
          readOnly = readOnly,
          onValueChange = {
            interactedWithTitle = true
            onValueChanged(foodItem.copy(name = it))
          },
          textStyle = MaterialTheme.typography.titleMedium,
          trailingIcon = { TrailingEditIcon(!readOnly) },
          placeholder = { Text("Name") },
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
      }
    }
  }
  Spacer(Modifier.size(8.dp))
  //Barcode n image n stuff
  ElevatedCard {
    Box(
      Modifier
        .padding(8.dp)
    ) {
      Column(
        Modifier
          .wrapContentSize()
      ) {
        Row(
          Modifier
            .fillMaxWidth()
        ) {
          BoxWithConstraints(Modifier.fillMaxWidth(0.5f)) {
            val targetWidth = if (!compact) maxWidth.coerceAtMost(800.dp) else 80.dp
            val targetHeight = if (!compact) maxWidth.coerceAtMost(800.dp) else 80.dp

            val animatedWidth by animateDpAsState(
              targetValue = targetWidth,
              animationSpec = tween(durationMillis = 500)
            )
            val animatedHeight by animateDpAsState(
              targetValue = targetHeight,
              animationSpec = tween(durationMillis = 500)
            )
            Column(
              Modifier
                .fillMaxSize(),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Center
            ) {
              Box {
                ImageCard(
                  modifier = Modifier
                    .align(Alignment.Center)
                    .size(animatedWidth, animatedHeight)
                    .aspectRatio(1f)
                    .clickable {

                      scope.launch {
/*                        onImageRequest().onSuccess {
                          onValueChanged(foodItem.copy(imageByteArray = it))
                        }*/
                      }
                    },
                  foodItem.imageByteArray
                )
                androidx.compose.animation.AnimatedVisibility(
                  visible = !compact && foodItem.barcode.isNotBlank(),
                  modifier = Modifier.align(Alignment.BottomEnd),
                  enter = expandVertically(tween(500, 500)),
                  exit = fadeOut(
                    tween(
                      250
                    )
                  ) + shrinkVertically(tween(250), shrinkTowards = Alignment.Bottom)
                ) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.BottomEnd)
                      .padding(top = 8.dp)
                  ) {
                    Image(
                      modifier = Modifier.fillMaxWidth(0.4f),
                      painter = foodItem.nutriScorePainter(),
                      contentDescription = "NutriScore"
                    )
                  }
                }
              }
            }
          }
          Spacer(Modifier.size(8.dp))
          Box(
            Modifier
              .fillMaxSize()
          ) {
            Column(
              Modifier
                .fillMaxHeight()
            ) {
              AnimatedVisibility(editingBarcode) {
                var isFocused by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                  barcodeFocusRequester.requestFocus()
                }
                LaunchedEffect(isFocused) {
                  if (!isFocused) {
                    editingBarcode = false
                  }
                }
                OutlinedTextField(
                  value = foodItem.barcode,
                  label = { Text("Barcode") },
                  onValueChange = {
                    onValueChanged(foodItem.copy(barcode = it))
                  },
                  singleLine = true,
                  modifier = Modifier
                    .focusRequester(barcodeFocusRequester)
                    .onFocusChanged { focusState ->
                      isFocused = focusState.isFocused
                    },
                  keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                  ),
                  keyboardActions = KeyboardActions(
                    onDone = {
                      focusManager.clearFocus()
                      editingBarcode = false
                    }
                  ),
                  trailingIcon = { TrailingEditIcon(!readOnly) }
                )
              }
              AnimatedVisibility(!editingBarcode) {
                Box(
                  Modifier.clickable {
                    editingBarcode = true
                  }
                ) {
                  Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      maxLines = 1,
                      text = foodItem.barcode.ifBlank { "Barcode" },
                      overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.size(8.dp))
                    TrailingEditIcon(!readOnly)
                  }
                }
              }
              AnimatedVisibility(
                visible = compact && !foodItem.isSaved,
                enter = slideInHorizontally(tween(500), initialOffsetX = { 2 * it }),
                exit = slideOutHorizontally(
                  tween(500, delayMillis = 125),
                  targetOffsetX = { 2 * it }) + fadeOut(
                  tween(
                    500, delayMillis = 125
                  )
                )
              ) {
                Spacer(Modifier.size(8.dp))
                ScannerFloatingActionButton(onClick = {
                  scope.launch { onScannerRequest() }
                })
              }
            }
          }
        }
      }
      androidx.compose.animation.AnimatedVisibility(
        visible = !compact && foodItem.barcode.isNotBlank(),
        modifier = Modifier.align(Alignment.BottomEnd),
        enter = expandVertically(tween(500, 500)),
        exit = fadeOut(
          tween(
            250
          )
        ) + shrinkVertically(tween(250), shrinkTowards = Alignment.Bottom)
      ) {
        Row(verticalAlignment = Alignment.Bottom) {
          Spacer(Modifier.size(8.dp))
          Box {
            Card(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 8.dp)
            ) {
              Image(
                modifier = Modifier
                  .width(80.dp)
                  .aspectRatio(1f),
                painter = foodItem.novaGroupPainter(),
                contentDescription = "Nova Group"
              )
            }
          }
        }
      }
    }//
  }
  Spacer(Modifier.size(8.dp))
  //Tags editor
  ElevatedCard(
    Modifier.fillMaxWidth()
  ) {
    Box(
      Modifier
        .padding(8.dp)
    ) {
      Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
          Box(Modifier
            .fillMaxWidth()
            .clickable {
              categoryEditorExpanded = !categoryEditorExpanded
            }) {
            Text(
              modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp),
              text = "Tags",
              style = MaterialTheme.typography.titleMedium
            )
            TextButton(
              modifier = Modifier.align(Alignment.CenterEnd),
              onClick = {
                categoryEditorExpanded = !categoryEditorExpanded
              }
            ) {
              val rotationState by animateFloatAsState(if (categoryEditorExpanded) 180f else 0f)
              Icon(
                modifier = Modifier.rotate(rotationState),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand Tags"
              )
            }
          }
        }
        AnimatedVisibility(
          visible = categoryEditorExpanded,
          enter = expandVertically(
            animationSpec = tween(500)
          ) + fadeIn(tween(500)),
          exit = shrinkVertically(
            animationSpec = tween(500)
          ) + fadeOut(tween(500))
        ) {
          CategoryEditor(
            categories = foodItem.categories,
            onListChanged = {
              onValueChanged(foodItem.copy(categories = it))
            }
          )
        }
      }
    }
  }
}
