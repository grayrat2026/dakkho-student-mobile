package com.dakkho.android.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.CourseNote
import com.dakkho.android.domain.repository.CourseNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class CourseNotesUiState(
    val isLoading: Boolean = true,
    val notes: List<CourseNote> = emptyList(),
    val selectedNote: CourseNote? = null,
    val isEditing: Boolean = false,
    val editContent: String = "",
    val showAddNoteSheet: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val noteToDelete: Long? = null,
    val currentVideoPositionMs: Long = 0,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CourseNotesViewModel @Inject constructor(
    private val courseNoteRepository: CourseNoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseNotesUiState())
    val uiState: StateFlow<CourseNotesUiState> = _uiState.asStateFlow()

    private var courseId: String = ""
    private var videoId: String = ""
    private var saveJob: Job? = null

    fun initialize(courseId: String, videoId: String = "") {
        this.courseId = courseId
        this.videoId = videoId
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val notes = if (videoId.isNotEmpty()) {
                    courseNoteRepository.getNotesForVideo(videoId)
                } else {
                    courseNoteRepository.getNotesForCourse(courseId)
                }
                _uiState.value = _uiState.value.copy(
                    notes = notes,
                    isLoading = false
                )
            } catch (e: Exception) {
                Timber.e(e, "Load notes failed")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load notes"
                )
            }
        }
    }

    fun onNoteClick(note: CourseNote) {
        _uiState.value = _uiState.value.copy(
            selectedNote = note,
            isEditing = true,
            editContent = note.content
        )
    }

    fun onEditContentChange(content: String) {
        _uiState.value = _uiState.value.copy(editContent = content)
        // Debounced auto-save (1s after last edit)
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(1000)
            autoSaveCurrentNote()
        }
    }

    private suspend fun autoSaveCurrentNote() {
        val currentNote = _uiState.value.selectedNote ?: return
        val newContent = _uiState.value.editContent
        if (newContent.isBlank()) return

        val updatedNote = currentNote.copy(
            content = newContent,
            updatedAt = System.currentTimeMillis()
        )
        try {
            courseNoteRepository.updateNote(updatedNote)
            loadNotes()
        } catch (e: Exception) {
            Timber.e(e, "Auto-save note failed")
        }
    }

    fun addNote(positionMs: Long, content: String, videoTitle: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val timestampLabel = formatTimestamp(positionMs)
                val note = CourseNote(
                    videoId = videoId,
                    courseId = courseId,
                    userId = "",
                    positionMs = positionMs,
                    content = content,
                    videoTitle = videoTitle,
                    timestampLabel = timestampLabel
                )
                val id = courseNoteRepository.saveNote(note)
                if (id > 0) {
                    loadNotes()
                    _uiState.value = _uiState.value.copy(
                        showAddNoteSheet = false,
                        isSaving = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Failed to save note"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Add note failed")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save note"
                )
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            try {
                courseNoteRepository.deleteNote(id)
                _uiState.value = _uiState.value.copy(
                    showDeleteConfirm = false,
                    noteToDelete = null
                )
                loadNotes()
            } catch (e: Exception) {
                Timber.e(e, "Delete note failed")
            }
        }
    }

    fun showAddNoteSheet() {
        _uiState.value = _uiState.value.copy(showAddNoteSheet = true)
    }

    fun hideAddNoteSheet() {
        _uiState.value = _uiState.value.copy(showAddNoteSheet = false)
    }

    fun showDeleteConfirm(id: Long) {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirm = true,
            noteToDelete = id
        )
    }

    fun hideDeleteConfirm() {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirm = false,
            noteToDelete = null
        )
    }

    fun updateVideoPosition(positionMs: Long) {
        _uiState.value = _uiState.value.copy(currentVideoPositionMs = positionMs)
    }

    fun exitEditMode() {
        saveJob?.cancel()
        viewModelScope.launch {
            autoSaveCurrentNote()
            _uiState.value = _uiState.value.copy(
                isEditing = false,
                selectedNote = null,
                editContent = ""
            )
        }
    }

    private fun formatTimestamp(positionMs: Long): String {
        val totalSeconds = positionMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveJob?.cancel()
    }
}
