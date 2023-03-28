package merchant.mokka.model

import merchant.mokka.common.ToolbarStyle
import java.io.Serializable

data class HelpDto(
    val toolbarStyle: ToolbarStyle,
    val helpRes: String
) : Serializable