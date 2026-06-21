# Phase 8: Explore Screen - Work Record

## Summary
Implemented the Explore Screen with Paging 3, Room cache, filter chips, sort dropdown, search bar, and course card grid.

## Files Created

### Data Layer
1. **`data/db/entity/RemoteKeysEntity.kt`** — Room entity for pagination remote keys (course_id, prev_key, next_key, created_at)
2. **`data/db/dao/RemoteKeysDao.kt`** — DAO for remote keys CRUD operations
3. **`data/paging/CoursePagingSource.kt`** — PagingSource that loads courses directly from API (used for search)
4. **`data/paging/CourseRemoteMediator.kt`** — RemoteMediator for Room + API cache (REFRESH/PREPEND/APPEND handling, clears stale data on refresh)

### Domain Layer
5. **`domain/model/ExploreFilters.kt`** — Data class with technology, level, priceType, sortBy filters; PriceType and SortOption enums

### Presentation Layer
6. **`presentation/screens/explore/ExploreViewModel.kt`** — Hilt ViewModel with:
   - PagingData flows (main + search) using flatMapLatest on filter changes
   - Room PagingSource + RemoteMediator for offline caching
   - SavedStateHandle persistence for filter restoration
   - Technology loading from API
   
7. **`presentation/components/explore/ExploreCourseCard.kt`** — Glassmorphism card with thumbnail (Coil), title (2 lines), instructor, rating stars, price badge, bookmark icon, level badge
8. **`presentation/components/explore/FilterChipsRow.kt`** — Horizontal scrollable filter chips (technology, level, price type)
9. **`presentation/components/explore/SortDropdown.kt`** — ExposedDropdownMenu for sort options
10. **`presentation/components/explore/ExploreSearchBar.kt`** — Expanding search bar with animated visibility
11. **`presentation/screens/explore/ExploreScreen.kt`** — Full screen with TopBar, search, filters, 2-column LazyVerticalGrid, scroll-to-top FAB, shimmer loading, empty state

## Files Modified

1. **`data/db/DakkhoDatabase.kt`** — Added RemoteKeysEntity to entities list and remoteKeysDao() abstract function
2. **`di/DatabaseModule.kt`** — Added RemoteKeysDao import and provider method
3. **`data/db/dao/CourseDao.kt`** — Added paging queries: getCoursesPaged(), getCoursesCount(), clearAllCourses(), getCoursesPagingSource()
4. **`presentation/navigation/DakkhoNavHost.kt`** — Replaced PlaceholderScreen("Explore") with ExploreScreen composable

## Architecture Decisions
- Used `courseDao.getCoursesPagingSource()` (Room PagingSource) as the pagingSourceFactory for the RemoteMediator-based Pager instead of `CoursePagingSource` (which returns CourseDto, incompatible with RemoteMediator<Int, CourseEntity>)
- Changed RemoteMediator's `initialize()` to always `LAUNCH_INITIAL_REFRESH` since filters change between Pager instances
- Clear both remote keys AND courses on REFRESH to avoid showing stale data from different filter queries
- Search flow uses direct API PagingSource (no Room cache) since search results are transient
