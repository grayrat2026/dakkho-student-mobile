package com.dakkho.android.presentation.screens.instructorschedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.LiveClass
import com.dakkho.android.domain.model.LiveClassStatus
import com.dakkho.android.domain.repository.InstructorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class InstructorScheduleUiState(
    val instructorName: String = "",
    val liveClasses: List<LiveClass> = emptyList(),
    val upcomingClasses: List<LiveClass> = emptyList(),
    val pastClasses: List<LiveClass> = emptyList(),
    val selectedMonth: Calendar = Calendar.getInstance(),
    val selectedDate: Calendar? = null,
    val eventsByDate: Map<String, List<LiveClass>> = emptyMap(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class InstructorScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val instructorRepository: InstructorRepository
) : ViewModel() {

    private val instructorId: String = savedStateHandle["instructorId"] ?: ""
    private val instructorName: String = savedStateHandle["instructorName"] ?: ""

    private val _uiState = MutableStateFlow(InstructorScheduleUiState(instructorName = instructorName))
    val uiState: StateFlow<InstructorScheduleUiState> = _uiState.asStateFlow()

    init {
        loadLiveClasses()
    }

    fun loadLiveClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = instructorRepository.getInstructorLiveClasses(instructorId)
            result.fold(
                onSuccess = { (liveClasses, _) ->
                    val upcoming = liveClasses.filter {
                        it.status == LiveClassStatus.SCHEDULED || it.status == LiveClassStatus.LIVE
                    }.sortedBy { it.scheduledAt }

                    val past = liveClasses.filter {
                        it.status == LiveClassStatus.ENDED
                    }.sortedByDescending { it.scheduledAt }

                    val eventsByDate = mutableMapOf<String, List<LiveClass>>()
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    for (liveClass in liveClasses) {
                        val dateKey = liveClass.scheduledAt?.let { parseDate(it) }
                            ?.let { sdf.format(it) } ?: continue
                        eventsByDate[dateKey] = (eventsByDate[dateKey] ?: emptyList()) + liveClass
                    }

                    _uiState.update {
                        it.copy(
                            liveClasses = liveClasses,
                            upcomingClasses = upcoming,
                            pastClasses = past,
                            eventsByDate = eventsByDate,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Failed to load schedule"
                        )
                    }
                }
            )
        }
    }

    fun selectMonth(calendar: Calendar) {
        _uiState.update { it.copy(selectedMonth = calendar.clone() as Calendar, selectedDate = null) }
    }

    fun selectDate(calendar: Calendar) {
        _uiState.update { it.copy(selectedDate = calendar.clone() as Calendar) }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadLiveClasses()
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            val format = when {
                dateStr.contains("T") -> SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                dateStr.contains(" ") -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                else -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }
            format.parse(dateStr)
        } catch (e: Exception) { null }
    }
}
