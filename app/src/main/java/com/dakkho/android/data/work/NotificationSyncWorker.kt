package com.dakkho.android.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dakkho.android.domain.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class NotificationSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("NotificationSyncWorker: syncing notifications")
            notificationRepository.syncNotifications(page = 1, pageSize = 50)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "NotificationSyncWorker: sync failed")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val WORK_NAME = "notification_sync_periodic"
        const val SYNC_INTERVAL_MINUTES = 15L
    }
}
