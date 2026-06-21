package com.dakkho.android.presentation.screens.examtips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.ExamApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ExamModels.ExamTip
import com.dakkho.android.domain.model.ExamModels.TipCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ExamTipsUiState(
    val isLoading: Boolean = false,
    val tips: List<ExamTip> = emptyList(),
    val filteredTips: List<ExamTip> = emptyList(),
    val selectedCategory: TipCategory? = null,
    val todayTip: ExamTip? = null,
    val error: String? = null
)

@HiltViewModel
class ExamTipsViewModel @Inject constructor(
    private val examApiService: ExamApiService,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamTipsUiState())
    val uiState: StateFlow<ExamTipsUiState> = _uiState.asStateFlow()

    init {
        loadTips()
    }

    private fun loadTips() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = examApiService.getExamTips()
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    val tips = apiResult?.data ?: emptyList()
                    val todayTip = selectTodayTip(tips)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tips = tips,
                            filteredTips = tips,
                            todayTip = todayTip
                        )
                    }
                } else {
                    loadFallbackData()
                }
            } catch (e: Exception) {
                loadFallbackData()
            }
        }
    }

    private fun loadFallbackData() {
        val sampleTips = listOf(
            ExamTip(
                id = "t1", title = "পমোডোরো কৌশল",
                content = "২৫ মিনিট পড়াশোনা করুন, ৫ মিনিট বিশ্রাম নিন। এই কৌশলটি মনোযোগ বাড়াতে সাহায্য করে এবং মানসিক ক্লান্তি কমায়।",
                category = TipCategory.STUDY_HACK
            ),
            ExamTip(
                id = "t2", title = "সকালে পড়াশোনা",
                content = "সকালের সময়ে মস্তিষ্ক সবচেয়ে সতেজ থাকে। কঠিন বিষয়গুলো সকালে পড়ুন।",
                category = TipCategory.STUDY_HACK
            ),
            ExamTip(
                id = "t3", title = "সময় ব্লক করুন",
                content = "প্রতিটি বিষয়ের জন্য নির্দিষ্ট সময় বরাদ্দ করুন। ক্যালেন্ডার ব্যবহার করে সময়সূচী মেনে চলুন।",
                category = TipCategory.TIME_MANAGEMENT
            ),
            ExamTip(
                id = "t4", title = "পরীক্ষার আগে রিভিশন",
                content = "পরীক্ষার একদিন আগে নতুন কিছু শুরু করবেন না। শুধু পুরনো পড়া রিভিশন করুন।",
                category = TipCategory.EXAM_STRATEGY
            ),
            ExamTip(
                id = "t5", title = "পর্যাপ্ত ঘুম",
                content = "পরীক্ষার আগের রাতে কমপক্ষে ৭-৮ ঘন্টা ঘুমান। ঘুম স্মৃতিশক্তি বাড়ায়।",
                category = TipCategory.WELLNESS
            ),
            ExamTip(
                id = "t6", title = "অ্যাক্টিভ রিকল",
                content = "পড়ার পর বই বন্ধ করে নিজেকে প্রশ্ন করুন। এই পদ্ধতিতে মনে রাখার ক্ষমতা ৫০% বাড়ে।",
                category = TipCategory.STUDY_HACK
            ),
            ExamTip(
                id = "t7", title = "প্রশ্ন পড়ুন সাবধানে",
                content = "পরীক্ষায় উত্তর লেখার আগে প্রশ্নটি অন্তত দুইবার পড়ুন। অনেক সময় ভুল বোঝার কারণে ভুল উত্তর হয়।",
                category = TipCategory.EXAM_STRATEGY
            ),
            ExamTip(
                id = "t8", title = "ব্রেক নিন",
                content = "প্রতি ৪৫-৬০ মিনিট পড়ার পর ১০-১৫ মিনিট ব্রেক নিন। হাঁটাহাঁটি করুন বা চোখ বন্ধ করে বিশ্রাম নিন।",
                category = TipCategory.WELLNESS
            )
        )
        val todayTip = selectTodayTip(sampleTips)
        _uiState.update {
            it.copy(
                isLoading = false,
                tips = sampleTips,
                filteredTips = sampleTips,
                todayTip = todayTip
            )
        }
    }

    private fun selectTodayTip(tips: List<ExamTip>): ExamTip? {
        if (tips.isEmpty()) return null
        val dayOfYear = LocalDate.now().dayOfYear
        return tips[dayOfYear % tips.size]
    }

    fun filterByCategory(category: TipCategory?) {
        _uiState.update { current ->
            val filtered = if (category == null) {
                current.tips
            } else {
                current.tips.filter { it.category == category }
            }
            current.copy(selectedCategory = category, filteredTips = filtered)
        }
    }

    fun getCategoryDisplayName(category: TipCategory): String {
        return category.label
    }

    fun getCategoryIcon(category: TipCategory): String {
        return when (category) {
            TipCategory.STUDY_HACK -> "📖"
            TipCategory.TIME_MANAGEMENT -> "⏰"
            TipCategory.EXAM_STRATEGY -> "🎯"
            TipCategory.WELLNESS -> "🧘"
        }
    }

    fun retry() {
        loadTips()
    }
}
