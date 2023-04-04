package merchant.mokka.ui.client.document

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity.CENTER_VERTICAL
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import merchant.mokka.R
import merchant.mokka.utils.dp
import merchant.mokka.utils.dpf
import java.io.File

private const val TAG = "DocumentButtonView"
private val PADDING = 12.dp
private val IMAGE_SIZE = 24.dp

class DocumentButtonView @JvmOverloads constructor(
    context:Context,
    private val attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet) {

    private lateinit var documentInfoLayout: LinearLayout
    private lateinit var documentIcon: ImageView
    private lateinit var documentName: TextView
    private lateinit var documentImage: ImageView

    var title = "Title"
        set(value) {
            field = value
            documentName.text = value
        }

    init {
        initRootView()
        initDocumentInfoLayout()
        initDocumentIcon()
        initDocumentName()
        initDocumentImage()
        initAttr()
    }

    private fun initAttr() {
        if (attributeSet == null) return
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.DocumentButtonView)
        title = typeArray.getString(R.styleable.DocumentButtonView_buttonText)?:"Title"
        typeArray.recycle()
    }

    // region initial views

    private fun initRootView() {
        layoutParams = LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
        orientation = VERTICAL
        background = Color.WHITE.toDrawable()
        setRootElevation()
        setClickableAnimation()
        setPadding(PADDING)
    }

    private fun initDocumentInfoLayout() {
        documentInfoLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
            gravity = CENTER_VERTICAL
            orientation = HORIZONTAL
        }
        addView(documentInfoLayout)
    }

    private fun initDocumentIcon() {
        documentIcon = ImageView(context).apply {
            layoutParams = LayoutParams(
                IMAGE_SIZE,
                IMAGE_SIZE
            ).apply {
                setMargins(
                    4.dp, 0, 12.dp, 0
                )
            }
            setImageResource(R.drawable.ic_camera)
            setTint(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.colorAccent)
                )
            )
        }
        documentInfoLayout.addView(documentIcon)
    }

    private fun initDocumentName() {
        documentName = TextView(context).apply {
            layoutParams = LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
            text = context.getString(R.string.app_name)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(Color.BLACK)
        }
        documentInfoLayout.addView(documentName)
    }

    private fun initDocumentImage() {
        documentImage = ImageView(context).apply {
            layoutParams = LayoutParams(
                MATCH_PARENT, 170.dp
            ).apply {
                setMargins(0, PADDING, 0, 0)
            }
            isVisible = false
        }
        addView(documentImage)
    }

    //endregion



    //region public fun

    fun setImage(image: File?) {
        documentImage.isVisible = image != null
        updateButtonIcon(image != null)
        Glide.with(context)
            .load(image?.absolutePath)
            .apply(RequestOptions().centerCrop())
            .error(R.drawable.ic_error)
            .into(documentImage)
    }

    //endregion



    //region private fun

    private fun updateButtonIcon(isShow: Boolean) {
        val icon = if (isShow) R.drawable.reset_ff else R.drawable.ic_camera
        documentIcon.setImageResource(icon)
    }

    private fun setClickableAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val outValue = TypedValue()
            context.theme.resolveAttribute(
                android.R.attr.selectableItemBackground, outValue, true
            )
            foreground = getDrawable(context, outValue.resourceId)
        }
    }

    private fun setRootElevation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 4.dpf
        }
    }

    private fun ImageView.setTint(colors: ColorStateList) {
        ImageViewCompat.setImageTintList(this, colors)
    }
    //endregion
}