package moe.caffeine.fridgehero.data

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.realm.fetchObjectById
import org.mongodb.kbson.BsonObjectId

class ExpiryActionReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val itemId = intent.extras?.getString("ITEM_ID") ?: return
    val expiryDate = intent.extras?.getLong("EXPIRY_DATE") ?: return

    val realm = RealmProvider.realmInstance

    val item =
      (realm.fetchObjectById<RealmFoodItem>(BsonObjectId.invoke(itemId)).getOrNull() ?: return)

    realm.writeBlocking { findLatest(item)?.expiryDates?.remove(expiryDate) }

    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(itemId.hashCode())

    Toast.makeText(context, "Expiry date removed", Toast.LENGTH_SHORT).show()
  }
}
