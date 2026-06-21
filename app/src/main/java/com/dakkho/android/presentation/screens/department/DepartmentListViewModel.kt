package com.dakkho.android.presentation.screens.department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.domain.repository.DepartmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Department List page.
 * Shows all departments dynamically — only what Admin/Instructor has added.
 * No hardcoded departments. If the API returns empty, we show an empty state.
 */
@HiltViewModel
class DepartmentListViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _departments = MutableStateFlow<List<Technology>>(emptyList())
    val departments: StateFlow<List<Technology>> = _departments.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Technology>>(emptyList())
    val searchResults: StateFlow<List<Technology>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDepartments()
    }

    private fun loadDepartments() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // First refresh from API, then emit Room data
                departmentRepository.refreshDepartments()
            } catch (e: Exception) {
                // API failed, but Room cache might have data
            } finally {
                _isLoading.value = false
            }

            // Collect Room data reactively
            departmentRepository.getAllDepartments().collect { departments ->
                _departments.value = departments
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                departmentRepository.refreshDepartments()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            departmentRepository.searchDepartments(query).collect { results ->
                _searchResults.value = results
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun retry() {
        loadDepartments()
    }
}
