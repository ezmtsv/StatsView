package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWide = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var valueAnimator: ValueAnimator? = null
    private var progress = 0F
    private var modeAnimation: Int = 0
    private var currentIndexAngel = 0
    private var sumAngel = 0F

    var data: List<Float> = emptyList()
        set(value) {
            val newValue = value.map {
                it / value.sum()
            }
            field = newValue
            update()
            //invalidate()
        }

    init {
        context.withStyledAttributes(
            attributeSet,
            R.styleable.StatsView
        ) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWide = getDimension(R.styleable.StatsView_lineWidth, lineWide.toFloat()).toInt()
            modeAnimation = getInteger(R.styleable.StatsView_modeAnimation, 0)
            colors = listOf(
                getColor(R.styleable.StatsView_colors1, randomColor()),
                getColor(R.styleable.StatsView_colors2, randomColor()),
                getColor(R.styleable.StatsView_colors3, randomColor()),
                getColor(R.styleable.StatsView_colors4, randomColor()),
            )

        }
    }

    var dataStr: String = ""
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            strokeWidth = lineWide.toFloat()
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND

        }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            textSize = this@StatsView.textSize
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWide
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )
    }

    @SuppressLint("DrawAllocation", "ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var startEngel = -90F
        val countData = data.count()
        var colorCircle = 0

        when (modeAnimation) {
            1 -> {
                data.forEachIndexed { index, datum ->
                    val angel = datum * 360
                    if (index == countData - 1) {
                        paint.color = 0xFFffffff.toInt()
                        val endAngel = (-360 + angel) * PI / 180
                        val endX = center.x - radius * sin(endAngel).toFloat()
                        val endY = center.y - radius * cos(endAngel).toFloat()
                        canvas.drawPoint(endX, endY, paint)

                        paint.color = 0x55999999

                    } else {
                        paint.color = colors.getOrElse(index) { randomColor() }
                        if (index == 0) {
                            colorCircle = paint.color
                        }
                    }
                    canvas.drawArc(oval, startEngel, angel * progress, false, paint)
                    startEngel += angel
                }
                paint.color = colorCircle
                canvas.drawPoint(center.x, center.y - radius, paint)
            }

            2 -> {

                var angel = 360 * progress
                startEngel = -90F

                if (currentIndexAngel == 0) sumAngel = data[0] * 360

                if (angel > sumAngel) {
                    currentIndexAngel++
                    sumAngel += data[currentIndexAngel] * 360
                }
                when (currentIndexAngel) {
                    0 -> {
                        paint.color = colors.getOrElse(currentIndexAngel) { randomColor() }
                        canvas.drawArc(oval, startEngel, angel, false, paint)
                    }

                    1 -> {

                        angel -= data[0] * 360
                        paint.color = colors.getOrElse(currentIndexAngel - 1) { randomColor() }
                        canvas.drawArc(oval, startEngel, data[0] * 360, false, paint)

                        if (currentIndexAngel == countData - 1) paint.color = 0x55999999
                        else paint.color = colors.getOrElse(currentIndexAngel) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + data[0] * 360,
                            angel * progress,
                            false,
                            paint
                        )
                    }

                    2 -> {
                        angel -= (data[0] + data[1]) * 360
                        paint.color = colors.getOrElse(currentIndexAngel - 2) { randomColor() }
                        canvas.drawArc(oval, startEngel, data[0] * 360, false, paint)

                        paint.color = colors.getOrElse(currentIndexAngel - 1) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + data[0] * 360,
                            data[1] * 360,
                            false,
                            paint
                        )

                        if (currentIndexAngel == countData - 1) paint.color = 0x55999999
                        else paint.color = colors.getOrElse(currentIndexAngel) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + (data[0] + data[1]) * 360,
                            angel * progress,
                            false,
                            paint
                        )
                    }

                    3 -> {
                        angel -= (data[0] + data[1] + data[2]) * 360
                        paint.color = colors.getOrElse(currentIndexAngel - 3) { randomColor() }
                        canvas.drawArc(oval, startEngel, data[0] * 360, false, paint)

                        paint.color = colors.getOrElse(currentIndexAngel - 2) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + data[0] * 360,
                            data[1] * 360,
                            false,
                            paint
                        )

                        paint.color = colors.getOrElse(currentIndexAngel - 1) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + (data[0] + data[1]) * 360,
                            data[2] * 360,
                            false,
                            paint
                        )

                        if (currentIndexAngel == countData - 1) paint.color = 0x55999999
                        else paint.color = colors.getOrElse(currentIndexAngel) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + (data[0] + data[1] + data[2]) * 360,
                            angel * progress,
                            false,
                            paint
                        )
                    }

                    4 -> {
                        angel -= (data[0] + data[1] + data[2] + data[3]) * 360
                        paint.color = colors.getOrElse(currentIndexAngel - 4) { randomColor() }
                        canvas.drawArc(oval, startEngel, data[0] * 360, false, paint)

                        paint.color = colors.getOrElse(currentIndexAngel - 3) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + data[0] * 360,
                            data[1] * 360,
                            false,
                            paint
                        )

                        paint.color = colors.getOrElse(currentIndexAngel - 2) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + (data[0] + data[1]) * 360,
                            data[2] * 360,
                            false,
                            paint
                        )

                        paint.color = colors.getOrElse(currentIndexAngel - 1) { randomColor() }
                        canvas.drawArc(
                            oval,
                            startEngel + (data[0] + data[1] + data[2]) * 360,
                            data[3] * 360,
                            false,
                            paint
                        )

                        paint.color = 0x55999999
                        canvas.drawArc(
                            oval,
                            startEngel + (data[0] + data[1] + data[2] + data[3]) * 360,
                            angel * progress,
                            false,
                            paint
                        )

                    }

                }
            }

            3 -> {
                startEngel = -90F + data[0] * 360 / 2
                data.forEachIndexed { index, datum ->
                    val angel = datum * 360
                    paint.color = colors.getOrElse(index) { randomColor() }

                    if (index == countData - 1) {
                        paint.color = 0xff999999.toInt()
                    }
                    canvas.drawArc(oval, startEngel, angel * progress / 2, false, paint)
                    canvas.drawArc(oval, startEngel, -angel * progress / 2, false, paint)

                    if (index != countData - 1) startEngel += angel / 2 + data[index + 1] * 360 / 2
                }
            }
        }


//        canvas.drawText(
//            "%.2f%%".format((data.sum() - data.get(countData - 1)) * 100),
//            center.x,
//            center.y + textPaint.textSize / 4,
//            textPaint
//        )

    }

    @SuppressLint("Recycle")
    private fun update() {
        dataStr = "%.2f%%".format((data.sum() - data[data.count() - 1]) * 100)
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F
        valueAnimator = ValueAnimator.ofFloat(0F, 1F)
            .apply {
                addUpdateListener { anim ->
                    progress = anim.animatedValue as Float
                    if (modeAnimation == 1) rotation = 360 * progress
                    invalidate()
                }
                duration = 1500
                //startDelay = delay.toLong()

                interpolator = LinearInterpolator()
            }.also {
                it.start()
            }


    }

    private fun randomColor() = kotlin.random.Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}