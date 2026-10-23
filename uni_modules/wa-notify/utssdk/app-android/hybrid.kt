package uts.sdk.modules.waNotify

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 每晨通知：AlarmManager 定时（每天 09:00 ±10 分钟窗口）+ 常驻接收器弹通知并自排次日。
 * 摘要（昨夜夜 key + 次数）由 JS 侧在数据变更/页面显示时写入本插件私有 SharedPreferences，
 * 接收器被系统拉起（进程已死）时直接读取，无 JS 环境。
 */
class WaNotifyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val nightKey = prefs.getString(KEY_NIGHT, "") ?: ""
        val count = prefs.getInt(KEY_COUNT, -1)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val yesterday = fmt.format(cal.time)
        val text = if (nightKey == yesterday && count >= 0) {
            if (count > 0) "昨晚娃醒了 " + count + " 次 ☀️" else "昨晚一夜安睡，没有醒来 🌙"
        } else {
            "看看昨晚娃醒了几次 ☀️"
        }
        notifyNow(context, text)
        if (prefs.getBoolean(KEY_ENABLED, false)) {
            scheduleNext(context)
        }
    }

    companion object {
        private const val PREFS_NAME = "wa_notify"
        private const val KEY_NIGHT = "nightKey"
        private const val KEY_COUNT = "count"
        private const val KEY_ENABLED = "enabled"
        private const val CHANNEL_ID = "wa_morning_report"
        private const val NOTIFY_ID = 1001
        private const val ALARM_REQUEST_CODE = 1001
        private const val WINDOW_MS = 10 * 60 * 1000L

        fun scheduleNext(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pi = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                Intent(context, WaNotifyReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            am.setWindow(AlarmManager.RTC_WAKEUP, nextNineAm(), WINDOW_MS, pi)
        }

        fun cancel(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pi = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                Intent(context, WaNotifyReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            am.cancel(pi)
        }

        fun setEnabled(context: Context, value: Boolean) {
            prefs(context).edit().putBoolean(KEY_ENABLED, value).apply()
        }

        fun isEnabled(context: Context): Boolean {
            return prefs(context).getBoolean(KEY_ENABLED, false)
        }

        fun setSummary(context: Context, nightKey: String, count: Number) {
            prefs(context).edit().putString(KEY_NIGHT, nightKey).putInt(KEY_COUNT, count.toInt()).apply()
        }

        private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        private fun nextNineAm(): Long {
            val cal = Calendar.getInstance()
            if (cal.get(Calendar.HOUR_OF_DAY) >= 9) {
                cal.add(Calendar.DATE, 1)
            }
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        private fun notifyNow(context: Context, text: String) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= 26) {
                nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "每晨报告", NotificationManager.IMPORTANCE_DEFAULT))
            }
            val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
            if (launch == null) {
                return
            }
            val pi = PendingIntent.getActivity(
                context,
                NOTIFY_ID,
                launch,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val builder: Notification.Builder
            if (Build.VERSION.SDK_INT >= 26) {
                builder = Notification.Builder(context, CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                builder = Notification.Builder(context)
            }
            builder.setContentTitle("娃醒了")
            builder.setContentText(text)
            builder.setAutoCancel(true)
            builder.setContentIntent(pi)
            builder.setSmallIcon(context.applicationInfo.icon)
            nm.notify(NOTIFY_ID, builder.build())
        }
    }
}
