package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.model.FoodItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSheet(
  modifier: Modifier = Modifier,
  prefill: FoodItem,
  expiryEditorExpandedInitial: Boolean,
  onBarcodeFromScanner: suspend () -> Result<String>,
  onFoodItemFromBarcode: suspend (String) -> Result<FoodItem>,
  onExpiryDateRequest: suspend () -> Result<Long>,
  bottomSheetScaffoldState: BottomSheetScaffoldState,
  onComplete: suspend (Result<FoodItem>) -> Unit,
  onHardRemove: (FoodItem) -> Unit,
) {
  val scrollState = rememberScrollState()
  val scope = rememberCoroutineScope()
  val latestPrefill: FoodItem by rememberUpdatedState(newValue = prefill)
  var editableFoodItem by rememberSaveable { mutableStateOf(prefill) }
  var saved by remember(editableFoodItem) { mutableStateOf(editableFoodItem.realmObjectId.isNotBlank()) }
  var expiryEditorExpanded by rememberSaveable { mutableStateOf(expiryEditorExpandedInitial) }
  BottomSheetScaffold(
    scaffoldState = bottomSheetScaffoldState,
    sheetPeekHeight = 240.dp,
    modifier = modifier,
    sheetContent = {
      Row {
        Box(
          Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          TextButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = {
              scope.launch { onComplete(Result.failure(Throwable("Dismissed"))) }
            }) {
            Text("Dismiss")
          }
          TextButton(
            onClick = {
              editableFoodItem = prefill
            }) {
            Text("Reset")
          }
          TextButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = {
              scope.launch {
                onComplete(Result.success(editableFoodItem))
                editableFoodItem = FoodItem()
                saved = false
              }
            }
          ) {
            Text("Save")
          }
        }
      }
      Surface(modifier = Modifier.verticalScroll(scrollState)) {
        Column {
          Card(
            modifier = Modifier
              .padding(8.dp)
              .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
                RoundedCornerShape(16.dp)
              )
          ) {
            Column {
              Text(
                modifier = Modifier.padding(8.dp),
                text = "Item Editor",
                style = MaterialTheme.typography.labelLarge
              )
              ItemEditor(
                editableFoodItem,
                onScannerRequest = { replaceAll ->
                  scope.launch {
                    val barcode =
                      onBarcodeFromScanner().getOrNull() ?: return@launch
                    editableFoodItem = editableFoodItem.copy(barcode = barcode)
                    if (!replaceAll)
                      return@launch
                    onFoodItemFromBarcode(barcode).onSuccess {
                      editableFoodItem = it
                    }
                  }
                },
                onFieldChanged = { editedFoodItem ->
                  editableFoodItem = editedFoodItem
                }
              )
            }
          }
          Card(
            modifier = Modifier
              .padding(8.dp)
              .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
                RoundedCornerShape(16.dp)
              )
          ) {
            Column {
              Text(
                modifier = Modifier.padding(8.dp),
                text = "Expiry Editor",
                style = MaterialTheme.typography.labelLarge
              )
              ExpiryEditor(
                editableFoodItem.expiryDates,
                onRequestExpiry = onExpiryDateRequest,
                small = !expiryEditorExpanded,
                onShowMore = {
                  expiryEditorExpanded = !expiryEditorExpanded
                },
                onListChanged = { newDates ->
                  editableFoodItem =
                    editableFoodItem.copy(expiryDates = newDates)
                }
              )
            }
          }
          Card(
            modifier = Modifier
              .padding(8.dp)
              .border(
                BorderStroke(2.dp, Color.Red),
                RoundedCornerShape(16.dp)
              )
          ) {
            Column(Modifier.fillMaxWidth()) {
              Text(
                modifier = Modifier.padding(8.dp),
                text = "Danger Zone",
                style = MaterialTheme.typography.labelLarge
              )
            }
            Button(
              enabled = saved,
              modifier = Modifier
                .align(Alignment.Start)
                .padding(8.dp),
              colors = ButtonColors(
                containerColor = Color.Red,
                contentColor = Color.Black,
                disabledContentColor = Color.LightGray,
                disabledContainerColor = Color.DarkGray
              ),
              onClick = {
                scope.launch {
                  onHardRemove(editableFoodItem)
                  saved = false
                }
              }
            ) {
              Text(
                "PERMANENTLY DELETE"
              )
            }
          }
          Spacer(Modifier.size(20.dp))
        }
      }
    }
  ) {}
  LaunchedEffect(latestPrefill) {
    editableFoodItem = latestPrefill
  }
}
