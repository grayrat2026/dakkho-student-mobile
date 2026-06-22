package com.dakkho.android.presentation.screens.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Curriculum
import com.dakkho.android.domain.model.Lesson
import com.dakkho.android.domain.model.LessonResources
import com.dakkho.android.domain.model.ResourceFile
import com.dakkho.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ResourceItem(
    val id: String,
    val title: String,
    val fileType: String,
    val fileSize: String? = null,
    val fileUrl: String,
    val lessonTitle: String,
    val sectionTitle: String,
    val isDownloaded: Boolean = false
)

data class ResourcesUiState(
    val isLoading: Boolean = true,
    val resources: List<ResourceItem> = emptyList(),
    val downloadingResourceId: String? = null,
    val searchQuery: String = "",
    val filterType: String? = null, // pdf, image, all
    val error: String? = null
) {
    val filteredResources: List<ResourceItem>
        get() {
            var filtered = resources
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                filtered = filtered.filter {
                    it.title.lowercase().contains(query) ||
                    it.lessonTitle.lowercase().contains(query)
                }
            }
            if (filterType != null && filterType != "all") {
                filtered = filtered.filter { it.fileType == filterType }
            }
            return filtered
        }
}

@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResourcesUiState())
    val uiState: StateFlow<ResourcesUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun initialize(courseId: String) {
        this.courseId = courseId
        loadResources()
    }

    fun loadResources() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            courseRepository.getCourseCurriculum(courseId)
                .onSuccess { curriculum ->
                    val resources = extractResources(curriculum)
                    _uiState.value = _uiState.value.copy(
                        resources = resources,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Timber.e(error, "Load resources failed")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load resources"
                    )
                }
        }
    }

    private fun extractResources(curriculum: Curriculum): List<ResourceItem> {
        val resourceItems = mutableListOf<ResourceItem>()
        curriculum.sections.forEach { subject ->
            val sectionTitle = subject.title
            // Walk the hierarchy: Subject -> Classes -> Units -> Lessons
            subject.classes?.forEach { cls ->
                cls.units?.forEach { unit ->
                    unit.lessons.forEach { lesson ->
                        lesson.resources?.let { res ->
                            addResourcesFromLesson(res, lesson, sectionTitle, resourceItems)
                        }
                    }
                }
            }
            // Backward compat: if no classes hierarchy, check direct lessons
            if (subject.classes.isNullOrEmpty()) {
                // This might be a Section from old format
                (subject as? com.dakkho.android.domain.model.Section)?.let { section ->
                    section.lessons.forEach { lesson ->
                        lesson.resources?.let { res ->
                            addResourcesFromLesson(res, lesson, sectionTitle, resourceItems)
                        }
                    }
                }
            }
        }
        return resourceItems
    }

    private fun addResourcesFromLesson(
        res: LessonResources,
        lesson: Lesson,
        sectionTitle: String,
        resourceItems: MutableList<ResourceItem>
    ) {
        res.lectureSheets.forEach { file ->
            resourceItems.add(file.toResourceItem("Lecture Sheet", lesson.title, sectionTitle))
        }
        res.pdfs.forEach { file ->
            resourceItems.add(file.toResourceItem("PDF", lesson.title, sectionTitle))
        }
        res.notes.forEach { file ->
            resourceItems.add(file.toResourceItem("Notes", lesson.title, sectionTitle))
        }
    }

    private fun ResourceFile.toResourceItem(typeLabel: String, lessonTitle: String, sectionTitle: String): ResourceItem {
        return ResourceItem(
            id = id,
            title = title,
            fileType = fileType ?: "pdf",
            fileSize = fileSize?.let { "${it / 1024} KB" },
            fileUrl = fileUrl ?: "",
            lessonTitle = lessonTitle,
            sectionTitle = sectionTitle
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateFilterType(type: String?) {
        _uiState.value = _uiState.value.copy(filterType = type)
    }

    fun downloadResource(resourceId: String) {
        // Mark as downloading for UI feedback
        _uiState.value = _uiState.value.copy(downloadingResourceId = resourceId)
        // In a real implementation, this would use WorkManager for background download
        // For now, we simulate marking it as downloaded after a delay
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _uiState.value = _uiState.value.copy(
                downloadingResourceId = null,
                resources = _uiState.value.resources.map {
                    if (it.id == resourceId) it.copy(isDownloaded = true) else it
                }
            )
        }
    }

    fun refresh() {
        loadResources()
    }
}
