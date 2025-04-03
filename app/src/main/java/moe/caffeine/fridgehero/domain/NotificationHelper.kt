package moe.caffeine.fridgehero.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.NotificationCompat
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.data.ExpiryActionReceiver
import moe.caffeine.fridgehero.domain.helper.readableDaysUntil
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem

class NotificationHelper(private val context: Context) {
  fun showExpiryNotification(items: List<FoodItem>) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        "expiry_channel",
        "Expiry Alerts",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notifications for expiring items"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 500, 200, 500)
      }
      notificationManager.createNotificationChannel(channel)
    }

    if (items.isEmpty())
      return

    val summary = NotificationCompat.Builder(context, "expiry_channel")
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("${items.size} items Expiring Soon!")
      .setGroup(items.hashCode().toString())
      .setGroupSummary(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setVibrate(longArrayOf(0, 500, 200, 500))
      .setAutoCancel(true)
      .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
      .setStyle(
        NotificationCompat.InboxStyle()
          .setSummaryText("${items.size} items expiring soon")
      )
      .build()

    notificationManager.notify(items.hashCode(), summary)

    items.forEach { item ->

      val dismissIntent = Intent(context, ExpiryActionReceiver::class.java).apply {
        putExtra("ITEM_ID", item.realmId)
        putExtra("EXPIRY_DATE", item.expiryDates.filter { it != -1L }.minOrNull() ?: return@forEach)
        action = "ACTION_DISMISS_EXPIRY_${item.realmId}"
      }

      val dismissPendingIntent = PendingIntent.getBroadcast(
        context,
        item.hashCode(),
        dismissIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val notification = NotificationCompat.Builder(context, "expiry_channel")
        .setSmallIcon(
          R.drawable.priority_high
        )
        .apply {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setStyle(
              NotificationCompat
                .BigPictureStyle()
                .bigPicture(
                  Icon.createWithData(
                    item.imageByteArray,
                    0,
                    item.imageByteArray.size
                  )
                )
                .showBigPictureWhenCollapsed(true)
            )
          }
        }
        .setContentTitle("Expiring Soon: ${item.name}")
        .setContentText(
          "Expiry: ${
            item.expiryDates.filter { it != -1L }.min().readableDaysUntil()
          }"
        )
        .addAction(R.drawable.close, "Remove Item", dismissPendingIntent)
        .setGroup(items.hashCode().toString())
        .setSortKey(item.expiryDates.filter { it != -1L }.min().toString())
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
        .setAutoCancel(true)
        .build()
      notificationManager.notify(item.realmId.hashCode(), notification)
    }

  }
}
