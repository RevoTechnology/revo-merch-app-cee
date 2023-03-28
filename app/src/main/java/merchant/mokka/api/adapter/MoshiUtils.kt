package merchant.mokka.api.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiUtils {
    fun moshi(): Moshi.Builder = Moshi.Builder()
        .add(DateAdapter())
        .add(KotlinJsonAdapterFactory())

    fun <T> toMap(item: T, cl: Class<T>, moshi: Moshi = moshi().build()): Map<String, Any>? {
        val itemType = Types.newParameterizedType(cl, cl)
        val json = moshi.adapter<T>(itemType).toJson(item)

        val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val adapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)

        return adapter.fromJson(json)
    }
}