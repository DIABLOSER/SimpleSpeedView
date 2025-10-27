package io.github.diabloser

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.apply
import kotlin.let
import kotlin.math.max
import kotlin.ranges.coerceIn
import kotlin.ranges.until
import kotlin.text.format

class SimpleSpeedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 圆环属性
    private var startAngle: Float = 135f // 起始角度（默认-45度，即135度）
    private var sweepAngle: Float = 270f // 扫过的角度（默认270度）
    private var arcBackgroundColor: Int = Color.LTGRAY // 圆环背景色
    private var arcProgressColor: Int = Color.parseColor("#00BCD4") // 圆环进度色
    private var arcWidth: Float = 30f // 圆环宽度
    private var arcBorderColor: Int = Color.TRANSPARENT // 圆环边框颜色
    private var arcBorderWidth: Float = 0f // 圆环边框宽度

    // 指针属性
    private var pointerColor: Int = Color.RED // 指针颜色
    private var pointerDrawable: Int = 0 // 指针图片资源ID
    private var pointerLength: Float = 150f // 指针长度
    private var pointerWidth: Float = 8f // 指针宽度
    private var pointerOffset: Float = 0f // 指针偏移量（正数远离圆心，负数经过圆心）

    // 速度属性
    private var currentSpeed: Float = 0f // 当前速度
    private var maxSpeed: Float = 200f // 最大速度
    
    // 速度动画
    private var speedAnimator: ValueAnimator? = null
    
    // 速度文本属性
    private var showSpeedText: Boolean = true // 是否显示速度文本
    private var speedTextColor: Int = Color.BLACK // 速度数值颜色
    private var speedTextSize: Float = 24f // 速度数值大小
    private var speedUnit: String = "km/h" // 速度单位
    private var speedUnitColor: Int = Color.GRAY // 速度单位颜色
    private var speedUnitSize: Float = 24f // 速度单位大小
    private var speedTextBackgroundRes: Int = 0 // 速度文本背景图片资源ID
    private var speedTextBackgroundPadding: Float = 12f // 速度文本背景内边距
    private var speedTextOffsetY: Float = 0f // 速度文本垂直偏移量（正数向下，负数向上）
    
    // 速度文本背景（直接使用Drawable）
    private var speedTextBackgroundDrawable: Drawable? = null
    
    // 刻度属性
    private var showScaleText: Boolean = true // 是否显示刻度值
    private var scaleTextOutside: Boolean = true // 刻度值是否显示在外侧
    private var scaleTextColor: Int = Color.GRAY // 刻度值颜色
    private var scaleTextSize: Float = 28f // 刻度值大小
    private var scaleTextStyle: Int = 0 // 刻度值样式 (0=normal, 1=bold, 2=italic, 3=bold_italic)
    private var scaleCount: Int = 11 // 刻度数量（默认0-200，每20一个刻度，共11个）

    // 画笔
    private val arcBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedUnitPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val scaleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 圆环的矩形边界
    private val arcRectF = RectF()
    private val arcOuterBorderRectF = RectF() // 外侧边框
    private val arcInnerBorderRectF = RectF() // 内侧边框
    
    // 边框路径
    private val borderPath = Path()

    // 指针图片
    private var pointerBitmap: Bitmap? = null

    init {
        // 读取自定义属性
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomSpeedView)
            
            startAngle = typedArray.getFloat(R.styleable.CustomSpeedView_startAngle, 135f)
            sweepAngle = typedArray.getFloat(R.styleable.CustomSpeedView_sweepAngle, 270f)
            arcBackgroundColor = typedArray.getColor(
                R.styleable.CustomSpeedView_arcBackgroundColor,
                Color.LTGRAY
            )
            arcProgressColor = typedArray.getColor(
                R.styleable.CustomSpeedView_arcProgressColor,
                Color.parseColor("#00BCD4")
            )
            arcWidth = typedArray.getDimension(R.styleable.CustomSpeedView_arcWidth, 30f)
            arcBorderColor = typedArray.getColor(
                R.styleable.CustomSpeedView_arcBorderColor,
                Color.TRANSPARENT
            )
            arcBorderWidth = typedArray.getDimension(R.styleable.CustomSpeedView_arcBorderWidth, 0f)
            
            pointerColor = typedArray.getColor(R.styleable.CustomSpeedView_pointerColor, Color.RED)
            pointerDrawable = typedArray.getResourceId(R.styleable.CustomSpeedView_pointerDrawable, 0)
            pointerLength = typedArray.getDimension(R.styleable.CustomSpeedView_pointerLength, 150f)
            pointerWidth = typedArray.getDimension(R.styleable.CustomSpeedView_pointerWidth, 8f)
            pointerOffset = typedArray.getDimension(R.styleable.CustomSpeedView_pointerOffset, 0f)
            
            currentSpeed = typedArray.getFloat(R.styleable.CustomSpeedView_currentSpeed, 0f)
            maxSpeed = typedArray.getFloat(R.styleable.CustomSpeedView_maxSpeed, 200f)
            
            showSpeedText = typedArray.getBoolean(R.styleable.CustomSpeedView_showSpeedText, true)
            speedTextColor = typedArray.getColor(R.styleable.CustomSpeedView_speedTextColor, Color.BLACK)
            speedTextSize = typedArray.getDimension(R.styleable.CustomSpeedView_speedTextSize, 48f)
            speedUnit = typedArray.getString(R.styleable.CustomSpeedView_speedUnit) ?: "km/h"
            speedUnitColor = typedArray.getColor(R.styleable.CustomSpeedView_speedUnitColor, Color.GRAY)
            speedUnitSize = typedArray.getDimension(R.styleable.CustomSpeedView_speedUnitSize, 24f)
            speedTextBackgroundRes = typedArray.getResourceId(R.styleable.CustomSpeedView_speedTextBackground, 0)
            speedTextBackgroundPadding = typedArray.getDimension(R.styleable.CustomSpeedView_speedTextBackgroundPadding, 12f)
            speedTextOffsetY = typedArray.getDimension(R.styleable.CustomSpeedView_speedTextOffsetY, 0f)
            
            showScaleText = typedArray.getBoolean(R.styleable.CustomSpeedView_showScaleText, true)
            scaleTextOutside = typedArray.getBoolean(R.styleable.CustomSpeedView_scaleTextOutside, true)
            scaleTextColor = typedArray.getColor(R.styleable.CustomSpeedView_scaleTextColor, Color.GRAY)
            scaleTextSize = typedArray.getDimension(R.styleable.CustomSpeedView_scaleTextSize, 28f)
            scaleTextStyle = typedArray.getInt(R.styleable.CustomSpeedView_scaleTextStyle, 0)
            scaleCount = typedArray.getInt(R.styleable.CustomSpeedView_scaleCount, 11)
            
            typedArray.recycle()
        }

        // 初始化画笔
        initPaints()

        // 加载指针图片
        if (pointerDrawable != 0) {
            loadPointerBitmap()
        }
        
        // 加载速度文本背景图片
        if (speedTextBackgroundRes != 0) {
            loadSpeedTextBackground()
        }
    }

    private fun initPaints() {
        // 圆环背景画笔
        arcBackgroundPaint.apply {
            color = arcBackgroundColor
            style = Paint.Style.STROKE
            strokeWidth = arcWidth
            strokeCap = Paint.Cap.ROUND
        }

        // 圆环进度画笔
        arcProgressPaint.apply {
            color = arcProgressColor
            style = Paint.Style.STROKE
            strokeWidth = arcWidth
            strokeCap = Paint.Cap.ROUND
        }

        // 圆环边框画笔
        arcBorderPaint.apply {
            color = arcBorderColor
            style = Paint.Style.STROKE
            strokeWidth = arcBorderWidth
            strokeCap = Paint.Cap.BUTT // 使用BUTT以确保边角整齐
            strokeJoin = Paint.Join.ROUND // 圆角连接
        }

        // 指针画笔
        pointerPaint.apply {
            color = pointerColor
            style = Paint.Style.FILL
            strokeWidth = pointerWidth
            strokeCap = Paint.Cap.ROUND
        }
        
        // 速度数值画笔
        speedTextPaint.apply {
            color = speedTextColor
            textSize = speedTextSize
            textAlign = Paint.Align.LEFT
            isFakeBoldText = true
        }
        
        // 速度单位画笔
        speedUnitPaint.apply {
            color = speedUnitColor
            textSize = speedUnitSize
            textAlign = Paint.Align.LEFT
        }
        
        // 刻度文本画笔
        scaleTextPaint.apply {
            color = scaleTextColor
            textSize = scaleTextSize
            textAlign = Paint.Align.CENTER
            typeface = when (scaleTextStyle) {
                1 -> Typeface.DEFAULT_BOLD
                2 -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                3 -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                else -> Typeface.DEFAULT
            }
        }
    }

    private fun loadPointerBitmap() {
        try {
            val drawable = ContextCompat.getDrawable(context, pointerDrawable)
            drawable?.let {
                pointerBitmap = Bitmap.createBitmap(
                    it.intrinsicWidth,
                    it.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(pointerBitmap!!)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadSpeedTextBackground() {
        try {
            speedTextBackgroundDrawable = ContextCompat.getDrawable(context, speedTextBackgroundRes)
        } catch (e: Exception) {
            e.printStackTrace()
            speedTextBackgroundDrawable = null
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // 计算需要预留的空间
        var extraPadding = 0f
        if (showScaleText) {
            // 刻度文本需要的空间
            val scaleTextSpace = if (scaleTextOutside) {
                // 外侧：圆环宽度的一半 + 刻度文本大小 + 间距
                arcWidth / 2f + scaleTextSize + 20f
            } else {
                // 内侧：通常不会超出，但预留一些空间
                scaleTextSize / 2f
            }
            extraPadding = scaleTextSpace * 2 // 两侧都需要
        }
        
        // 计算圆环的矩形边界（圆环中心线位置）
        val totalPadding = arcWidth + arcBorderWidth * 2 + paddingLeft + paddingRight + extraPadding
        val diameter = min(w, h) - totalPadding
        val left = (w - diameter) / 2f
        val top = (h - diameter) / 2f
        val right = left + diameter
        val bottom = top + diameter
        
        arcRectF.set(left, top, right, bottom)
        
        // 计算边框的矩形边界（外侧和内侧）
        if (arcBorderWidth > 0) {
            val halfArcWidth = arcWidth / 2f
            
            // 外侧边框（在圆环外边缘）
            val outerOffset = halfArcWidth
            arcOuterBorderRectF.set(
                left - outerOffset,
                top - outerOffset,
                right + outerOffset,
                bottom + outerOffset
            )
            
            // 内侧边框（在圆环内边缘）
            val innerOffset = halfArcWidth
            arcInnerBorderRectF.set(
                left + innerOffset,
                top + innerOffset,
                right - innerOffset,
                bottom - innerOffset
            )
            
            // 构建完整的边框路径（封闭路径）
            updateBorderPath()
        }
    }
    
    private fun updateBorderPath() {
        borderPath.reset()
        
        val centerX = width / 2f
        val centerY = height / 2f
        val outerRadius = arcOuterBorderRectF.width() / 2f
        val innerRadius = arcInnerBorderRectF.width() / 2f
        val capRadius = arcWidth / 2f // 端点圆弧半径
        
        // 计算起始角度和结束角度（转换为弧度）
        val startRad = Math.toRadians(startAngle.toDouble())
        val endRad = Math.toRadians((startAngle + sweepAngle).toDouble())
        
        // 起点 - 外圈起始位置
        val outerStartX = (centerX + outerRadius * cos(startRad)).toFloat()
        val outerStartY = (centerY + outerRadius * sin(startRad)).toFloat()
        
        borderPath.moveTo(outerStartX, outerStartY)
        
        // 1. 绘制外圈弧线（从起点到终点）
        borderPath.arcTo(arcOuterBorderRectF, startAngle, sweepAngle, false)
        
        // 终点位置计算
        val endCenterX = (centerX + (outerRadius + innerRadius) / 2f * cos(endRad)).toFloat()
        val endCenterY = (centerY + (outerRadius + innerRadius) / 2f * sin(endRad)).toFloat()
        
        // 2. 终点圆弧封闭（从外圈到内圈，180度圆弧）
        val endCapRect = RectF(
            endCenterX - capRadius,
            endCenterY - capRadius,
            endCenterX + capRadius,
            endCenterY + capRadius
        )
        // 计算终点圆弧的起始角度（垂直于圆环）
        val endCapStartAngle = startAngle + sweepAngle
        borderPath.arcTo(endCapRect, endCapStartAngle, 180f, false)
        
        // 3. 绘制内圈弧线（从终点回到起点，逆向）
        borderPath.arcTo(arcInnerBorderRectF, startAngle + sweepAngle, -sweepAngle, false)
        
        // 起点位置计算
        val startCenterX = (centerX + (outerRadius + innerRadius) / 2f * cos(startRad)).toFloat()
        val startCenterY = (centerY + (outerRadius + innerRadius) / 2f * sin(startRad)).toFloat()
        
        // 4. 起点圆弧封闭（从内圈到外圈，180度圆弧）
        val startCapRect = RectF(
            startCenterX - capRadius,
            startCenterY - capRadius,
            startCenterX + capRadius,
            startCenterY + capRadius
        )
        // 计算起点圆弧的起始角度（垂直于圆环）
        val startCapStartAngle = startAngle + 180f
        borderPath.arcTo(startCapRect, startCapStartAngle, 180f, false)
        
        // 5. 封闭路径
        borderPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制圆环背景
        canvas.drawArc(arcRectF, startAngle, sweepAngle, false, arcBackgroundPaint)

        // 绘制圆环进度
        val progress = (currentSpeed / maxSpeed).coerceIn(0f, 1f)
        val progressAngle = sweepAngle * progress
        canvas.drawArc(arcRectF, startAngle, progressAngle, false, arcProgressPaint)

        // 绘制圆环边框（完整封闭路径）
        if (arcBorderWidth > 0 && arcBorderColor != Color.TRANSPARENT) {
            canvas.drawPath(borderPath, arcBorderPaint)
        }
        
        // 绘制刻度值
        if (showScaleText) {
            drawScaleText(canvas)
        }

        // 绘制指针
        drawPointer(canvas, progress)
        
        // 绘制速度文本
        if (showSpeedText) {
            drawSpeedText(canvas)
        }
    }

    private fun drawPointer(canvas: Canvas, progress: Float) {
        val centerX = width / 2f
        val centerY = height / 2f

        // 计算指针角度（根据进度）
        // 指针默认向上（-90度），所以需要加90度来校正
        val pointerAngle = startAngle + sweepAngle * progress + 90f

        canvas.save()
        canvas.rotate(pointerAngle, centerX, centerY)

        // 应用偏移量（正数远离圆心，负数经过圆心）
        val offsetY = centerY - pointerOffset

        if (pointerBitmap != null && pointerDrawable != 0) {
            // 使用图片绘制指针
            val bitmapWidth = pointerBitmap!!.width.toFloat()
            val bitmapHeight = pointerBitmap!!.height.toFloat()
            
            // 缩放图片以匹配指针长度
            val scale = pointerLength / bitmapHeight
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            // 应用偏移量：图片底部从 offsetY 开始
            matrix.postTranslate(centerX - (bitmapWidth * scale) / 2, offsetY - bitmapHeight * scale)
            
            canvas.drawBitmap(pointerBitmap!!, matrix, null)
        } else {
            // 使用颜色绘制指针
            val path = Path()
            
            // 绘制箭头形状的指针（向上指，从偏移位置开始）
            val halfWidth = pointerWidth / 2
            
            // 指针顶部（向上）
            path.moveTo(centerX, offsetY - pointerLength)
            // 指针右下（基准点）
            path.lineTo(centerX + halfWidth, offsetY)
            // 指针左下（基准点）
            path.lineTo(centerX - halfWidth, offsetY)
            path.close()
            
            canvas.drawPath(path, pointerPaint)
            
            // 绘制中心圆点（在偏移后的位置）
            canvas.drawCircle(centerX, offsetY, pointerWidth * 1.5f, pointerPaint)
        }

        canvas.restore()
    }
    
    private fun drawSpeedText(canvas: Canvas) {
        val centerX = width / 2f
        
        // 格式化速度值（整数显示）
        val speedValue = String.format("%.0f", currentSpeed)
        
        // 计算数值和单位的总宽度，用于合理布局
        val valueWidth = speedTextPaint.measureText(speedValue)
        val unitWidth = speedUnitPaint.measureText(speedUnit)
        val spacing = 8f // 数值和单位之间的间距
        val totalTextWidth = valueWidth + unitWidth + spacing
        
        // 计算文本度量（使用数值的metrics作为基准，因为通常数值字体更大）
        val speedTextMetrics = speedTextPaint.fontMetrics
        val unitTextMetrics = speedUnitPaint.fontMetrics
        
        // 计算最大高度（用于背景）
        val maxAscent = max(-speedTextMetrics.ascent, -unitTextMetrics.ascent)
        val maxDescent = max(speedTextMetrics.descent, unitTextMetrics.descent)
        val maxTextHeight = maxAscent + maxDescent
        
        // 计算文本基线位置（底部居中，留一些 padding，加上垂直偏移）
        val bottomPadding = 20f
        val baseline = height - bottomPadding - paddingBottom - maxDescent + speedTextOffsetY
        
        // 绘制背景图片（如果有）
        if (speedTextBackgroundDrawable != null && speedTextBackgroundRes != 0) {
            val bgWidth = totalTextWidth + speedTextBackgroundPadding * 2
            val bgHeight = maxTextHeight + speedTextBackgroundPadding * 2
            val bgLeft = centerX - bgWidth / 2
            val bgTop = baseline - maxAscent - speedTextBackgroundPadding
            
            // 设置drawable的绘制边界并绘制
            speedTextBackgroundDrawable!!.setBounds(
                bgLeft.toInt(),
                bgTop.toInt(),
                (bgLeft + bgWidth).toInt(),
                (bgTop + bgHeight).toInt()
            )
            speedTextBackgroundDrawable!!.draw(canvas)
        }
        
        // 计算文本起始位置（保证整体居中）
        val textStartX = centerX - totalTextWidth / 2
        
        // 绘制速度数值（底部对齐）
        canvas.drawText(
            speedValue,
            textStartX,
            baseline,
            speedTextPaint
        )
        
        // 绘制单位（底部对齐，紧跟数值）
        canvas.drawText(
            speedUnit,
            textStartX + valueWidth + spacing,
            baseline,
            speedUnitPaint
        )
    }
    
    private fun drawScaleText(canvas: Canvas) {
        if (scaleCount <= 0) return
        
        val centerX = width / 2f
        val centerY = height / 2f
        
        // 计算刻度文本的半径（根据是否在外侧）
        val arcRadius = arcRectF.width() / 2f
        
        // 使用文本的实际高度来计算偏移
        val textMetrics = scaleTextPaint.fontMetrics
        val textHeight = textMetrics.descent - textMetrics.ascent
        
        val textOffset = if (scaleTextOutside) {
            // 外侧：圆环外边缘 + 文本高度的一半 + 间距
            arcWidth / 2f + textHeight / 2f + 15f
        } else {
            // 内侧：圆环内边缘 - 文本高度的一半 - 间距
            -(arcWidth / 2f + textHeight / 2f + 15f)
        }
        val textRadius = arcRadius + textOffset
        
        // 绘制每个刻度值
        for (i in 0 until scaleCount) {
            // 计算当前刻度对应的速度值
            val scaleValue = maxSpeed * i / (scaleCount - 1)
            val scaleText = scaleValue.toInt().toString()
            
            // 计算刻度对应的角度
            val angle = startAngle + sweepAngle * i / (scaleCount - 1)
            val angleRad = Math.toRadians(angle.toDouble())
            
            // 计算文本位置
            val textX = (centerX + textRadius * cos(angleRad)).toFloat()
            val textY = (centerY + textRadius * sin(angleRad)).toFloat()
            
            // 调整文本垂直位置（让文本垂直居中）
            val textOffsetY = textHeight / 2 - textMetrics.descent
            
            canvas.drawText(scaleText, textX, textY + textOffsetY, scaleTextPaint)
        }
    }

    /**
     * 设置当前速度
     */
    fun setSpeed(speed: Float) {
        currentSpeed = speed.coerceIn(0f, maxSpeed)
        invalidate()
    }

    /**
     * 设置最大速度
     */
    fun setMaxSpeed(max: Float) {
        maxSpeed = max
        invalidate()
    }

    /**
     * 获取当前速度
     */
    fun getSpeed(): Float = currentSpeed

    /**
     * 设置圆环背景颜色
     */
    fun setArcBackgroundColor(color: Int) {
        arcBackgroundColor = color
        arcBackgroundPaint.color = color
        invalidate()
    }

    /**
     * 设置圆环进度颜色
     */
    fun setArcProgressColor(color: Int) {
        arcProgressColor = color
        arcProgressPaint.color = color
        invalidate()
    }

    /**
     * 设置指针颜色
     */
    fun setPointerColor(color: Int) {
        pointerColor = color
        pointerPaint.color = color
        invalidate()
    }

    /**
     * 设置圆环宽度
     */
    fun setArcWidth(width: Float) {
        arcWidth = width
        arcBackgroundPaint.strokeWidth = width
        arcProgressPaint.strokeWidth = width
        requestLayout() // 圆环宽度改变会影响刻度位置，需要重新计算
        
        // 如果存在边框，需要手动更新边框路径（因为边框依赖于圆环宽度）
        if (arcBorderWidth > 0 && this.width > 0 && this.height > 0) {
            // 重新计算边框矩形
            val centerX = this.width / 2f
            val centerY = this.height / 2f
            val halfArcWidth = arcWidth / 2f
            
            // 外侧边框
            val outerOffset = halfArcWidth
            arcOuterBorderRectF.set(
                arcRectF.left - outerOffset,
                arcRectF.top - outerOffset,
                arcRectF.right + outerOffset,
                arcRectF.bottom + outerOffset
            )
            
            // 内侧边框
            val innerOffset = halfArcWidth
            arcInnerBorderRectF.set(
                arcRectF.left + innerOffset,
                arcRectF.top + innerOffset,
                arcRectF.right - innerOffset,
                arcRectF.bottom - innerOffset
            )
            
            // 更新边框路径
            updateBorderPath()
        }
    }

    /**
     * 设置圆环边框颜色
     */
    fun setArcBorderColor(color: Int) {
        arcBorderColor = color
        arcBorderPaint.color = color
        invalidate()
    }

    /**
     * 设置圆环边框宽度
     */
    fun setArcBorderWidth(width: Float) {
        arcBorderWidth = width
        arcBorderPaint.strokeWidth = width
        requestLayout() // 边框宽度改变会影响布局，需要重新计算
    }

    /**
     * 设置指针长度
     */
    fun setPointerLength(length: Float) {
        pointerLength = length
        invalidate()
    }

    /**
     * 设置指针宽度
     */
    fun setPointerWidth(width: Float) {
        pointerWidth = width
        pointerPaint.strokeWidth = width
        invalidate()
    }

    /**
     * 设置起始角度
     */
    fun setStartAngle(angle: Float) {
        startAngle = angle
        if (arcBorderWidth > 0) {
            updateBorderPath()
        }
        invalidate()
    }

    /**
     * 设置扫过角度
     */
    fun setSweepAngle(angle: Float) {
        sweepAngle = angle
        if (arcBorderWidth > 0) {
            updateBorderPath()
        }
        invalidate()
    }

    /**
     * 设置指针偏移量
     * @param offset 偏移量，正数远离圆心，负数经过圆心
     */
    fun setPointerOffset(offset: Float) {
        pointerOffset = offset
        invalidate()
    }

    /**
     * 设置指针图片
     * @param resId 图片资源ID，传0则使用颜色绘制指针
     */
    fun setPointerDrawable(resId: Int) {
        pointerDrawable = resId
        if (resId != 0) {
            loadPointerBitmap()
        } else {
            pointerBitmap = null
        }
        invalidate()
    }

    /**
     * 设置是否显示速度文本
     */
    fun setShowSpeedText(show: Boolean) {
        showSpeedText = show
        invalidate()
    }

    /**
     * 设置速度数值颜色
     */
    fun setSpeedTextColor(color: Int) {
        speedTextColor = color
        speedTextPaint.color = color
        invalidate()
    }

    /**
     * 设置速度数值大小
     */
    fun setSpeedTextSize(size: Float) {
        speedTextSize = size
        speedTextPaint.textSize = size
        invalidate()
    }

    /**
     * 设置速度单位文本
     */
    fun setSpeedUnit(unit: String) {
        speedUnit = unit
        invalidate()
    }

    /**
     * 设置速度单位颜色
     */
    fun setSpeedUnitColor(color: Int) {
        speedUnitColor = color
        speedUnitPaint.color = color
        invalidate()
    }

    /**
     * 设置速度单位大小
     */
    fun setSpeedUnitSize(size: Float) {
        speedUnitSize = size
        speedUnitPaint.textSize = size
        invalidate()
    }

    /**
     * 设置速度文本背景图片
     * @param resId 图片资源ID
     */
    fun setSpeedTextBackground(resId: Int) {
        speedTextBackgroundRes = resId
        if (resId != 0) {
            loadSpeedTextBackground()
        } else {
            speedTextBackgroundDrawable = null
        }
        invalidate()
    }

    /**
     * 设置速度文本背景内边距
     * @param padding 内边距值
     */
    fun setSpeedTextBackgroundPadding(padding: Float) {
        speedTextBackgroundPadding = padding
        invalidate()
    }

    /**
     * 设置速度文本垂直偏移量
     * @param offsetY 偏移量，正数向下移动，负数向上移动
     */
    fun setSpeedTextOffsetY(offsetY: Float) {
        speedTextOffsetY = offsetY
        invalidate()
    }

    /**
     * 设置是否显示刻度值
     */
    fun setShowScaleText(show: Boolean) {
        showScaleText = show
        requestLayout() // 需要重新计算布局
    }

    /**
     * 设置刻度值是否显示在外侧
     * @param outside true=外侧，false=内侧
     */
    fun setScaleTextOutside(outside: Boolean) {
        scaleTextOutside = outside
        requestLayout() // 需要重新计算布局
    }

    /**
     * 设置刻度值颜色
     */
    fun setScaleTextColor(color: Int) {
        scaleTextColor = color
        scaleTextPaint.color = color
        invalidate()
    }

    /**
     * 设置刻度值大小
     */
    fun setScaleTextSize(size: Float) {
        scaleTextSize = size
        scaleTextPaint.textSize = size
        requestLayout() // 需要重新计算布局
    }

    /**
     * 设置刻度值样式
     * @param style 0=normal, 1=bold, 2=italic, 3=bold_italic
     */
    fun setScaleTextStyle(style: Int) {
        scaleTextStyle = style
        scaleTextPaint.typeface = when (style) {
            1 -> Typeface.DEFAULT_BOLD
            2 -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            3 -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
            else -> Typeface.DEFAULT
        }
        invalidate()
    }

    /**
     * 设置刻度数量
     */
    fun setScaleCount(count: Int) {
        scaleCount = count
        invalidate()
    }

    /**
     * 带动画效果地设置速度（默认动画时长500ms）
     * @param targetSpeed 目标速度值
     */
    fun animateToSpeed(targetSpeed: Float) {
        animateToSpeed(targetSpeed, 500)
    }

    /**
     * 带动画效果地设置速度
     * @param targetSpeed 目标速度值
     * @param duration 动画持续时间（毫秒）
     */
    fun animateToSpeed(targetSpeed: Float, duration: Long) {
        // 取消之前的动画
        speedAnimator?.cancel()
        
        // 限制目标速度在有效范围内
        val target = targetSpeed.coerceIn(0f, maxSpeed)
        
        // 创建新的动画
        speedAnimator = ValueAnimator.ofFloat(currentSpeed, target).apply {
            this.duration = duration
            
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                currentSpeed = animatedValue
                invalidate() // 刷新视图
            }
            
            start()
        }
    }

    /**
     * 取消当前的速度动画
     */
    fun cancelSpeedAnimation() {
        speedAnimator?.cancel()
    }
}

