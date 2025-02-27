package moe.caffeine.fridgehero.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.EventHandler
import moe.caffeine.fridgehero.ui.component.item.ItemSheet
import moe.caffeine.fridgehero.ui.overlay.DatePickerOverlay
import moe.caffeine.fridgehero.ui.overlay.FullScreenItemOverlay
import moe.caffeine.fridgehero.ui.overlay.ScannerOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  eventFlow: SharedFlow<Event>,
  emitEvent: (Event) -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val screens: List<Screen> =
    listOf(
      Screen.Home,
      Screen.Fridge,
      Screen.Recipes
    )

  // states for UI components
  var fullScreenItem by rememberSaveable { mutableStateOf<FoodItem?>(null) }

  val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
  var datePickerRequest by remember { mutableStateOf<Event.RequestDateFromPicker?>(null) }

  var itemBottomSheetRequest by remember { mutableStateOf(Event.RequestItemSheet()) }
  val itemStandardBottomSheetState = rememberStandardBottomSheetState(
    skipHiddenState = false,
    initialValue = SheetValue.Hidden,
    confirmValueChange = { true }
  )
  val itemBottomSheetScaffoldState = rememberBottomSheetScaffoldState(itemStandardBottomSheetState)

  var barcodeScanRequest by remember { mutableStateOf<Event.RequestBarcodeFromScanner?>(null) }
  LaunchedEffect(barcodeScanRequest) {
    if (barcodeScanRequest != null && itemStandardBottomSheetState.isVisible)
      itemStandardBottomSheetState.hide()
  }

  var appBarPadding by remember { mutableStateOf(PaddingValues()) }

  EventHandler(
    eventFlow,
    onDisplayToast = { message ->
      Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
      ).show()
    },
    onBarcodeRequest = {
      barcodeScanRequest = it
    },
    onDateRequest = { datePickerRequest = it },
    onItemSheetRequest = {
      itemBottomSheetRequest = it
      scope.launch { itemStandardBottomSheetState.expand() }
    },
    onFullScreenRequest = { fullScreenItem = it }
  )

  MainScaffold(
    onPaddingCreated = { appBarPadding = it },
    screens = screens,
    profile = profile,
    foodItems = foodItems,
    emitEvent = emitEvent
  )

  FullScreenItemOverlay(
    fullScreenItem = fullScreenItem,
    onDismiss = { fullScreenItem = null }
  )

  DatePickerOverlay(
    visible = datePickerRequest != null,
    state = datePickerState,
    onComplete = { result ->
      datePickerRequest?.let { request ->
        datePickerRequest = null
        request.result.complete(result)
      }
    }
  )

  ScannerOverlay(
    visible = barcodeScanRequest != null,
    onComplete = { result ->
      barcodeScanRequest?.result?.complete(result)
      barcodeScanRequest = null
    },
  )

  ItemSheet(
    bottomSheetScaffoldState = itemBottomSheetScaffoldState,
    prefill = itemBottomSheetRequest.prefill,
    expiryEditorExpandedInitial = itemBottomSheetRequest.expiryEditorExpanded,
    onComplete = { editedFoodItem ->
      itemBottomSheetRequest.result.complete(editedFoodItem)
      itemStandardBottomSheetState.hide()
      itemBottomSheetRequest = Event.RequestItemSheet()
    },
    onBarcodeFromScanner = {
      itemStandardBottomSheetState.hide()
      Event.RequestBarcodeFromScanner()
        .apply(emitEvent).result.await()
        .also { itemStandardBottomSheetState.expand() }
    },
    onFoodItemFromBarcode = { barcode ->
      Event.RequestFoodItemFromBarcode(barcode).apply(emitEvent).result.await()
    },
    onExpiryDateRequest = {
      Event.RequestDateFromPicker().apply(emitEvent).result.await()
    },
    onHardRemove = { itemToRemove ->
      Event.HardRemoveFoodItem(itemToRemove).apply(emitEvent)
    }
  )
}
