package pl.revo.merchant.repository

import com.squareup.moshi.Moshi
import pl.revo.merchant.api.adapter.DateAdapter
import pl.revo.merchant.api.adapter.MoshiUtils
import pl.revo.merchant.api.error.LamodaInvalidResult
import pl.revo.merchant.api.error.LamodaInvalidSignature
import pl.revo.merchant.api.request.*
import pl.revo.merchant.model.AutoAgentData
import pl.revo.merchant.pref.Prefs
import pl.revo.merchant.utils.sha1

private const val SECRET_KEY = "0aba16fd742571e091a2fb6da161ae4b"
private const val LAMODA_TOKEN = "2b4bdcc8fa57b6dd402d"

class LamodaRepository {
    private val moshi = MoshiUtils.moshi().build()
    val tokenValid: Boolean
        get() = Prefs.token == LAMODA_TOKEN

    fun parse(body: String?, autoAgentData: AutoAgentData) {
        val jsonAdapter = moshi.adapter<LamodaRes>(LamodaRes::class.java).lenient()
        val lamodaRes = jsonAdapter.fromJson(body) ?: throw Exception("Invalid json structure")
        val signature = signature(cl = LamodaPayloadRes::class.java, item = lamodaRes.payload, moshi = moshi)

        if (signature != lamodaRes.credentials.signature) throw LamodaInvalidSignature()
        autoAgentData.update(
                storeId = lamodaRes.credentials.storeId,
                agentPhone = lamodaRes.payload.agentPhone,
                merchantAgentId = lamodaRes.payload.merchantAgentId,
                orderId = lamodaRes.payload.orderId,
                amount = lamodaRes.payload.amount,
                phone = lamodaRes.payload.phone
        )

        // store lamoda auth token
        Prefs.token = LAMODA_TOKEN
    }

    fun result(lamoda: AutoAgentData, credentials: LamodaCredentialsRes?, payload: LamodaLoanPayloadRes?): String? {
        credentials ?: return null
        payload ?: return null

        val request = LamodaLoanRes(
                credentials = credentials,
                payload = payload)

        // clean out lamoda agent and it's auth token
        lamoda.update(forceUpdate = true)

//        request.credentials.signature = signature(cl = LamodaLoanPayloadRes::class.java, item = request.payload, moshi = moshi)
        if (!request.isValid) throw LamodaInvalidResult()

        return moshi
                .adapter(LamodaLoanRes::class.java)
                .toJson(request) ?: throw LamodaInvalidResult()
    }

    private fun <T> signature(item: T, cl: Class<T>, moshi: Moshi): String {
        val map = MoshiUtils.toMap(cl = cl, item = item, moshi = moshi)
        return (map?.keys?.sorted()?.map { map[it] }
                ?.joinToString("") + SECRET_KEY)
                .sha1()
    }

    fun test(autoAgentData: AutoAgentData) {
        val agents = listOf(
                "79991002030", // 0 - default merchant
                "79255632688", // 1 - merchant no insurance
                "78884112555" // 2 - merchant bill finalization
        )

        autoAgentData.update(agentPhone = agents[2])
    }


}