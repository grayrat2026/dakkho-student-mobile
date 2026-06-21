package com.dakkho.android.domain.model

data class ExploreFilters(
    val technology: String? = null,
    val level: String? = null,
    val priceType: PriceType = PriceType.ALL,
    val sortBy: SortOption = SortOption.LATEST
)

enum class PriceType(val label: String) {
    ALL("All"),
    FREE("Free"),
    PAID("Paid")
}

enum class SortOption(val label: String) {
    LATEST("Latest"),
    POPULAR("Popular"),
    RATING("Rating"),
    PRICE_LOW_HIGH("Price: Low-High"),
    PRICE_HIGH_LOW("Price: High-Low")
}
