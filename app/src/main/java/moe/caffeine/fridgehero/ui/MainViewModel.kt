package moe.caffeine.fridgehero.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.repository.DataRepositoryImpl
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.repository.DataRepository
import moe.caffeine.fridgehero.ui.nav.Screen

class MainViewModel : ViewModel() {
  val repository: DataRepository = DataRepositoryImpl()

  val profile = repository.getProfile().getOrNull()

  val navBarItems = listOf(
    Screen.Home,
    Screen.Fridge,
    Screen.Recipes
  )

  val foodItems = repository
    .getAllFoodItemsAsFlow()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(),
      emptyList()
    )

  private val _eventFlow = MutableSharedFlow<Event>()
  val eventFlow: SharedFlow<Event> = _eventFlow

  fun emitEvent(event: Event) = viewModelScope.launch { _eventFlow.emit(event) }

  init {
    eventFlow.onEach { event ->
      withContext(Dispatchers.IO) {
        when (event) {
          is Event.RequestFoodItemFromBarcode -> {
            println("test")
            emitEvent(Event.DisplayToast("Processing barcode ${event.barcode}, please wait..."))
            val result = repository.retrieveFoodItemCachedFirst(event.barcode).fold(
              onSuccess = { foodItem ->
                emitEvent(Event.DisplayToast("Successfully retrieved ${foodItem.name}"))
                Result.success(foodItem)
              },
              onFailure = { failure ->
                emitEvent(Event.DisplayToast("ERROR: ${failure.message}"))
                Result.failure(failure)
              }
            )
            event.result.complete(result)
          }

          is Event.UpsertFoodItem -> {
            event.result.complete(
              repository.upsertFoodItem(event.foodItem)
            )
          }

          is Event.SoftRemoveFoodItem -> {
            repository.upsertFoodItem(
              event.foodItem.copy(expiryDates = listOf())
            )
          }

          is Event.HardRemoveFoodItem -> {
            event.result.complete(
              repository.deleteFoodItem(event.foodItem)
            )
          }

          else -> return@withContext
        }
      }
    }.launchIn(viewModelScope)
  }
}
