package merchant.mokka.model

import merchant.mokka.pref.Prefs

data class AutoAgentData(
        var storeId: Int? = null,
        var agentPhone: String? = null,
        var merchantAgentId: String? = null,
        var orderId: String? = null,
        var amount: Float? = null,
        var phone: String? = null
) {
    val isValid: Boolean
        get() = storeId != null && agentPhone != null && merchantAgentId != null && orderId != null && amount != null && phone != null

    fun update(storeId: Int? = null,
               agentPhone: String? = null,
               merchantAgentId: String? = null,
               orderId: String? = null,
               amount: Float? = null,
               phone: String? = null,
               forceUpdate: Boolean = false,
               resetAuth: Boolean = false) = apply {

        if (storeId != null || forceUpdate) this.storeId = storeId
        if (agentPhone != null || forceUpdate) this.agentPhone = agentPhone
        if (merchantAgentId != null || forceUpdate) this.merchantAgentId = merchantAgentId
        if (orderId != null || forceUpdate) this.orderId = orderId
        if (amount != null || forceUpdate) this.amount = amount
        if (phone != null || forceUpdate) this.phone = phone

        if(resetAuth) Prefs.token = ""
    }


}