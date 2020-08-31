package com.manhnguyen.trackme.domain.model

import com.google.android.gms.maps.model.LatLng

data class UserRecordInfo(val distance: String, val speed: String, val points: ArrayList<LatLng>)