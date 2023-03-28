package merchant.mokka.model

import java.io.Serializable

data class DocumentData(
    val name: String,
    val titleResId: Int,
    var kind: DocumentKind,
    var checked: Boolean = false,
    val checkable: Boolean = true,
    val isOptional: Boolean = false
) : Serializable

enum class DocumentKind(val urlPart: String) {
    ASP("asp"),
    AGREEMENT("agreement"),
    INDIVIDUAL("individual"),
    SECCI("secci"),
    OFFER("offer"),
    RCL("rcl"),

    SECCI_RCL_REGULAR("secci"),
    AGREEMENT_RCL_REGULAR("agreement_rcl_regular_loan"),
    INDIVIDUAL_AGREEMENT("individual_agreement"),
    AGREEMENT_RCL("individual_agreement"),
    NONE(""),
    SECCI_RCL("secci?product_kind=rcl"),
    AGREEMENT_FACTORING("individual_agreement?product_kind=factoring"),
    INDIVIDUAL_REGULAR_LOAN("agreement?product_kind=rcl"),
    SECCI_REGULAR_LOAN("secci"),
    PERSONAL_DATA("personal_data")
}