package com.manhnguyen.trackme.util

import android.text.TextUtils
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {

    companion object {

        private const val SHORT_TIME_FORMAT = "HH:mm"
        private const val LONG_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss"
        private const val LONG_TIME_FORMAT_2 = "yyyy-MM-dd HH:mm:ss"

        private const val SHORT_DATE_FORMAT = "dd/MM/yyyy"
        private const val FULL_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private const val FULL_DATE_1 = "yyyy-MM-dd'T'HH:mm:ssZ"
        private const val FULL_DATE_2 = "yyyy-MM-dd'T'HH:mm:ss"
        private const val LONG_DATE_TIME_NAME = "dd MMM yyyy HH:mm"
        private const val SHORT_DATE_NAME = "dd MMM yyyy"

        fun getLocalDateFromEvent(date: String): LocalDate? {
            try {
                val instant = Instant.parse(date).atZone(ZoneId.of("UTC")).toInstant()
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()

            } catch (it: Exception) {
                it.printStackTrace()
            }
            return null
        }

        fun getLocalDateTimeFromDate(date: Date): LocalDateTime? {
            try {
                return Instant.ofEpochMilli(date.time)
                    .atZone(ZoneId.systemDefault()) //ZoneId.systemDefault()
                    .toLocalDateTime().withSecond(0).withNano(0)
            } catch (it: Exception) {
            }

            return null
        }

        fun getTimeStampFromDate(date: Date): Long {
            try {
                return Instant.ofEpochMilli(date.time)
                    .atZone(ZoneId.systemDefault())
                    .toInstant().atZone(ZoneId.of("UTC")).toEpochSecond()
            } catch (e: Exception) {
            }
            return 0
        }

        fun getLocalDateTimeTimestamp(Timestamp: Long): LocalDateTime? {
            try {
                return Instant.ofEpochSecond(Timestamp)
                    .atZone(ZoneId.of("UTC"))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            } catch (e: Exception) {
            }
            return null
        }

        fun getLocalDateTimeTimestamp2(Timestamp: Long): LocalDateTime? {
            try {
                return Instant.ofEpochMilli(Timestamp)
                    .atZone(ZoneId.of("UTC"))
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            } catch (e: Exception) {
            }
            return null
        }

        fun getLongDateFromEvent(date: String): LocalDateTime? {
            try {
                val instant = Instant.parse(date).atZone(ZoneId.of("UTC")).toInstant()
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            } catch (it: Exception) {
                it.printStackTrace()
            }
            return null
        }

        /**
         * format : dd MMM yyyy HH:mm
         *  String to String
         *  Format: yyyy-MM-dd'T'HH:mm:ssZ
         */
        fun getLongDateTimeFromStr(date: String, isFrom: Boolean): String {
            try {

                var dt = date
                if (dt.length == 11) {
                    dt = if (isFrom)
                        dt.plus(" 00:00")
                    else
                        dt.plus(" 23:00")
                }
                val formatter: DateTimeFormatter =
                    DateTimeFormatter.ofPattern(LONG_DATE_TIME_NAME)
                val dateTime = LocalDateTime.parse(dt, formatter)
                val instant =
                    dateTime.atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.of("UTC"))
                return instant.toInstant().toString()

            } catch (it: Exception) {
                it.printStackTrace()
            }
            return ""
        }

        /**
         * format: yyyy-MM-dd'T'HH:mm:ssZ
         * LocalDateTime to String
         */
        fun getLongDateTimeFromLocalDateTime(date: LocalDateTime): String {
            try {
                val instant =
                    date.atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.of("UTC"))
                        .toInstant()
                return instant.toString()
            } catch (it: Exception) {
                it.printStackTrace()
            }
            return ""
        }


        /**
         * dd/MM/yyyy HH:mm:ss
         * Date() Timestamp to String
         *
         */
        fun getLongDateTime(time: Long): String {
            try {
                val simpleDateFormat = SimpleDateFormat(LONG_TIME_FORMAT, Locale.getDefault())
                return simpleDateFormat.format(time)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        /**
         * Date() to String : yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
         */
        fun getFullDateFromDate(time: Date, create: Boolean): String {
            try {
                val c = Calendar.getInstance()
                c.time = time
                if (!create)
                    c.add(Calendar.DATE, -1)

                val instant =
                    Instant.ofEpochMilli(c.timeInMillis).atZone(ZoneId.systemDefault()).toInstant()
                        .atZone(
                            ZoneId.of("UTC")
                        ).toInstant()
                return instant.toString()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun getDateTimeFromDate(date: LocalDateTime, isFrom: Boolean): String {
            try {
                val dateValue = if (isFrom)
                    date.atZone(ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0)
                        .withNano(0).toInstant().atZone(ZoneId.of("UTC")).toInstant().toString()
                else
                    date.atZone(ZoneId.systemDefault()).withHour(23).withMinute(59).withSecond(59)
                        .withNano(0).toInstant().atZone(ZoneId.of("UTC")).toInstant().toString()
                return dateValue

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        /**
         * format : dd MMM yyyy HH:mm
         * LocalDateTime to String
         */
        fun getDateMonthNameFromDate(date: LocalDateTime, isAlDay: Boolean): String {
            try {
                val instant: Instant = date.atZone(ZoneId.systemDefault()).toInstant()
                val time: Long = instant.toEpochMilli()
                val simpleDateFormat = if (isAlDay) SimpleDateFormat(
                    SHORT_DATE_NAME,
                    Locale.getDefault()
                ) else SimpleDateFormat(LONG_DATE_TIME_NAME, Locale.getDefault())
                return simpleDateFormat.format(time)
            } catch (e: Exception) {

            }
            return ""
        }

        /**
         * from yyyy-MM-dd'T'HH:mm:ss
         *   to dd MMM yyyy HH:mm
         */
        fun getDateMonthNameFromString(dateStr: String, isAlDay: Boolean): String {
            try {
                if (TextUtils.isEmpty(dateStr)) return ""

                val instant = Instant.parse(dateStr).atZone(ZoneId.of("UTC")).toInstant()
                val time: Long = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).atZone(
                    ZoneId.systemDefault()
                ).toInstant().toEpochMilli()
                val simpleDateFormat: SimpleDateFormat = if (isAlDay)
                    SimpleDateFormat(SHORT_DATE_NAME, Locale.getDefault())
                else
                    SimpleDateFormat(LONG_DATE_TIME_NAME, Locale.getDefault())
                return simpleDateFormat.format(time)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        /**
         * format: dd MMM yyyy HH:mm
         *  String to  LocalDateTime
         */
        fun getDateTime(date: String, isFrom: Boolean): LocalDateTime? {
            try {
                val formatter: DateTimeFormatter =
                    DateTimeFormatter.ofPattern(LONG_DATE_TIME_NAME)
                if (date.length == 11)
                    return if (isFrom) LocalDateTime.parse(
                        "$date 00:00",
                        formatter
                    ) else LocalDateTime.parse("$date 23:00", formatter)
                else
                    return LocalDateTime.parse(date, formatter)
            } catch (it: Exception) {
                it.printStackTrace()
            }
            return null
        }

        /**
         * format: dd MMM yyyy HH:mm or dd MMM yyyy
         *  String to  LocalDateTime
         */
        fun getDateTime2(date: String, isAlDay: Boolean, isFrom: Boolean): String {
            try {
                val formatter =
                    DateTimeFormatter.ofPattern(LONG_DATE_TIME_NAME)
                var dt = date
                if (dt.length == 11) {
                    dt = if (isFrom)
                        dt.plus(" 00:00")
                    else
                        dt.plus(" 23:00")
                }

                val dateValue = LocalDateTime.parse(dt, formatter)
                val instant: Instant = dateValue.atZone(ZoneId.systemDefault()).toInstant()
                val simpleDateFormat: SimpleDateFormat = if (isAlDay)
                    SimpleDateFormat(SHORT_DATE_NAME, Locale.getDefault())
                else
                    SimpleDateFormat(LONG_DATE_TIME_NAME, Locale.getDefault())
                return simpleDateFormat.format(instant.toEpochMilli())
            } catch (it: Exception) {
                it.printStackTrace()
            }
            return ""
        }


        fun getShortDateWithDayOfWeek(date: LocalDateTime): String {
            try {
                val dateValue = date.atZone(ZoneId.systemDefault())
                val simpleDateFormat = SimpleDateFormat(SHORT_DATE_FORMAT, Locale.getDefault())
                val strDate = simpleDateFormat.format(dateValue.toInstant().toEpochMilli())
                val strDayOfWeek =
                    dateValue.dayOfWeek.name.toLowerCase(Locale.getDefault()).capitalize()

                return "$strDate $strDayOfWeek"
                return ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }


    }
}