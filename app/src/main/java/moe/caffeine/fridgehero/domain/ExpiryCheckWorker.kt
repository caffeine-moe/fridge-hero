package moe.caffeine.fridgehero.domain

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.repository.DataRepositoryImpl
import moe.caffeine.fridgehero.domain.helper.expiryImminent
import moe.caffeine.fridgehero.domain.repository.DataRepository

class ExpiryCheckWorker(
  context: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

  private val repository: DataRepository by lazy {
    DataRepositoryImpl(
      RealmProvider,
      OpenFoodFactsApi,
      CoroutineScope(Dispatchers.IO)
    )
  }

  override suspend fun doWork(): Result {
    checkExpiringItems()
    return Result.success()
  }

  private fun checkExpiringItems() {
    val items = repository.getAllFoodItemsAsList()
    val notificationHelper = NotificationHelper(applicationContext)

    val toNotify =
      items.filter { it.expiresSoon && it.expiryDates.any { expiry -> expiry.expiryImminent() } }

    notificationHelper.showExpiryNotification(toNotify)
  }

  companion object {
    const val WORK_TAG = "expiry_check"
  }
}
