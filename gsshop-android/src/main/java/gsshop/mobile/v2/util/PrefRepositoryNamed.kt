/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.app.Activity
import android.content.Context
import com.gsshop.mocha.core.exception.JsonException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 마이그레이션에 대한 고민이 필요하다 ,
 * 생성 / 삭제시 prefName,key 기준으로 이루어지기 때문에 관리가 필요 하다
 * prefName, key 변경될 소지가 있는 경우 사용을 권장하지 않음
 *
 * prefName, key 스택틱 하게 고정된 값만 사용
 *
 * to do
 * 프리페어런스 prefName 각각의 클래스 생성이 필요 할까?
 */
object PrefRepositoryNamed {
    private val mapper = ObjectMapper()

    /**
     * 디폴트 프리퍼런스 파일명을 조회한다.
     *
     * @param context 컨텍스트
     * @return 프리퍼런스 파일명
     */
    private fun getDefaultPrefName(context: Context): String {
        return context.packageName + "_preferences"
    }

    /**
     * 키값에 저장된 value정보를 조회한다. (디폴트 프리퍼런스파일을 사용하는 경우)
     *
     * @param context 컨텍스트
     * @param key 키값
     * @return 키값에 저장된 value
     */
    @JvmStatic
    fun get(context: Context, key: String): JSONObject? {
        return get(context, getDefaultPrefName(context), key)
    }

    /**
     * 지정된 file - SharedPreferences 에서 key 값에 해당 하는 값을 json 형태의 데이터를 가져온다.
     *
     * @param context
     * @param prefName
     * @param key
     * @return
     */
    @JvmStatic
    fun get(context: Context, prefName: String, key: String): JSONObject? {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        val json = pref.getString(key, null) ?: return null
        return try {
            JSONObject(json)
        } catch (e: JSONException) {
            throw JsonException(e)
        }
    }

    /**
     * 키값에 저장된 value정보를 조회한다. (디폴트 프리퍼런스파일을 사용하는 경우)
     *
     * @param context 컨텍스트
     * @param key 키값
     * @return 키값에 저장된 value
     */
    @JvmStatic
    fun getString(context: Context, key: String): String? {
        return getString(context, getDefaultPrefName(context), key)
    }

    @JvmStatic
    fun getString(context: Context, prefName: String, key: String): String? {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        return pref.getString(key, "")
    }

    /**
     * 키값에 저장된 value정보를 조회한다. (디폴트 프리퍼런스파일을 사용하는 경우)
     *
     * @param context 컨텍스트
     * @param key 키값
     * @return 키값에 저장된 value
     */
    @JvmStatic
    fun <T> get(context: Context, key: String, clazz: Class<T>): T? {
        return get(context, getDefaultPrefName(context), key, clazz)
    }

    /**
     * 지정된 file - SharedPreferences 에서 key 값에 해당 하는 값을 json 형태의 String 으로 저장 한다.
     *
     * @return 저장된 모델 없으면 null
     * @throws JsonException
     * Json-Object 변환 실패시
     *
     * @param context
     * @param prefName
     * @param key
     * @param clazz
     * @return
     */
    @JvmStatic
    fun <T> get(context: Context, prefName: String, key: String, clazz: Class<T>): T? {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        val json = pref.getString(key, null) ?: return null
        return try {
            mapper.readValue(json, clazz)
        } catch (e: IOException) {
            throw JsonException(e)
        }
    }

    @JvmStatic
    fun <T> get(context: Context, key: String, type: TypeReference<T>): T? {
        return get(context, getDefaultPrefName(context), key, type)
    }

    /**
     * 지정된 file - SharedPreferences 에서 key 값에 저장된 해당 모델클래스 타입의 객체를 가져온다.
     *
     * @param context
     * @param prefName
     * @param key
     * @param type
     * @return
     */
    @JvmStatic
    fun <T> get(context: Context, prefName: String, key: String, type: TypeReference<T>): T? {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        val json = pref.getString(key, null) ?: return null
        return try {
            mapper.readValue(json, type)
        } catch (e: IOException) {
            throw JsonException(e)
        }
    }

