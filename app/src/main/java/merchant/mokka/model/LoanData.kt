package merchant.mokka.model

import java.io.Serializable

data class LoanData(
        val token: String,
        var clientPhone: String = "",
        var isNewClient: Boolean = false,
        var client: ClientData? = null,
        val clientIds: ClientIdPhoto = ClientIdPhoto(),
        var agrees: GdprAcceptance = GdprAcceptance(),
        var sum: Double = 0.0,
        var tariffs: List<TariffData>? = null,
        var secci: String? = null,
        var termId: Int? = null,
        var smsInfoAgree: Boolean = false,
        var insuranceAgree: Boolean? = null,
        var insuranceAvailable: Boolean = false
) : Serializable {

    constructor(loan: LoanData) : this(
            token = loan.token,
            clientPhone = loan.clientPhone,
            isNewClient = loan.isNewClient,
            client = loan.client,
            clientIds = loan.clientIds,
            agrees = loan.agrees,
            sum = loan.sum,
            tariffs = loan.tariffs,
            secci = loan.secci,
            termId = loan.termId,
            smsInfoAgree = false
    )

    val tariff
        get() = tariffs?.firstOrNull { it.term_id == termId }
}