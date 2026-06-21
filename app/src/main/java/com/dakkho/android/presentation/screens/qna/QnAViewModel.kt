package com.dakkho.android.presentation.screens.qna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Discussion
import com.dakkho.android.domain.model.DiscussionReply
import com.dakkho.android.domain.repository.DiscussionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class QnAUiState(
    val isLoading: Boolean = true,
    val discussions: List<Discussion> = emptyList(),
    val selectedDiscussion: Discussion? = null,
    val replies: List<DiscussionReply> = emptyList(),
    val isLoadingDetail: Boolean = false,
    val isSubmittingQuestion: Boolean = false,
    val isSubmittingReply: Boolean = false,
    val showAskQuestionSheet: Boolean = false,
    val showReplySheet: Boolean = false,
    val questionTitle: String = "",
    val questionBody: String = "",
    val questionTags: String = "",
    val replyBody: String = "",
    val page: Int = 1,
    val hasMore: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class QnAViewModel @Inject constructor(
    private val discussionRepository: DiscussionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QnAUiState())
    val uiState: StateFlow<QnAUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String) {
        this.courseId = courseId
        loadDiscussions()
    }

    fun loadDiscussions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            discussionRepository.getDiscussions(courseId, page = 1)
                .onSuccess { discussions ->
                    _uiState.value = _uiState.value.copy(
                        discussions = discussions,
                        isLoading = false,
                        page = 1,
                        hasMore = discussions.size >= 20
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load discussions failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load discussions"
                    )
                }
        }
    }

    fun loadMoreDiscussions() {
        if (_uiState.value.isLoading || !_uiState.value.hasMore) return
        val nextPage = _uiState.value.page + 1
        viewModelScope.launch {
            discussionRepository.getDiscussions(courseId, page = nextPage)
                .onSuccess { newDiscussions ->
                    _uiState.value = _uiState.value.copy(
                        discussions = _uiState.value.discussions + newDiscussions,
                        page = nextPage,
                        hasMore = newDiscussions.size >= 20
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load more discussions failed")
                }
        }
    }

    fun openDiscussionDetail(discussion: Discussion) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedDiscussion = discussion,
                isLoadingDetail = true
            )
            discussionRepository.getDiscussionDetail(discussion.id)
                .onSuccess { (thread, replies) ->
                    _uiState.value = _uiState.value.copy(
                        selectedDiscussion = thread,
                        replies = replies,
                        isLoadingDetail = false
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load discussion detail failed")
                    _uiState.value = _uiState.value.copy(isLoadingDetail = false)
                }
        }
    }

    fun closeDiscussionDetail() {
        _uiState.value = _uiState.value.copy(
            selectedDiscussion = null,
            replies = emptyList()
        )
    }

    fun showAskQuestionSheet() {
        _uiState.value = _uiState.value.copy(
            showAskQuestionSheet = true,
            questionTitle = "",
            questionBody = "",
            questionTags = ""
        )
    }

    fun hideAskQuestionSheet() {
        _uiState.value = _uiState.value.copy(showAskQuestionSheet = false)
    }

    fun updateQuestionTitle(title: String) {
        _uiState.value = _uiState.value.copy(questionTitle = title)
    }

    fun updateQuestionBody(body: String) {
        _uiState.value = _uiState.value.copy(questionBody = body)
    }

    fun updateQuestionTags(tags: String) {
        _uiState.value = _uiState.value.copy(questionTags = tags)
    }

    fun submitQuestion() {
        val title = _uiState.value.questionTitle.trim()
        val body = _uiState.value.questionBody.trim()
        if (title.isBlank() || body.isBlank()) return

        val tags = _uiState.value.questionTags
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingQuestion = true)
            discussionRepository.createDiscussion(courseId, title, body, tags)
                .onSuccess { newDiscussion ->
                    val updatedList = listOf(newDiscussion) + _uiState.value.discussions
                    _uiState.value = _uiState.value.copy(
                        discussions = updatedList,
                        isSubmittingQuestion = false,
                        showAskQuestionSheet = false,
                        questionTitle = "",
                        questionBody = "",
                        questionTags = ""
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Submit question failed")
                    _uiState.value = _uiState.value.copy(isSubmittingQuestion = false)
                }
        }
    }

    fun showReplySheet() {
        _uiState.value = _uiState.value.copy(
            showReplySheet = true,
            replyBody = ""
        )
    }

    fun hideReplySheet() {
        _uiState.value = _uiState.value.copy(showReplySheet = false)
    }

    fun updateReplyBody(body: String) {
        _uiState.value = _uiState.value.copy(replyBody = body)
    }

    fun submitReply() {
        val discussion = _uiState.value.selectedDiscussion ?: return
        val body = _uiState.value.replyBody.trim()
        if (body.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReply = true)
            discussionRepository.createReply(discussion.id, body)
                .onSuccess { newReply ->
                    val updatedReplies = _uiState.value.replies + newReply
                    val updatedDiscussion = discussion.copy(
                        replyCount = discussion.replyCount + 1
                    )
                    _uiState.value = _uiState.value.copy(
                        replies = updatedReplies,
                        selectedDiscussion = updatedDiscussion,
                        isSubmittingReply = false,
                        showReplySheet = false,
                        replyBody = ""
                    )
                    val updatedList = _uiState.value.discussions.map {
                        if (it.id == discussion.id) updatedDiscussion else it
                    }
                    _uiState.value = _uiState.value.copy(discussions = updatedList)
                }
                .onFailure { error ->
                    Timber.e(error, "Submit reply failed")
                    _uiState.value = _uiState.value.copy(isSubmittingReply = false)
                }
        }
    }

    fun toggleLike(discussion: Discussion) {
        viewModelScope.launch {
            discussionRepository.toggleLike(discussion.id)
                .onSuccess { (liked, likes) ->
                    val updatedDiscussion = discussion.copy(
                        likes = likes,
                        isLikedByUser = liked
                    )
                    val updatedList = _uiState.value.discussions.map {
                        if (it.id == discussion.id) updatedDiscussion else it
                    }
                    val selectedUpdate = if (_uiState.value.selectedDiscussion?.id == discussion.id) {
                        updatedDiscussion
                    } else {
                        _uiState.value.selectedDiscussion
                    }
                    _uiState.value = _uiState.value.copy(
                        discussions = updatedList,
                        selectedDiscussion = selectedUpdate
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Toggle like failed")
                }
        }
    }

    fun refresh() {
        loadDiscussions()
    }
}
