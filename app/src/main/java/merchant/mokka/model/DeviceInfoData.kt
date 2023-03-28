package merchant.mokka.model

import com.squareup.moshi.JsonAdapter
import merchant.mokka.api.adapter.MoshiUtils
import merchant.mokka.api.request.DeviceInfoReq


data class DeviceInfoData(
        val uuid: String,
        val info: DeviceInfoReq
) {
    fun toMap(): Map<String, Any> = try {
        val moshi = MoshiUtils.moshi().build()
        val adapter: JsonAdapter<Any> = moshi.adapter(Any::class.java)
        val jsonStructure: Any = adapter.toJsonValue(info) ?: throw Exception("Invalid json structure")
        val jsonMap = jsonStructure as Map<*, *>
        jsonMap.map { it.key.toString() to it.value.toString() }.toMap()

    } catch (e: Exception) {
        HashMap()
    }

}