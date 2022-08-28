package com.tiagoalmeida.lottery.data.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.repository.ConsultRepositoryImpl
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.ui.main.MainActivity
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.repository.ConsultRepository
import com.tiagoalmeida.lottery.extensions.isSDKVersionBiggerThanM
import com.tiagoalmeida.lottery.util.Constants
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConsultGamesWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters), KoinComponent {

    companion object {
        const val WORK_REQUEST_NAME = "ConsultGamesWorker"
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_REQUEST_CODE = 101
    }

    private val consultRepository: ConsultRepository by inject()

    private val preferencesRepository: PreferencesRepository by inject()

    override suspend fun doWork(): Result {
        var hasNewGames = false

        for (type in LotteryType.values()) {
            if (checkGame(type)) {
                hasNewGames = true
                break
            }
        }

        return if (hasNewGames) {
            createNotifications()
            Result.success()
        } else {
            Result.retry()
        }
    }

    private suspend fun checkGame(type: LotteryType): Boolean {
        val savedContestNumber = preferencesRepository.getLastSavedContestNumber(type)
        if (savedContestNumber > 0) {
            val result = consultRepository.consultLatestContest(type)
            return result.contestNumber.toInt() > savedContestNumber
        }
        return false
    }

    private fun createNotifications() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_REQUEST_CODE,
            intent,
            if (isSDKVersionBiggerThanM()) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val notification = NotificationCompat
            .Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.icon_clover)
            .setContentTitle(applicationContext.getString(R.string.notification_title_single))
            .setContentText(applicationContext.getString(R.string.notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(NOTIFICATION_ID, notification)
    }

}