    /**
     * 키값에 Object를 json으로 변환하여 저장한다. (디폴트 프리퍼런스파일을 사용하는 경우)
     *
     * @param context 컨텍스트
     * @param key 키값
     * @param v 저장할 Object
     */
    @JvmStatic
    fun save(context: Context, key: String, v: Any?) {
        save(context, getDefaultPrefName(context), key, v)
    }

    /**
     * 모델 객체를 지정된 file - SharedPreferences에 저장한다.
     *
     *
     * @param context
     * @param prefName
     * @param key
     * @param v
     */
    @JvmStatic
    fun save(context: Context, prefName: String, key: String, v: Any?) {
        if (v == null) {
            return
        }
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        val json = try {
            mapper.writeValueAsString(v)
        } catch (e: IOException) {
            throw JsonException(e)
        }
        pref.edit().putString(key, json).apply()
    }

    /**
     * 키값에 String을 저장한다. (디폴트 프리퍼런스파일을 사용하는 경우)
     *
     * @param context 컨텍스트
     * @param key 키값
     * @param v 저장할 String
     */
    @JvmStatic
    fun saveString(context: Context, key: String, v: String?) {
        saveString(context, getDefaultPrefName(context), key, v)
    }

    @JvmStatic
    fun saveString(context: Context, prefName: String, key: String, v: String?) {
        if (v == null) {
            return
        }
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        pref.edit().putString(key, v).apply()
    }

    /**
     * key값에 해당하는 Data 를 삭제한다. (디폴트 프리퍼런스파일을 사용하는 경우)
     *
     * @param context 컨텍스트
     * @param key 키값
     */
    @JvmStatic
    fun remove(context: Context, key: String) {
        remove(context, getDefaultPrefName(context), key)
    }

    /**
     * 지정된 file - SharedPreferences 에서 key 값에 해당하는 Data 를 삭제한다.
     *
     * @param context
     * @param prefName
     * @param key
     */
    @JvmStatic
    fun remove(context: Context, prefName: String, key: String) {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        pref.edit().remove(key).apply()
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String, defaultValue: Boolean): Boolean {
        return getBoolean(context, getDefaultPrefName(context), key, defaultValue)
    }

    /**
     * 지정된 file - SharedPreferences 에서 key 값에 해당하는 Data 를 얻어온다.
     *
     * @param context
     * @param prefName
     * @param key
     * @param defaultValue
     * @return
     */
    @JvmStatic
    fun getBoolean(context: Context, prefName: String, key: String, defaultValue: Boolean): Boolean {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        return pref?.getBoolean(key, defaultValue) ?: false
    }

    @JvmStatic
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        saveBoolean(context, getDefaultPrefName(context), key, value)
    }

    /**
     * 지정된 file - SharedPreferences 에서 key 값에 해당하는 Data 를 설정한다.
     *
     * @param context
     * @param prefName
     * @param key
     * @param value
     */
    @JvmStatic
    fun saveBoolean(context: Context, prefName: String, key: String, value: Boolean) {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        pref?.edit()?.putBoolean(key, value)?.apply()
    }

    fun saveInt(context: Context, key: String, value: Int) {
        saveInt(context, getDefaultPrefName(context), key, value)
    }

    @JvmStatic
    fun saveInt(context: Context, prefName: String, key: String, value: Int) {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        pref?.edit()?.putInt(key, value)?.apply()
    }

    fun saveLong(context: Context, key: String, value: Long) {
        saveLong(context, getDefaultPrefName(context), key, value)
    }

    @JvmStatic
    fun saveLong(context: Context, prefName: String, key: String, value: Long) {
        val pref = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
        pref?.edit()?.putLong(key, value)?.apply()
    }
}