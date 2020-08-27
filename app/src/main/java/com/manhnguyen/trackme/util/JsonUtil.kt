package com.manhnguyen.trackme.util

import android.text.TextUtils
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.util.*

class JsonUtil  {
     var gson: Gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create()

    fun isJSONValid(jsonInString: String?, classOfT: Class<*>?): Boolean {
        return if (TextUtils.isEmpty(jsonInString)) false else try {
            gson.fromJson(jsonInString, classOfT)
            true
        } catch (ex: JsonSyntaxException) {
            false
        }
    }

    fun <T> stringToArray(
        s: String?,
        clazz: Class<Array<T>?>?
    ): List<T> {
        val arr = gson.fromJson(s, clazz)!!
        return Arrays.asList(*arr)
    }

    companion object {
        val instance by lazy { JsonUtil() }
    }

    fun <T> parse2Model(jsonData: String?, cls: Class<T>?): T {
        val jsonParser = JsonParser()
        val jsonElement = jsonParser.parse(jsonData)
        val gson = Gson()
        return gson.fromJson(jsonElement, cls)
    }

    fun <T> parse2ListModel(
        jsonData: String?,
        cls: Class<T>?
    ): List<T> {
        val jsonParser = JsonParser()
        val jsonElement = jsonParser.parse(jsonData)
        val gson = Gson()
        return Gson().fromJson(
            jsonData,
            object : TypeToken<ArrayList<T>?>() {}.type
        )
    }

    fun parseModel2Json(obj: Any?): String {
        return try {
            val gson = Gson()
            gson.toJson(obj)
        } catch (e: Exception) {
            ""
        }
    }
}
