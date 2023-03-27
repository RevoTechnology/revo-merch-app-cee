package pl.revo.merchant.model

import pl.revo.merchant.common.ToolbarStyle
import java.io.Serializable

data class HelpDto(
        val toolbarStyle: ToolbarStyle,
        val helpRes: String
) : Serializable