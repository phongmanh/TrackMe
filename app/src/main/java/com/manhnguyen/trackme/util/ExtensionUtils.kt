package com.manhnguyen.trackme.util

fun Int.formatToString(): String =
    if (this.toString().length == 1) "0$this" else "$this"

fun Int.durationFormat(): String =
    when {
        this < 60 -> {
            "00:00:${this.formatToString()}"
        }
        this < 3600 -> {
            val second = this.rem(60)
            val minute = (this - second).div(60)
            "00:${minute.formatToString()}:${second.formatToString()}"
        }
        else -> {
            val hour = this.div(3600)
            val minute = (this.rem(3600)).div(60)
            val second = this - (hour * 3600) - (minute * 60)
            "${hour.formatToString()}:${minute.formatToString()}:${second.formatToString()}"
        }
    }
