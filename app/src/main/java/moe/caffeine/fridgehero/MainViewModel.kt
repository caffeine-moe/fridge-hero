package moe.caffeine.fridgehero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import moe.caffeine.fridgehero.model.FoodItem
import moe.caffeine.fridgehero.repo.MongoRealm

class MainViewModel : ViewModel() {
    val realm = MongoRealm

    val foodItems = realm
        .fetchAllByTypeAsFlow<FoodItem>()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )
}