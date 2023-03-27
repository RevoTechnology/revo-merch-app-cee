package pl.revo.merchant.model

data class MemoryCashedData(
        var agentLogin: String? = null,
        var agentPin: String? = null,
        var agentData: AgentData? = null,
        var remoteVersion: String? = null,
        var demo: Boolean = false
)