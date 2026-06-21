package com.dakkho.android.presentation.screens.peerconnections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SocialApiService
import com.dakkho.android.domain.model.PeerUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeerConnectionsViewModel @Inject constructor(
    private val socialApiService: SocialApiService
) : ViewModel() {

    private val _peers = MutableStateFlow<List<PeerUser>>(emptyList())
    val peers: StateFlow<List<PeerUser>> = _peers.asStateFlow()

    private val _suggestions = MutableStateFlow<List<PeerUser>>(emptyList())
    val suggestions: StateFlow<List<PeerUser>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPeers()
        loadSuggestions()
    }

    fun loadPeers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = socialApiService.getPeers()
                if (response.isSuccessful) {
                    _peers.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSuggestions() {
        viewModelScope.launch {
            try {
                val response = socialApiService.getPeerSuggestions()
                if (response.isSuccessful) {
                    _suggestions.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleFollow(peerId: String, isCurrentlyFollowing: Boolean) {
        viewModelScope.launch {
            try {
                val response = if (isCurrentlyFollowing) socialApiService.unfollowPeer(peerId) else socialApiService.followPeer(peerId)
                if (response.isSuccessful) {
                    _peers.value = _peers.value.map {
                        if (it.id == peerId) it.copy(isFollowing = !isCurrentlyFollowing) else it
                    }
                    _suggestions.value = _suggestions.value.map {
                        if (it.id == peerId) it.copy(isFollowing = !isCurrentlyFollowing) else it
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
