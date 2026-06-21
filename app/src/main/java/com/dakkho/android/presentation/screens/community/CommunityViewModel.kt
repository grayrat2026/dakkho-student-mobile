package com.dakkho.android.presentation.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SocialApiService
import com.dakkho.android.domain.model.CommunityComment
import com.dakkho.android.domain.model.CommunityPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val socialApiService: SocialApiService
) : ViewModel() {

    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow<List<CommunityComment>>(emptyList())
    val comments: StateFlow<List<CommunityComment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init { loadPosts() }

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = socialApiService.getCommunityPosts(_selectedCategory.value)
                if (response.isSuccessful) {
                    _posts.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                val response = socialApiService.getPostComments(postId)
                if (response.isSuccessful) {
                    _comments.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    fun upvotePost(postId: String) {
        viewModelScope.launch {
            try {
                val response = socialApiService.upvotePost(postId)
                if (response.isSuccessful) {
                    _posts.value = _posts.value.map {
                        if (it.id == postId) it.copy(
                            upvotes = if (it.isUpvoted) it.upvotes - 1 else it.upvotes + 1,
                            isUpvoted = !it.isUpvoted
                        ) else it
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun createPost(title: String, content: String, category: String) {
        viewModelScope.launch {
            try {
                val request = com.dakkho.android.domain.model.CreatePostRequest(title, content, category)
                val response = socialApiService.createPost(request)
                if (response.isSuccessful) loadPosts()
            } catch (_: Exception) {}
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                val response = socialApiService.addComment(postId, mapOf("content" to content))
                if (response.isSuccessful) loadComments(postId)
            } catch (_: Exception) {}
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        loadPosts()
    }
}
