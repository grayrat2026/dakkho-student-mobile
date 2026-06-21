package com.dakkho.android.presentation.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.db.dao.BookmarkDao
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class BookmarkItem(
    val courseId: String,
    val title: String,
    val instructorName: String?,
    val thumbnailUrl: String?,
    val price: Double?,
    val rating: Float?,
    val technology: String?,
    val bookmarkedAt: String?
)

data class BookmarksUiState(
    val bookmarks: List<BookmarkItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = encryptedPrefsHelper.getUserId() ?: return@launch
                val entities = bookmarkDao.getByUserId(userId)
                _uiState.update {
                    it.copy(
                        bookmarks = entities.map { entity ->
                            BookmarkItem(
                                courseId = entity.courseId,
                                title = entity.courseTitle ?: "",
                                instructorName = entity.instructorName,
                                thumbnailUrl = entity.thumbnailUrl,
                                price = entity.price,
                                rating = entity.rating,
                                technology = entity.technology,
                                bookmarkedAt = entity.createdAt
                            )
                        },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load bookmarks")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun removeBookmark(courseId: String) {
        viewModelScope.launch {
            try {
                val userId = encryptedPrefsHelper.getUserId() ?: return@launch
                bookmarkDao.deleteByCourseId(courseId, userId)
                _uiState.update {
                    it.copy(bookmarks = it.bookmarks.filter { b -> b.courseId != courseId })
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to remove bookmark")
            }
        }
    }
}
