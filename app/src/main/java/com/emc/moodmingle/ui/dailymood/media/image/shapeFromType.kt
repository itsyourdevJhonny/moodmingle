package com.emc.moodmingle.ui.dailymood.media.image

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.emc.moodmingle.data.firebase.model.post.dailymood.media.ShapeType
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun animatedShape(targetShape: ShapeType): Shape {
    val transition = updateTransition(targetShape, label = "shape")

    val progress by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 500, easing = FastOutSlowInEasing)
        },
        label = "progress"
    ) { 1f }

    return shapeFromType(targetShape, progress)
}

fun shapeFromType(shapeType: ShapeType, progress: Float = 1f): Shape {
    return when (shapeType) {
        ShapeType.NORMAL -> RectangleShape
        ShapeType.CIRCLE -> circleShape()
        ShapeType.ROUNDED -> roundedShape(16 * progress)
        ShapeType.CUT -> CutCornerShape(percent = 32)
        ShapeType.OVAL_HORIZONTAL -> horizontalOvalShape()
        ShapeType.OVAL_VERTICAL -> verticalOvalShape()
        ShapeType.TRIANGLE -> polygonShape(3, progress)
        ShapeType.DIAMOND -> diamondShape(progress)
        ShapeType.HEXAGON -> polygonShape(6, progress)
        ShapeType.PENTAGON -> polygonShape(5, progress)
        ShapeType.OCTAGON -> polygonShape(8, progress)
        ShapeType.STAR -> starShape(progress)
        ShapeType.HEART -> heartShape(progress)
        ShapeType.BLOB -> blobShape(progress)
        ShapeType.PARALLELOGRAM -> parallelogramShape(progress)
        ShapeType.TRAPEZOID -> trapezoidShape(progress)
        ShapeType.CHEVRON -> chevronShape(progress)
        ShapeType.MESSAGE_BUBBLE -> messageBubbleShape(progress)
    }
}

fun circleShape(): Shape = GenericShape { size, _ ->
    val r = minOf(size.width, size.height) / 2f
    val cx = size.width / 2f
    val cy = size.height / 2f

    moveTo(cx + r, cy)
    for (i in 1..360) {
        val angle = Math.toRadians(i.toDouble())
        lineTo(
            cx + cos(angle).toFloat() * r,
            cy + sin(angle).toFloat() * r
        )
    }
    close()
}

fun roundedShape(radius: Float): Shape = GenericShape { size, _ ->
    val r = minOf(radius, minOf(size.width, size.height) / 2f)

    val w = size.width
    val h = size.height

    moveTo(r, 0f)
    lineTo(w - r, 0f)
    quadraticTo(w, 0f, w, r)
    lineTo(w, h - r)
    quadraticTo(w, h, w - r, h)
    lineTo(r, h)
    quadraticTo(0f, h, 0f, h - r)
    lineTo(0f, r)
    quadraticTo(0f, 0f, r, 0f)
    close()
}

fun verticalOvalShape(): Shape = GenericShape { size, _ ->
    addOval(
        Rect(
            left = size.width * 0.15f,
            top = 0f,
            right = size.width * 0.85f,
            bottom = size.height
        )
    )
}

fun horizontalOvalShape(): Shape = GenericShape { size, _ ->
    addOval(
        Rect(
            left = 0f,
            top = size.height * 0.15f,
            right = size.width,
            bottom = size.height * 0.85f
        )
    )
}

fun polygonShape(sides: Int, progress: Float): Shape {
    return GenericShape { size, _ ->
        val radius = size.minDimension / 2f
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val angle = (2 * Math.PI / sides)

        for (i in 0 until sides) {
            val theta = angle * i - Math.PI / 2

            val x = centerX + radius * progress * cos(theta).toFloat()
            val y = centerY + radius * progress * sin(theta).toFloat()

            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }

        close()
    }
}

fun diamondShape(progress: Float) = GenericShape { size, _ ->
    val w = size.width
    val h = size.height

    moveTo(w / 2f, 0f)
    lineTo(w, h / 2f * progress)
    lineTo(w / 2f, h)
    lineTo(0f, h / 2f * progress)
    close()
}

fun starShape(progress: Float) = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val outer = w / 2f
    val inner = outer * 0.45f * progress

    for (i in 0..9) {
        val angle = Math.PI / 5 * i - Math.PI / 2
        val radius = if (i % 2 == 0) outer else inner

        val x = cx + radius * cos(angle).toFloat()
        val y = cy + radius * sin(angle).toFloat()

        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }

    close()
}

fun heartShape(progress: Float) = GenericShape { size, _ ->
    val w = size.width
    val h = size.height

    moveTo(w / 2f, h)

    cubicTo(
        w * (1.2f * progress),
        h * 0.6f,
        w * 0.8f,
        h * 0.1f,
        w / 2f,
        h * 0.35f
    )

    cubicTo(
        w * 0.2f,
        h * 0.1f,
        -w * 0.2f * progress,
        h * 0.6f,
        w / 2f,
        h
    )

    close()
}

fun blobShape(progress: Float) = GenericShape { size, _ ->
    val w = size.width
    val h = size.height

    moveTo(w * 0.3f, 0f)

    cubicTo(
        w,
        0f,
        w,
        h * 0.6f * progress,
        w * 0.6f,
        h
    )

    cubicTo(
        0f,
        h,
        0f,
        h * 0.4f,
        w * 0.3f,
        0f
    )

    close()
}

fun parallelogramShape(progress: Float): Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val offset = w * 0.2f * progress

    moveTo(offset, 0f)
    lineTo(w, 0f)
    lineTo(w - offset, h)
    lineTo(0f, h)
    close()
}

fun trapezoidShape(progress: Float): Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val inset = w * 0.25f * progress

    moveTo(inset, 0f)
    lineTo(w - inset, 0f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
}

fun chevronShape(progress: Float): Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val depth = w * 0.3f * progress

    moveTo(0f, h / 2f)
    lineTo(depth, 0f)
    lineTo(w, h / 2f)
    lineTo(depth, h)
    close()
}

fun messageBubbleShape(progress: Float) = GenericShape { size, _ ->
    val w = size.width
    val h = size.height

    addRoundRect(
        RoundRect(0f, 0f, w, h * 0.85f, CornerRadius(30f * progress))
    )

    moveTo(w * 0.25f, h * 0.85f)
    lineTo(w * 0.4f, h)
    lineTo(w * 0.5f, h * 0.85f)
}
