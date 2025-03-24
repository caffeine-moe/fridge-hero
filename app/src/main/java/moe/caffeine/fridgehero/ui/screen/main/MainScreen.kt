package moe.caffeine.fridgehero.ui.screen.main

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
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
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.overlay.DatePickerModalOverlay
import moe.caffeine.fridgehero.ui.overlay.RecipeEditorOverlay
import moe.caffeine.fridgehero.ui.overlay.ScannerOverlay
import moe.caffeine.fridgehero.ui.overlay.item.FullScreenItemOverlay
import moe.caffeine.fridgehero.ui.overlay.item.ItemSearchOverlay
import moe.caffeine.fridgehero.ui.overlay.item.ItemSheetOverlay
import moe.caffeine.fridgehero.ui.screen.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  recipes: StateFlow<List<Recipe>>,
  eventFlow: SharedFlow<Event>,
  emitEvent: (Event) -> Unit,
) {
  //constant values
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val screens: List<Screen> =
    listOf(
      Screen.Home,
      Screen.Fridge,
      Screen.Recipes
    )

  // states for UI components

  //recipe editor
  var showRecipeEditor by rememberSaveable { mutableStateOf(false) }
  var recipeEditorRequest by remember { mutableStateOf(Event.RequestRecipeEditor()) }

  //item search overlay
  var showItemSearch by rememberSaveable { mutableStateOf(false) }
  var itemSearchRequest by remember { mutableStateOf(Event.RequestItemFromSearch()) }

  //fullscreen item
  var fullScreenItem by rememberSaveable { mutableStateOf<FoodItem?>(null) }

  //date picker
  var showDatePicker by rememberSaveable { mutableStateOf(false) }
  var datePickerRequest by remember { mutableStateOf(Event.RequestDateFromPicker()) }

  //item bottom sheet
  var itemBottomSheetRequest by remember { mutableStateOf(Event.RequestItemSheet()) }
  val itemBottomSheetState = rememberStandardBottomSheetState(
    skipHiddenState = false,
    initialValue = SheetValue.Hidden,
    confirmValueChange = { true }
  )

  //barcode scanner
  var barcodeScanRequest by remember { mutableStateOf<Event.RequestBarcodeFromScanner?>(null) }
  LaunchedEffect(barcodeScanRequest) {
    if (barcodeScanRequest != null && itemBottomSheetState.isVisible)
      itemBottomSheetState.hide()
  }

  // collects events from the event flow and calls their respective lambdas
  // (cleaner than a massive when block being here imo)
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
    onDateRequest = { datePickerRequest = it;showDatePicker = true },
    onItemSheetRequest = {
      itemBottomSheetRequest = it
      scope.launch { itemBottomSheetState.expand() }
    },
    onFullScreenRequest = { fullScreenItem = it },
    onRecipeEditorRequest = {
      recipeEditorRequest = it
      showRecipeEditor = true
    },
    onItemSearchRequest = {
      itemSearchRequest = it
      showItemSearch = true
    }
  )

  // Hosts main UI components (FAB, Bars, screens)
  MainScaffold(
    screens = screens,
    profile = profile,
    foodItems = foodItems,
    recipes = recipes,
    emitEvent = emitEvent
  )

  // Composables that overlay the main screen

  RecipeEditorOverlay(
    visible = showRecipeEditor,
    prefill = recipeEditorRequest.prefill,
    emitEvent = emitEvent,
    onComplete = {
      showRecipeEditor = false
      recipeEditorRequest.result.complete(it)
    }
  )

  FullScreenItemOverlay(
    fullScreenItem = fullScreenItem,
    onDismiss = { fullScreenItem = null }
  )

  DatePickerModalOverlay(
    visible = showDatePicker,
    onComplete = { result ->
      datePickerRequest.result.complete(result)
      showDatePicker = false
      datePickerRequest = Event.RequestDateFromPicker()
    }
  )

  ScannerOverlay(
    visible = barcodeScanRequest != null,
    onComplete = { result ->
      barcodeScanRequest?.result?.complete(result)
      barcodeScanRequest = null
    },
  )

  // This is more like a screen, but it overlays the main screen so therefore overlay.
  ItemSheetOverlay(
    sheetState = itemBottomSheetState,
    prefill = itemBottomSheetRequest.prefill,
    emitEvent = emitEvent,
    expiryEditorExpandedInitial = itemBottomSheetRequest.expiryEditorExpanded,
    onComplete = { result ->
      itemBottomSheetRequest.result.complete(result)
      itemBottomSheetRequest = Event.RequestItemSheet()
    }
  )

  ItemSearchOverlay(
    visible = showItemSearch,
    foodItems = foodItems,
    onComplete = {
      showItemSearch = false
      itemSearchRequest.result.complete(it)
    }
  )
}
