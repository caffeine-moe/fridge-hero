package moe.caffeine.fridgehero.ui

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.component.MainScaffold
import moe.caffeine.fridgehero.ui.component.overlay.DatePickerOverlay
import moe.caffeine.fridgehero.ui.component.overlay.FullScreenOverlay
import moe.caffeine.fridgehero.ui.component.overlay.ItemSheetOverlay
import moe.caffeine.fridgehero.ui.component.overlay.ScannerOverlay
import moe.caffeine.fridgehero.ui.nav.Screen

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

  // overlay states
  var destination by rememberSaveable { mutableStateOf("Home") }
  var fullScreenItem by remember { mutableStateOf<FoodItem?>(null) }
  var datePickerRequest by remember { mutableStateOf<Event.RequestDateFromPicker?>(null) }
  var bottomSheetRequest by remember { mutableStateOf<Event.RequestItemSheet?>(null) }
  var barcodeScanRequest by remember { mutableStateOf<Event.RequestBarcodeFromScanner?>(null) }
  var appBarPadding by remember { mutableStateOf(PaddingValues()) }

  MainScaffold(
    navController = navController,
    destination = destination,
    navBarItems = navBarItems,
    onPaddingCreated = {
      appBarPadding = it
    },
    onDestinationChange = {
      destination = it
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
    onItemSheetRequest = { bottomSheetRequest = it },
    onFullScreenRequest = { fullScreenItem = it }
  )

  FullScreenOverlay(
    fullScreenItem = fullScreenItem,
    onDismiss = { fullScreenItem = null }
  )

  DatePickerOverlay(
    datePickerRequest = datePickerRequest,
    onDismiss = { datePickerRequest = null }
  )

  ScannerOverlay(
    barcodeScanRequest = barcodeScanRequest,
    onDismiss = { barcodeScanRequest = null }
  )

  ItemSheetOverlay(
    itemSheetRequest = bottomSheetRequest,
    onDismiss = {
      bottomSheetRequest?.result?.complete(Result.failure(Throwable("Dismissed")))
      bottomSheetRequest = null
    },
    onBarcodeFromScanner = {
      val completableBarcode: CompletableDeferred<Result<String>> =
        CompletableDeferred()
      emitEvent(Event.RequestBarcodeFromScanner(completableBarcode))
      completableBarcode.await()
    },
    onFoodItemFromBarcodeFromScanner = {
      val completableBarcode: CompletableDeferred<Result<String>> =
        CompletableDeferred()
      val completableFoodItem: CompletableDeferred<Result<FoodItem>> =
        CompletableDeferred()
      emitEvent(Event.RequestBarcodeFromScanner(completableBarcode))
      completableBarcode.await().fold(
        onSuccess = { barcode ->
          emitEvent(
            Event.RequestFoodItemFromBarcode(
              barcode,
              completableFoodItem
            )
          )
        },
        onFailure = {
          return@ItemSheetOverlay Result.failure(it)
        }
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
      emitEvent(
        Event.UpsertFoodItem(
          editedFoodItem
        )
      )
      bottomSheetRequest = null
    }
  )
}
