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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.component.itemsheet.ScannerFloatingActionButton

@Composable
fun ItemEditor(
  foodItem: FoodItem,
  expiryEditorExpandedInitial: Boolean = false,
  imageSectionExpanded: Boolean = true,
  readOnly: Boolean = false,
  compact: Boolean = false,
  onScannerRequest: suspend () -> Unit = {},
  onDatePickerRequest: suspend () -> (Result<Long>) = { Result.failure(Throwable("")) },
  onValueChanged: (FoodItem) -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  var interactedWithTitle by rememberSaveable { mutableStateOf(false) }
  var editingBarcode by remember { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current
  val barcodeFocusRequester = remember { FocusRequester() }
  var categoryEditorExpanded by rememberSaveable { mutableStateOf(false) }
  var expiryEditorExpanded by rememberSaveable { mutableStateOf(expiryEditorExpandedInitial) }

  val trailingEditIcon = @Composable {
    if (!readOnly) {
      Icon(
        Icons.Outlined.Edit,
        null,
        Modifier.size(16.dp)
      )
    }
  }
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
          trailingIcon = trailingEditIcon,
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
          BoxWithConstraints(Modifier.weight(0.5f)) {
            val targetWidth = if (imageSectionExpanded) maxWidth.coerceAtMost(800.dp) else 80.dp
            val targetHeight = if (imageSectionExpanded) maxWidth.coerceAtMost(800.dp) else 80.dp

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
              horizontalAlignment = Alignment.Start
            ) {
              Box {
                ItemImageCard(
                  modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(animatedWidth, animatedHeight)
                    .aspectRatio(1f),
                  foodItem
                )
              }
            }
          }
          Spacer(Modifier.size(8.dp))
          Box(
            Modifier
              .weight(0.5f)
              .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
          ) {
            Column {
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
                  trailingIcon = trailingEditIcon
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
                    trailingEditIcon()
                  }
                }
              }
              Spacer(Modifier.size(8.dp))
              AnimatedVisibility(
                visible = !compact && !foodItem.isSaved,
                enter = slideInHorizontally(tween(500), initialOffsetX = { 2 * it }),
                exit = slideOutHorizontally(
                  tween(500, delayMillis = 125),
                  targetOffsetX = { 2 * it }) + fadeOut(
                  tween(
                    500, delayMillis = 125
                  )
                )
              ) {
                ScannerFloatingActionButton(onClick = {
                  scope.launch { onScannerRequest() }
                })
              }
            }
          }
        }
      }
    }
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
  Spacer(Modifier.size(8.dp))
  //EXPIRY EDITOR
  ElevatedCard {
    ExpiryEditor(
      foodItem.expiryDates,
      onRequestExpiry = onDatePickerRequest,
      small = !expiryEditorExpanded,
      onShowMore = {
        expiryEditorExpanded = !expiryEditorExpanded
      },
      onListChanged = {
        onValueChanged(foodItem.copy(expiryDates = it))
      }
    )
  }
}
