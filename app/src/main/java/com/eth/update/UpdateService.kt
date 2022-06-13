package com.eth.update

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import java.net.URL

class UpdateService : Service() {
    var mini = 2000.0f

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        START_REDELIVER_INTENT


    override fun onCreate() {
        super.onCreate()

        Thread {
            while (true) {
                kotlin.runCatching {
                    val httpURLConnection =
                        URL("https://www.usd-cny.com/data/b.js").openConnection()
                    httpURLConnection.connect()
                    httpURLConnection.getInputStream().use {
                        val str = String(it.readBytes())
                        str.run {
                            val index = indexOf("��̫������\u052A(ETH/USD)")
                            val temp = substring(index - 10, index - 3).toFloat()
                            notifyEth(temp.toString(), 1000)
                            if (temp < mini) {
                                mini = temp
                                notifyEth(
                                    mini.toString(),
                                    1000,
                                    NotificationManager.IMPORTANCE_HIGH
                                )
                            }
                        }

                    }
                }

                Thread.sleep(5000)
            }
        }.start()
    }

    private fun notifyEth(msg: String, id: Int, it: Int = NotificationManager.IMPORTANCE_MIN) {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 创建通知渠道
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_name)
        val channel = NotificationChannel(id.toString(), name, it).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
        val mBuilder = Notification.Builder(this, id.toString())
            .setSmallIcon(R.mipmap.ic_launcher)//小图标
            .setContentTitle("当前：$msg")
            .setContentText("最低：$mini")
        notificationManager.notify(id, mBuilder.build())
    }
}