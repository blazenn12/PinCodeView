package ru.zakharov.pincodeview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import ru.zakharov.R

class PinCodeView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs),
    IPinStore.Callback, View.OnClickListener {

    companion object {
        private const val X_VALUE = -100F
        private const val Y_VALUE = -50F
        private const val PIN_FINAL_POSITION = 0F
        private const val STIFFNESS = 800F
        private const val SECOND_PIN_DELAY = 100L
        private const val THIRD_PIN_DELAY = 200L
        private const val FOURTH_PIN_DELAY = 300L

        private val LAYOUT = R.layout.pin_code_view
    }

    interface OnPinCodeEnterListener {

        fun onPinCodeAttempt(pinCode: String)
    }

    private val numberZero: Button
    private val numberOne: Button
    private val numberTwo: Button
    private val numberThree: Button
    private val numberFour: Button
    private val numberFive: Button
    private val numberSix: Button
    private val numberSeven: Button
    private val numberEight: Button
    private val numberNine: Button
    private val numberList: List<Button>

    private val pins: LinearLayout
    private val pinOne: ImageView
    private val pinTwo: ImageView
    private val pinThree: ImageView
    private val pinFour: ImageView
    private val pintList: List<ImageView>

    private val cleanButton: ImageButton

    private val failAnimation: SpringAnimation
    private val pinOneSuccessAnimation: SpringAnimation
    private val pinTwoSuccessAnimation: SpringAnimation
    private val pinThreeSuccessAnimation: SpringAnimation
    private val pinFourSuccessAnimation: SpringAnimation

    private val iPinStore: IPinStore = PinStore(this)

    var failAnimationEnabled = true
    var successAnimationEnabled = true
    private var onPinCodeEnterListener: OnPinCodeEnterListener? = null

    init {
        LayoutInflater.from(context).inflate(LAYOUT, this)

        numberZero = rootView.findViewById(R.id.b_0)
        numberOne = rootView.findViewById(R.id.b_1)
        numberTwo = rootView.findViewById(R.id.b_2)
        numberThree = rootView.findViewById(R.id.b_3)
        numberFour = rootView.findViewById(R.id.b_4)
        numberFive = rootView.findViewById(R.id.b_5)
        numberSix = rootView.findViewById(R.id.b_6)
        numberSeven = rootView.findViewById(R.id.b_7)
        numberEight = rootView.findViewById(R.id.b_8)
        numberNine = rootView.findViewById(R.id.b_9)
        pins = rootView.findViewById(R.id.ll_pins)
        pinOne = rootView.findViewById(R.id.iv_pin_1)
        pinTwo = rootView.findViewById(R.id.iv_pin_2)
        pinThree = rootView.findViewById(R.id.iv_pin_3)
        pinFour = rootView.findViewById(R.id.iv_pin_4)
        cleanButton = rootView.findViewById(R.id.ib_clean)
        cleanButton.visibility = View.INVISIBLE

        pintList = listOf(
            pinOne,
            pinTwo,
            pinThree,
            pinFour
        )

        numberList = listOf(
            numberZero,
            numberOne,
            numberTwo,
            numberThree,
            numberFour,
            numberFive,
            numberSix,
            numberSeven,
            numberEight,
            numberNine
        )

        failAnimation = SpringAnimation(pins, DynamicAnimation.TRANSLATION_X, PIN_FINAL_POSITION)
        pinOneSuccessAnimation =
            SpringAnimation(pinOne, DynamicAnimation.TRANSLATION_Y, PIN_FINAL_POSITION)
        pinTwoSuccessAnimation =
            SpringAnimation(pinTwo, DynamicAnimation.TRANSLATION_Y, PIN_FINAL_POSITION)
        pinThreeSuccessAnimation =
            SpringAnimation(pinThree, DynamicAnimation.TRANSLATION_Y, PIN_FINAL_POSITION)
        pinFourSuccessAnimation =
            SpringAnimation(pinFour, DynamicAnimation.TRANSLATION_Y, PIN_FINAL_POSITION)

        initButtons()

        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.PinCodeView, 0, 0)
            successAnimationEnabled =
                ta.getBoolean(R.styleable.PinCodeView_PCV_enableSuccessAnimation, true)
            failAnimationEnabled =
                ta.getBoolean(R.styleable.PinCodeView_PCV_enableFailAnimation, true)
            val buttonTextColor = ta.getColor(
                R.styleable.PinCodeView_PCV_buttonTextColor,
                resources.getColor(android.R.color.white, null)
            )

            val buttonTintColor = ta.getColor(
                R.styleable.PinCodeView_PCV_buttonTintColor,
                getColorFromAttr(android.R.attr.colorPrimary)
            )
            val pinSrc = ta.getDrawable(R.styleable.PinCodeView_PCV_pinsSrc)
            val pinTint = ta.getColor(
                R.styleable.PinCodeView_PCV_pinColor,
                getColorFromAttr(android.R.attr.colorPrimary)
            )
            val buttonBackground = ta.getDrawable(R.styleable.PinCodeView_PCV_buttonBackground)
            val cleanSrc = ta.getDrawable(R.styleable.PinCodeView_PCV_cleanSrc)
            val cleanBackground = ta.getDrawable(R.styleable.PinCodeView_PCV_cleanBackground)
            val cleanBackgroundTint = ta.getColor(
                R.styleable.PinCodeView_PCV_cleanTint,
                getColorFromAttr(android.R.attr.colorPrimary)
            )
            ta.recycle()

            setButtonTextColor(buttonTextColor)
            setButtonBackground(buttonBackground)
            setButtonBackgroundTintColor(buttonTintColor)
            setCleanButtonTintColor(cleanBackgroundTint)
            setCleanButtonBackground(cleanBackground)
            setCleanButtonSrc(cleanSrc)
            setPinsSrc(pinSrc)
            setPinsTint(pinTint)
        }
    }

    fun addOnPinCodeEnterListener(listener: OnPinCodeEnterListener) {
        onPinCodeEnterListener = listener
    }

    fun removeOnPinCodeEnterListener() {
        onPinCodeEnterListener = null
    }

    fun setPinsTint(@ColorInt color: Int) {
        for (i in pintList.indices) {
            pintList[i].imageTintList = ColorStateList.valueOf(color)
        }
    }

    /**
     *  Set src on each pin.
     *  For correct behavior, drawable should include selector with 2 items
     *  and one of them with state_activated = true
     */
    fun setPinsSrc(drawable: Drawable?) {
        drawable?.let {
            for (i in pintList.indices) {
                pintList[i].setImageDrawable(drawable)
            }
        }
    }

    fun setCleanButtonSrc(drawable: Drawable?) {
        drawable?.let {
            cleanButton.setImageDrawable(it)
        }
    }

    fun setCleanButtonBackground(drawable: Drawable?) {
        drawable?.let {
            cleanButton.background = it
        }
    }

    fun setCleanButtonTintColor(@ColorInt color: Int) {
        cleanButton.backgroundTintList = ColorStateList.valueOf(color)
    }

    fun setButtonTextColor(@ColorInt color: Int) {
        for (i in numberList.indices) {
            numberList[i].setTextColor(color)
        }
    }

    fun setButtonBackground(drawable: Drawable?) {
        drawable?.let {
            for (i in numberList.indices) {
                numberList[i].background = drawable
            }
        }
    }

    fun setButtonBackgroundTintColor(@ColorInt color: Int) {
        for (i in numberList.indices) {
            numberList[i].backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun initButtons() {
        for (i in numberList.indices) {
            numberList[i].tag = i
            numberList[i].setOnClickListener(this)
        }

        cleanButton.setOnClickListener {
            clean()
        }
    }

    private fun failAnimation() {
        if (failAnimationEnabled) {
            failAnimation.apply {
                setStartValue(X_VALUE)
                spring.stiffness = STIFFNESS
                addEndListener { _, _, _, _ ->
                    clean()
                }
                start()
            }
        } else {
            clean()
        }
    }

    private fun successAnimation() {
        if (successAnimationEnabled) {
            pinOneSuccessAnimation.apply {
                setStartValue(Y_VALUE)
                spring.stiffness = STIFFNESS
                handler.postDelayed({ pinTwoSuccessAnimation.start() }, SECOND_PIN_DELAY)
            }

            pinTwoSuccessAnimation.apply {
                setStartValue(Y_VALUE)
                spring.stiffness = STIFFNESS
                handler.postDelayed({ pinThreeSuccessAnimation.start() }, THIRD_PIN_DELAY)
            }

            pinThreeSuccessAnimation.apply {
                setStartValue(Y_VALUE)
                spring.stiffness = STIFFNESS
                handler.postDelayed({ pinFourSuccessAnimation.start() }, FOURTH_PIN_DELAY)
            }

            pinFourSuccessAnimation.apply {
                setStartValue(Y_VALUE)
                spring.stiffness = STIFFNESS
                addEndListener { _, _, _, _ ->
                    clean()
                }
            }

            pinOneSuccessAnimation.start()
        } else {
            clean()
        }
    }

    private fun clean() {
        cleanButton.visibility = View.INVISIBLE
        pinOne.isActivated = false
        pinTwo.isActivated = false
        pinThree.isActivated = false
        pinFour.isActivated = false
        iPinStore.clean()
    }

    fun onFail() {
        cleanButton.visibility = View.INVISIBLE
        failAnimation()
    }

    fun onSuccess() {
        cleanButton.visibility = View.INVISIBLE
        successAnimation()
    }

    override fun onCompleted(pinCode: String) {
        onPinCodeEnterListener?.onPinCodeAttempt(pinCode)
    }

    override fun onPinAdd(pinNumber: Int) {
        when (pinNumber) {
            0 -> {
                pinOne.isActivated = true
                cleanButton.visibility = View.VISIBLE
            }
            1 -> pinTwo.isActivated = true
            2 -> pinThree.isActivated = true
            3 -> {
                pinFour.isActivated = true
            }
        }
    }

    override fun onClick(v: View?) {
        iPinStore.add(v?.tag as Int)
    }

    @ColorInt
    fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }
}