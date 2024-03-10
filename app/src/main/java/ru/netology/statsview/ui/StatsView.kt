package ru.netology.statsview.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWide = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(
            attributeSet,
            R.styleable.StatsView
        ) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWide = getDimension(R.styleable.StatsView_lineWidth, lineWide.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_colors1, randomColor()),
                getColor(R.styleable.StatsView_colors2, randomColor()),
                getColor(R.styleable.StatsView_colors3, randomColor()),
                getColor(R.styleable.StatsView_colors4, randomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            val newValue = value.map {
                it / value.sum()
            }
//            println("value $newValue")
            field = newValue
            invalidate()
        }
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
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
        var countData = data.count()
        data.forEachIndexed() { index, datum ->
            val angel = datum * 360
            if (index == countData - 1) {
                paint.color = 0x55999999.toInt()

            } else {
                paint.color = colors.getOrElse(index) { randomColor() }
            }
            canvas.drawArc(oval, startEngel, angel, false, paint)
            startEngel += angel
        }

        canvas.drawText(
            "%.2f%%".format((data.sum() - data.get(countData - 1)) * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun randomColor() = kotlin.random.Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}