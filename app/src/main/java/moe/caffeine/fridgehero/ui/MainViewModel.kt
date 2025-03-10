package moe.caffeine.fridgehero.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.repository.DataRepositoryImpl
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.repository.DataRepository

class MainViewModel : ViewModel() {
  private val repository: DataRepository = DataRepositoryImpl(
    realmProvider = RealmProvider,
    openFoodFactsApi = OpenFoodFactsApi,
    viewModelScope
  )

  val profile: StateFlow<Result<Profile>?> = repository.getProfileAsFlow()
    .onStart { emit(null) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = Result.failure(Throwable("No profile found."))
    )

  fun upsertProfile(profile: Profile) = viewModelScope.launch {
    repository.upsertProfile(profile)
  }

  private fun initialiseRepository() = viewModelScope.launch { repository.initialise() }

  val initialisationStage: StateFlow<InitialisationStage> = repository.initialisationStage
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      InitialisationStage.None
    )

  val foodItems: StateFlow<List<FoodItem>> = repository.getAllFoodItemsAsFlow()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

  private val _eventFlow = MutableSharedFlow<Event>()
  val eventFlow: SharedFlow<Event> = _eventFlow

  fun emitEvent(event: Event) = viewModelScope.launch { _eventFlow.emit(event) }

  init {
    if (initialisationStage.value == InitialisationStage.None) {
      initialiseRepository()
    }
    eventFlow.onEach { event ->
      when (event) {
        is Event.RequestFoodItemFromBarcode -> {
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

        is Event.UpsertFoodItem ->
          event.result.complete(
            repository.upsertFoodItem(event.foodItem)
          )

        is Event.SoftRemoveFoodItem ->
          event.result.complete(
            repository.upsertFoodItem(
              event.foodItem.copy(expiryDates = listOf())
            )
          )

        is Event.HardRemoveFoodItem ->
          event.result.complete(
            repository.deleteFoodItem(event.foodItem)
          )

        else -> return@onEach
      }
    }.launchIn(viewModelScope)
  }
}
