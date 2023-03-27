package pl.revo.merchant.common

import android.annotation.SuppressLint
import android.os.Bundle
import com.arellomobile.mvp.MvpDelegate
import com.github.salomonbrys.kodein.android.KodeinSupportFragment

abstract class AbsKodeinFragment : KodeinSupportFragment() {
    private val delegate by lazy { MvpDelegate(this) }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        delegate.onAttach()
    }

    override fun onResume() {
        super.onResume()
        delegate.onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
        delegate.onDetach()
    }

    override fun onStop() {
        super.onStop()

        delegate.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        delegate.onDetach()
        delegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (requireActivity().isFinishing) {
            delegate.onDestroy()
            return
        }

        // See https://github.com/Arello-Mobile/Moxy/issues/24
        var anyParentIsRemoving = false
        var parent = parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.parentFragment
        }

        if (isRemoving || anyParentIsRemoving) {
            delegate.onDestroy()
        }
    }
}