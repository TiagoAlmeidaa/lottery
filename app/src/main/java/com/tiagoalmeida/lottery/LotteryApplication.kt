package com.tiagoalmeida.lottery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.tiagoalmeida.lottery.data.worker.ConsultGamesWorker
import com.tiagoalmeida.lottery.di.LotteryModules
import com.tiagoalmeida.lottery.extensions.isSDKVersionBiggerThanO
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.Constants.NOTIFICATION_CHANNEL_ID
import org.koin.core.context.startKoin
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class LotteryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        setupKoin()
        createNotificationChannel()
        setupWorker()
    }

    private fun setupKoin() {
        startKoin {
            with(LotteryModules) {
                modules(firebase)
                modules(sharedPreferences(getPreferences()))
                modules(retrofit)
                modules(dataSource)
                modules(repository)
                modules(useCases)
                modules(viewModel)
            }
        }
    }

    private fun getPreferences(): SharedPreferences =
        getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    private fun createNotificationChannel() {
        if (isSDKVersionBiggerThanO()) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.channel_name),
                importance
            ).apply {
                description = getString(R.string.channel_description)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupWorker() {
        val timeNowMillis = System.currentTimeMillis()

        val timeEightThirtyPM = Calendar.getInstance(Locale.getDefault()).apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 30)
        }

        val delay = if (timeEightThirtyPM.timeInMillis > timeNowMillis) {
            timeEightThirtyPM.timeInMillis - timeNowMillis
        } else {
            0L
        }

        val workRequest = PeriodicWorkRequestBuilder<ConsultGamesWorker>(Constants.WORKER_PERIODICITY_ONE_DAY, TimeUnit.HOURS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, Constants.WORKER_BACK_OFF_POLICY_THIRTY_MINUTES, TimeUnit.MINUTES)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                ConsultGamesWorker.WORK_REQUEST_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

}
