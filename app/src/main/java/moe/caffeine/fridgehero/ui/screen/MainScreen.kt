package moe.caffeine.fridgehero.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
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
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.EventHandler
import moe.caffeine.fridgehero.ui.overlay.DatePickerOverlay
import moe.caffeine.fridgehero.ui.overlay.FullScreenOverlay
import moe.caffeine.fridgehero.ui.overlay.ItemSheetOverlay
import moe.caffeine.fridgehero.ui.overlay.ScannerOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  profile: Profile,
  navBarItems: List<Screen>,
  foodItems: StateFlow<List<FoodItem>>,
  eventFlow: SharedFlow<Event>,
  emitEvent: (Event) -> Unit,
) {
  val navController = rememberNavController()
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  // mutable overlay states
  var fullScreenItem by rememberSaveable { mutableStateOf<FoodItem?>(null) }
  var datePickerRequest by remember { mutableStateOf<Event.RequestDateFromPicker?>(null) }

  var bottomSheetRequest by remember { mutableStateOf<Event.RequestItemSheet?>(null) }
  val standardBottomSheetState = rememberStandardBottomSheetState(
    skipHiddenState = false,
    initialValue = SheetValue.Hidden,
    confirmValueChange = { true }
  )

  var barcodeScanRequest by remember { mutableStateOf<Event.RequestBarcodeFromScanner?>(null) }
  var appBarPadding by remember { mutableStateOf(PaddingValues()) }

  LaunchedEffect(barcodeScanRequest) {
    if (barcodeScanRequest != null && standardBottomSheetState.isVisible)
      standardBottomSheetState.hide()
    else if (!standardBottomSheetState.isVisible)
      standardBottomSheetState.expand()
  }

  MainScaffold(
    navController = navController,
    navBarItems = navBarItems,
    onPaddingCreated = {
      appBarPadding = it
    },
    profile = profile,
    foodItems = foodItems,
    emitEvent = emitEvent
  )

  EventHandler(
    eventFlow,
    onDisplayToast = { message ->
      Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
      ).show()
    },
    onBarcodeRequest = { barcodeScanRequest = it },
    onDateRequest = { datePickerRequest = it },
    onItemSheetRequest = {
      bottomSheetRequest = it
      scope.launch { standardBottomSheetState.expand() }
    },
    onFullScreenRequest = { fullScreenItem = it }
  )

  FullScreenOverlay(
    fullScreenItem = fullScreenItem,
    onDismiss = { fullScreenItem = null }
  )

  DatePickerOverlay(
    state = rememberDatePickerState(initialDisplayMode = DisplayMode.Input),
    datePickerRequest = datePickerRequest,
    onDismiss = { datePickerRequest = null }
  )

  ScannerOverlay(
    barcodeScanRequest = barcodeScanRequest,
    onDismiss = { barcodeScanRequest = null }
  )

  ItemSheetOverlay(
    state = standardBottomSheetState,
    request = bottomSheetRequest,
    onBarcodeFromScanner = {
      val completableBarcode: CompletableDeferred<Result<String>> =
        CompletableDeferred()
      emitEvent(Event.RequestBarcodeFromScanner(completableBarcode))
      completableBarcode.await()
    },
    onFoodItemFromBarcode = { barcode ->
      val completableFoodItem: CompletableDeferred<Result<FoodItem>> =
        CompletableDeferred()
      emitEvent(
        Event.RequestFoodItemFromBarcode(
          barcode,
          completableFoodItem
        )
      )
      completableFoodItem.await()
    },
    onExpiryDateRequest = {
      val completableExpiryDate: CompletableDeferred<Result<Long>> = CompletableDeferred()
      emitEvent(
        Event.RequestDateFromPicker(
          completableExpiryDate
        )
      )
      completableExpiryDate.await()
    },
    onComplete = { editedFoodItem ->
      val completableFoodItem: CompletableDeferred<Result<FoodItem>> =
        CompletableDeferred()
      emitEvent(
        Event.UpsertFoodItem(
          editedFoodItem,
          completableFoodItem
        )
      )
      completableFoodItem.await()
    },
    onDismiss = {
      scope.launch {
        standardBottomSheetState.hide()
        bottomSheetRequest = null
      }
    },
    onHardRemove = {
      emitEvent(
        Event.HardRemoveFoodItem(it)
      )
    }
  )
}
