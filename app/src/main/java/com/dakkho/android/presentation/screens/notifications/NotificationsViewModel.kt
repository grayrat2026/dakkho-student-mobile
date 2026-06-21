package com.dakkho.android.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dakkho.android.data.api.NotificationApiService
import com.dakkho.android.data.paging.NotificationPagingSource
import com.dakkho.android.domain.model.NotificationItem
import com.dakkho.android.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val notificationApiService: NotificationApiService
) : ViewModel() {

    val unreadCount: StateFlow<Int> = notificationRepository
        .getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedNotification = MutableStateFlow<NotificationItem?>(null)
    val selectedNotification: StateFlow<NotificationItem?> = _selectedNotification.asStateFlow()

    val pagingDataFlow: Flow<PagingData<NotificationItem>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
            initialLoadSize = 40
        ),
        pagingSourceFactory = {
            NotificationPagingSource(notificationApiService)
        }
    ).flow.cachedIn(viewModelScope)

    // Room-backed flow for instant local updates
    val localNotifications = notificationRepository.getNotificationsFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.update { true }
            notificationRepository.syncNotifications()
            _isRefreshing.update { false }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(id)
        }
    }

    suspend fun getNotificationById(id: String): NotificationItem? {
        return notificationRepository.getNotificationById(id)
    }

    fun selectNotification(notification: NotificationItem?) {
        _selectedNotification.update { notification }
        if (notification != null && !notification.isRead) {
            markAsRead(notification.id)
        }
    }
}
