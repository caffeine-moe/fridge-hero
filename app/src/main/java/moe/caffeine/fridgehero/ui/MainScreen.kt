package moe.caffeine.fridgehero.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.android.datatransport.BuildConfig
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.data.realm.FoodItem
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.ui.components.DatePickerModal
import moe.caffeine.fridgehero.ui.item.ItemFull
import moe.caffeine.fridgehero.ui.item.ItemSheet
import moe.caffeine.fridgehero.ui.nav.BottomNavBar
import moe.caffeine.fridgehero.ui.nav.BottomNavGraph
import moe.caffeine.fridgehero.ui.nav.BottomNavItem
import moe.caffeine.fridgehero.ui.scanner.Scanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  navBarItems: List<BottomNavItem>,
  eventFlow: SharedFlow<Event>,
  emitEvent: (Event) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val navController = rememberNavController()
  var destination by rememberSaveable { mutableStateOf("Home") }
  var fullscreenItem: FoodItem? by remember { mutableStateOf(null) }
  var datePickerRequest: Event.RequestDateFromPicker? by remember {
    mutableStateOf(null)
  }
  var bottomSheetRequest: Event.RequestItemSheet? by remember {
    mutableStateOf(null)
  }
  var barcodeScanRequest: Event.RequestBarcodeFromScanner? by remember {
    mutableStateOf(null)
  }
  var scannerPadding: PaddingValues by remember {
    mutableStateOf(PaddingValues())
  }
  val context = LocalContext.current
  Surface(
    modifier = Modifier.fillMaxSize()
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding(),
      bottomBar = {
        BottomNavBar(
          navController,
          navBarItems
        ) {
          destination = it
        }
      },
      topBar = {
        TopAppBar(
          modifier = Modifier
            .background(Color.Black),
          title = {
            Text(
              destination,
              style = MaterialTheme.typography.headlineLarge
            )
          },
        )
      }
    ) { paddingValues ->
      scannerPadding = paddingValues
      Column(
        Modifier.padding(paddingValues)
      ) {
        BottomNavGraph(
          navController,
          destination,
          navBarItems
        )
      }
    }
  }

  LaunchedEffect(Unit) {
    eventFlow.collectLatest { event ->
      when (event) {
        is Event.DisplayToast -> {
          Toast.makeText(
            context,
            event.message,
            Toast.LENGTH_SHORT
          ).show()
        }

        is Event.RequestBarcodeFromScanner -> {
          barcodeScanRequest = event
        }

        is Event.RequestDateFromPicker -> {
          datePickerRequest = event
        }

        is Event.RequestItemSheet -> {
          bottomSheetRequest = event
        }

        is Event.RequestItemFullScreen -> {
          fullscreenItem = event.foodItem
        }

        else -> return@collectLatest
      }
    }
  }

  AnimatedVisibility(fullscreenItem != null) {
    fullscreenItem?.let {
      ItemFull(it) {
        fullscreenItem = null
      }
    }
  }

  AnimatedVisibility(datePickerRequest != null) {
    datePickerRequest?.result?.let { completable ->
      DatePickerModal(
        onDateSelected = { date ->
          completable.complete(Result.success(date))
        },
        onDismiss = {
          datePickerRequest = null
          completable.complete(Result.failure(Throwable("Dismissed")))
        }
      )
    }
  }

  AnimatedVisibility(barcodeScanRequest != null) {
    barcodeScanRequest?.result?.let { completable ->
      var barcode = "5941143028832"
      if (!BuildConfig.DEBUG) {
        Scanner(
          scannerPadding
        ) {
          barcode = it
        }
      }
      completable.complete(Result.success(barcode))
      barcodeScanRequest = null
    }
  }

  AnimatedVisibility(bottomSheetRequest != null) {
    bottomSheetRequest?.let { event ->
      val originalID = event.prefill._id
      var editableFoodItem by remember { mutableStateOf(event.prefill) }
      var editableExpiryDates by remember {
        mutableStateOf(event.prefill.expiryDates.toList())
      }
      ItemSheet(
        editableFoodItem = editableFoodItem,
        editableExpiryDates = editableExpiryDates,
        onDismiss = {
          event.result.complete(
            Result.failure(Throwable("Dismissed"))
          )
          bottomSheetRequest = null
        },
        onComplete = {
          editableFoodItem = editableFoodItem.apply { _id = originalID }
          event.result.complete(
            Result.success(
              Pair(
                editableFoodItem,
                editableExpiryDates
              )
            )
          )
          if (event.upsertResult) {
            emitEvent(
              Event.UpsertFoodItem(
                editableFoodItem
              ),
            )
            emitEvent(
              Event.RequestLiveFoodItemOperation(
                editableFoodItem,
                {
                  it.expiryDates.clear()
                  it.expiryDates.addAll(editableExpiryDates)
                }
              )
            )
            bottomSheetRequest = null
          }
        },
        onResetRequest = {
          editableFoodItem = event.prefill
          editableExpiryDates = event.prefill.expiryDates.toList()
        },
        onScannerRequest = { replaceAll ->
          scope.launch {
            val completableBarcode: CompletableDeferred<Result<String>> =
              CompletableDeferred()
            val completableFoodItem: CompletableDeferred<Result<FoodItem>> =
              CompletableDeferred()
            emitEvent(Event.RequestBarcodeFromScanner(completableBarcode))
            completableBarcode.await().fold(
              onSuccess = { barcode ->
                if (!replaceAll) {
                  editableFoodItem.barcode = barcode
                  return@launch
                }
                emitEvent(
                  Event.RequestFoodItemFromBarcode(
                    barcode,
                    completableFoodItem
                  )
                )
              },
              onFailure = {
                return@launch
              }
            )
            completableFoodItem.await().onSuccess {
              editableFoodItem = it
            }
          }
        },
        onExpiryAddRequest = {
          scope.launch {
            val completableExpiry: CompletableDeferred<Result<Long>> =
              CompletableDeferred()
            emitEvent(
              Event.RequestDateFromPicker(
                completableExpiry
              )
            )
            completableExpiry.await().onSuccess { expiryDate ->
              editableExpiryDates = editableExpiryDates.toMutableList()
                .apply { add(expiryDate) }
            }
          }
        },
        onExpiryRemoveRequest = { expiryDate ->
          editableExpiryDates = editableExpiryDates.toMutableList()
            .apply { remove(expiryDate) }
        },
        onExpiryDuplicateRequest = { expiryDate ->
          editableExpiryDates = editableExpiryDates.toMutableList()
            .apply { add(expiryDate) }
        },
        onEditorFieldChanged = { editedFoodItem ->
          editableFoodItem = FoodItem().apply {
            name = editedFoodItem.name
            brand = editedFoodItem.brand
            barcode = editedFoodItem.barcode
            imageByteArray = editedFoodItem.imageByteArray
          }
        }
      )
    }
  }
}
