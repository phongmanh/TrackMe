package com.manhnguyen.trackme.domain.usercase

import com.manhnguyen.trackme.data.repository.TrackingHistoryRepo
import javax.inject.Inject

class TrackingHistoryUsecase @Inject constructor(private val repo: TrackingHistoryRepo) {
    suspend fun getTrackingHistory() = repo.getTrackingHistory()

}