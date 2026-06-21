package com.dakkho.android.presentation.screens.discussionforum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.ForumComment
import com.dakkho.android.domain.model.ForumThread
import com.dakkho.android.domain.model.ForumCategory
import com.dakkho.android.domain.repository.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscussionForumUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val threads: List<ForumThread> = emptyList(),
    val selectedThread: Pair<ForumThread, List<ForumComment>>? = null,
    val selectedCategory: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val isCreatingThread: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val showNewPostSheet: Boolean = false,
    val showThreadDetail: Boolean = false,
    val newPostTitle: String = "",
    val newPostBody: String = "",
    val newPostCategory: ForumCategory = ForumCategory.GENERAL,
    val commentBody: String = "",
    val isPreviewMode: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class DiscussionForumViewModel @Inject constructor(
    private val forumRepository: ForumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionForumUiState())
    val uiState: StateFlow<DiscussionForumUiState> = _uiState.asStateFlow()

    init {
        loadThreads()
    }

    fun loadThreads() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = forumRepository.getForumThreads(
                category = _uiState.value.selectedCategory,
                page = 1
            )
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    threads = result.getOrDefault(emptyList()),
                    currentPage = 1
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to load threads"
                )
            }
        }
    }

    fun loadMoreThreads() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return
        viewModelScope.launch {
            val nextPage = _uiState.value.currentPage + 1
            val result = forumRepository.getForumThreads(
                category = _uiState.value.selectedCategory,
                page = nextPage
            )
            if (result.isSuccess) {
                val newThreads = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    threads = _uiState.value.threads + newThreads,
                    currentPage = nextPage,
                    hasMorePages = newThreads.size >= 20
                )
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, currentPage = 1)
        loadThreads()
    }

    fun openThreadDetail(threadId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = forumRepository.getForumThreadDetail(threadId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedThread = result.getOrNull(),
                    showThreadDetail = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to load thread"
                )
            }
        }
    }

    fun closeThreadDetail() {
        _uiState.value = _uiState.value.copy(
            showThreadDetail = false,
            selectedThread = null
        )
    }

    fun showNewPostSheet() {
        _uiState.value = _uiState.value.copy(
            showNewPostSheet = true,
            newPostTitle = "",
            newPostBody = "",
            newPostCategory = ForumCategory.GENERAL,
            isPreviewMode = false
        )
    }

    fun hideNewPostSheet() {
        _uiState.value = _uiState.value.copy(showNewPostSheet = false)
    }

    fun updateNewPostTitle(title: String) {
        _uiState.value = _uiState.value.copy(newPostTitle = title)
    }

    fun updateNewPostBody(body: String) {
        _uiState.value = _uiState.value.copy(newPostBody = body)
    }

    fun updateNewPostCategory(category: ForumCategory) {
        _uiState.value = _uiState.value.copy(newPostCategory = category)
    }

    fun togglePreviewMode() {
        _uiState.value = _uiState.value.copy(isPreviewMode = !_uiState.value.isPreviewMode)
    }

    fun submitNewPost() {
        val state = _uiState.value
        if (state.newPostTitle.isBlank() || state.newPostBody.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Title and body are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingThread = true)
            val result = forumRepository.createForumThread(
                title = state.newPostTitle.trim(),
                body = state.newPostBody.trim(),
                category = state.newPostCategory.name.lowercase()
            )
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isCreatingThread = false,
                    showNewPostSheet = false,
                    successMessage = "Post created successfully!"
                )
                loadThreads() // Refresh list
            } else {
                _uiState.value = _uiState.value.copy(
                    isCreatingThread = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to create post"
                )
            }
        }
    }

    fun updateCommentBody(body: String) {
        _uiState.value = _uiState.value.copy(commentBody = body)
    }

    fun submitComment() {
        val state = _uiState.value
        val threadId = state.selectedThread?.first?.id ?: return
        if (state.commentBody.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingComment = true)
            val result = forumRepository.createComment(threadId, state.commentBody.trim())
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingComment = false,
                    commentBody = "",
                    successMessage = "Comment posted!"
                )
                openThreadDetail(threadId) // Refresh thread detail
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingComment = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to post comment"
                )
            }
        }
    }

    fun toggleUpvote(threadId: String) {
        viewModelScope.launch {
            val result = forumRepository.toggleUpvote(threadId)
            if (result.isSuccess) {
                // Refresh to show updated upvote count
                loadThreads()
                if (_uiState.value.showThreadDetail) {
                    openThreadDetail(threadId)
                }
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadThreads()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    /** Insert markdown formatting at cursor position */
    fun insertMarkdownFormatting(format: String) {
        val currentBody = _uiState.value.newPostBody
        val formatted = when (format) {
            "bold" -> "**$currentBody**"
            "italic" -> "_${currentBody}_"
            "code" -> "`$currentBody`"
            "link" -> "[$currentBody](url)"
            "heading" -> "## $currentBody"
            "list" -> "- $currentBody"
            else -> currentBody
        }
        _uiState.value = _uiState.value.copy(newPostBody = formatted)
    }
}
