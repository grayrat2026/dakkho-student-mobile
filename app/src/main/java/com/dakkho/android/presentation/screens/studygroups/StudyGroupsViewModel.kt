package com.dakkho.android.presentation.screens.studygroups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SocialApiService
import com.dakkho.android.domain.model.StudyGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyGroupsViewModel @Inject constructor(
    private val socialApiService: SocialApiService
) : ViewModel() {

    private val _groups = MutableStateFlow<List<StudyGroup>>(emptyList())
    val groups: StateFlow<List<StudyGroup>> = _groups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCreatingGroup = MutableStateFlow(false)
    val isCreatingGroup: StateFlow<Boolean> = _isCreatingGroup.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = socialApiService.getStudyGroups()
                if (response.isSuccessful) {
                    _groups.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            try {
                val response = socialApiService.joinGroup(groupId)
                if (response.isSuccessful) loadGroups()
            } catch (_: Exception) {}
        }
    }

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            try {
                val response = socialApiService.leaveGroup(groupId)
                if (response.isSuccessful) loadGroups()
            } catch (_: Exception) {}
        }
    }

    fun createGroup(name: String, description: String, subject: String) {
        viewModelScope.launch {
            _isCreatingGroup.value = true
            try {
                val request = com.dakkho.android.domain.model.CreateGroupRequest(name, description, subject)
                val response = socialApiService.createGroup(request)
                if (response.isSuccessful) {
                    _showCreateDialog.value = false
                    loadGroups()
                }
            } catch (_: Exception) {
            } finally {
                _isCreatingGroup.value = false
            }
        }
    }

    fun showCreateDialog() { _showCreateDialog.value = true }
    fun hideCreateDialog() { _showCreateDialog.value = false }
}
