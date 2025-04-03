package moe.caffeine.fridgehero.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.helper.readableDaysUntil
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem

class NotificationHelper(private val context: Context) {
  @RequiresApi(Build.VERSION_CODES.M)
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
      val image = Icon.createWithData(
        item.imageByteArray,
        0,
        item.imageByteArray.size
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
                .bigPicture(image)
                .showBigPictureWhenCollapsed(true)
            )
          }
        }
        .setContentTitle(item.name)
        .setContentText("Expiry: ${item.expiryDates.min().readableDaysUntil()}")
        .setGroup(items.hashCode().toString())
        .setSortKey(item.expiryDates.minBy { it != -1L }.toString())
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
        .setAutoCancel(true)
        .build()
      notificationManager.notify(item.hashCode(), notification)
    }

  }
}
