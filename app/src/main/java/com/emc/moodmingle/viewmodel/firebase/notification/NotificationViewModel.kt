package com.emc.moodmingle.viewmodel.firebase.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.notification.NotificationEntity
import com.emc.moodmingle.data.firebase.repository.notification.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    fun createNotification(notificationEntity: NotificationEntity) = viewModelScope.launch {
        notificationRepository.insert(notificationEntity)
    }

    fun getNotificationsByUserId(userId: String) =
        notificationRepository.getNotificationsByUserId(userId)

    fun getUnreadNotificationsByUserId(userId: String) =
        notificationRepository.getUnreadNotificationsByUserId(userId)

    suspend fun getNotificationPostId(postId: String) =
        notificationRepository.getNotificationPostId(postId)

    fun markNotificationsAsRead(unreadNotifications: List<NotificationEntity?>) {
        viewModelScope.launch {
            notificationRepository.markNotificationsAsRead(unreadNotifications)
        }
    }

    fun updateNotification(notificationEntity: NotificationEntity) = viewModelScope.launch {
        notificationRepository.update(notificationEntity)
    }

    fun deleteNotification(notificationEntity: NotificationEntity) = viewModelScope.launch {
        notificationRepository.delete(notificationEntity)
    }
}