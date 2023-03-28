package merchant.mokka.ui.updater.new_version

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView

interface NewVersionView : IBaseView {
    @StateStrategyType(SkipStrategy::class)
    fun installUpdate(filePath: Uri)
}