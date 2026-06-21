package com.dakkho.android.presentation.screens.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SupportApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.FAQCategory
import com.dakkho.android.domain.model.FAQItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FAQUiState(
    val isLoading: Boolean = false,
    val categories: List<FAQCategory> = emptyList(),
    val filteredCategories: List<FAQCategory> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class FAQViewModel @Inject constructor(
    private val apiService: SupportApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(FAQUiState())
    val uiState: StateFlow<FAQUiState> = _uiState.asStateFlow()

    init {
        loadFAQs()
    }

    private fun loadFAQs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = apiService.getFAQs()
                if (response.isSuccessful) {
                    val result = response.body()
                    val categories = result?.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories,
                            filteredCategories = categories
                        )
                    }
                } else {
                    // Fallback to mock data on API failure
                    loadMockData()
                }
            } catch (e: Exception) {
                // Fallback to mock data on network error
                loadMockData()
            }
        }
    }

    private fun loadMockData() {
        val mockCategories = listOf(
            FAQCategory(
                id = "courses",
                title = "কোর্স সম্পর্কিত",
                questions = listOf(
                    FAQItem(
                        id = "c1",
                        question = "কিভাবে কোর্সে এনরোল করবো?",
                        answer = "কোর্সের বিস্তারিত পৃষ্ঠায় গিয়ে 'এনরোল করুন' বাটনে ক্লিক করুন। পেমেন্ট সম্পন্ন করার পর আপনি সাথে সাথে কোর্সে অ্যাক্সেস পাবেন।",
                        category = "courses"
                    ),
                    FAQItem(
                        id = "c2",
                        question = "কোর্সের মেয়াদ শেষ হলে কি হবে?",
                        answer = "কোর্সের মেয়াদ শেষ হলেও আপনি ডাউনলোড করা কন্টেন্ট দেখতে পারবেন। তবে নতুন আপডেট বা লাইভ ক্লাসে অংশ নিতে পুনরায় এনরোল করতে হবে।",
                        category = "courses"
                    ),
                    FAQItem(
                        id = "c3",
                        question = "একাধিক কোর্সে একসাথে এনরোল করা যায় কি?",
                        answer = "হ্যাঁ, আপনি একাধিক কোর্সে একসাথে এনরোল করতে পারেন। প্রতিটি কোর্সের প্রোগ্রেস আলাদাভাবে সেভ হবে।",
                        category = "courses"
                    )
                )
            ),
            FAQCategory(
                id = "payment",
                title = "পেমেন্ট সম্পর্কিত",
                questions = listOf(
                    FAQItem(
                        id = "p1",
                        question = "কোন পেমেন্ট মেথড সাপোর্ট করে?",
                        answer = "আমরা বিকাশ, নগদ, রকেট, ক্রেডিট/ডেবিট কার্ড এবং ব্যাংক ট্রান্সফার সাপোর্ট করি। সকল পেমেন্ট SSL এনক্রিপ্টেড ও নিরাপদ।",
                        category = "payment"
                    ),
                    FAQItem(
                        id = "p2",
                        question = "রিফান্ড পলিসি কি?",
                        answer = "কোর্স শুরুর ৭ দিনের মধ্যে আপনি সম্পূর্ণ রিফান্ড পাবেন। এর পরে আংশিক রিফান্ড প্রযোজ্য হতে পারে। বিস্তারিত জানতে রিফান্ড পলিসি পড়ুন।",
                        category = "payment"
                    ),
                    FAQItem(
                        id = "p3",
                        question = "ইনস্টলমেন্টে কোর্স কিনতে পারবো কি?",
                        answer = "হ্যাঁ, নির্বাচিত কোর্সে ইএমআই সুবিধা পাওয়া যায়। কোর্সের বিস্তারিত পৃষ্ঠায় ইএমআই অপশন দেখুন।",
                        category = "payment"
                    )
                )
            ),
            FAQCategory(
                id = "technical",
                title = "প্রযুক্তিগত সমস্যা",
                questions = listOf(
                    FAQItem(
                        id = "t1",
                        question = "ভিডিও প্লে হচ্ছে না, কি করবো?",
                        answer = "প্রথমে ইন্টারনেট সংযোগ পরীক্ষা করুন। তারপর অ্যাপ ক্যাশে মুছে আবার চেষ্টা করুন। সমস্যা থাকলে ভিডিও কোয়ালিটি কমিয়ে দেখুন বা সাপোর্টে যোগাযোগ করুন।",
                        category = "technical"
                    ),
                    FAQItem(
                        id = "t2",
                        question = "অফলাইনে কোর্স দেখা যায় কি?",
                        answer = "হ্যাঁ, কোর্স ডাউনলোড করে অফলাইনে দেখতে পারবেন। সেটিংস থেকে ডাউনলোড কোয়ালিটি নির্বাচন করুন। ডাউনলোড কন্টেন্ট ৩০ দিন পর্যন্ত অফলাইনে দেখা যাবে।",
                        category = "technical"
                    )
                )
            )
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                categories = mockCategories,
                filteredCategories = mockCategories,
                error = null
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applySearchFilter(query)
    }

    private fun applySearchFilter(query: String) {
        val currentCategories = _uiState.value.categories

        if (query.isBlank()) {
            _uiState.update { it.copy(filteredCategories = currentCategories) }
            return
        }

        val filtered = currentCategories.mapNotNull { category ->
            val matchingQuestions = category.questions.filter { faqItem ->
                faqItem.question.contains(query, ignoreCase = true) ||
                    faqItem.answer.contains(query, ignoreCase = true)
            }
            if (matchingQuestions.isNotEmpty()) {
                category.copy(questions = matchingQuestions)
            } else {
                null
            }
        }

        _uiState.update { it.copy(filteredCategories = filtered) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
