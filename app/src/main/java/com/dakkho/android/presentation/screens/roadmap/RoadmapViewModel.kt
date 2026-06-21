package com.dakkho.android.presentation.screens.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SocialApiService
import com.dakkho.android.domain.model.FeatureStatus
import com.dakkho.android.domain.model.RoadmapFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val socialApiService: SocialApiService
) : ViewModel() {

    private val _features = MutableStateFlow<List<RoadmapFeature>>(emptyList())
    val features: StateFlow<List<RoadmapFeature>> = _features.asStateFlow()

    private val _selectedStatus = MutableStateFlow<FeatureStatus?>(null)
    val selectedStatus: StateFlow<FeatureStatus?> = _selectedStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { loadRoadmap() }

    fun loadRoadmap() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = socialApiService.getRoadmap()
                if (response.isSuccessful) {
                    _features.value = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun upvoteFeature(featureId: String) {
        viewModelScope.launch {
            try {
                val response = socialApiService.upvoteFeature(featureId)
                if (response.isSuccessful) {
                    _features.value = _features.value.map {
                        if (it.id == featureId) it.copy(
                            upvotes = if (it.isUpvoted) it.upvotes - 1 else it.upvotes + 1,
                            isUpvoted = !it.isUpvoted
                        ) else it
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun setStatusFilter(status: FeatureStatus?) {
        _selectedStatus.value = status
    }

    val filteredFeatures: List<RoadmapFeature>
        get() = if (_selectedStatus.value == null) _features.value else _features.value.filter { it.status == _selectedStatus.value }
}
