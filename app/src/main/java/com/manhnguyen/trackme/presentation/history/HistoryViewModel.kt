package com.manhnguyen.trackme.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manhnguyen.trackme.domain.model.AvgTrackingDataModel
import com.manhnguyen.trackme.domain.usercase.TrackingHistoryUsecase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class HistoryViewModel @Inject constructor(private val useCase: TrackingHistoryUsecase) :
    ViewModel() {

    private val trackingHistoryData = MutableLiveData<ArrayList<AvgTrackingDataModel>>()

    fun getTrackingHistoryData(): LiveData<ArrayList<AvgTrackingDataModel>> = trackingHistoryData

     fun loadTrackingHistoryData() {
        viewModelScope.launch {
            val trackingHisData: ArrayList<AvgTrackingDataModel> =
                useCase.getTrackingHistory() as ArrayList<AvgTrackingDataModel>
            trackingHistoryData.postValue(trackingHisData)
        }
    }

}