package com.manhnguyen.trackme.common


class Constants {
    companion object {
        val ZOOM_DEFAULT = 17f
    }

    enum class ViewType(value: String) {
        HISTORY("history"),
        RECORD("record")
    }

    sealed class Action {
        companion object {
            val RECORD = "record"
            val PAUSE = "pause"
            val RESUME = "resume"
            val STOP = "stop"
        }

    }

}