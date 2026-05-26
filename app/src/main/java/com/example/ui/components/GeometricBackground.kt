package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NavyPrimary

@Composable
fun GeometricBackground(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Draw outer thick concentric circle (Alpha 0.05)
        val circleRadius = 140.dp.toPx()
        val circleStrokeWidth = 36.dp.toPx()
        drawCircle(
            color = NavyPrimary.copy(alpha = 0.05f),
            radius = circleRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = circleStrokeWidth)
        )

        // Draw rotated inner square (Alpha 0.05)
        val squareHalfWidth = 90.dp.toPx()
        val squareStrokeWidth = 18.dp.toPx()
        val path = Path().apply {
            moveTo(centerX, centerY - squareHalfWidth)
            lineTo(centerX + squareHalfWidth, centerY)
            lineTo(centerX, centerY + squareHalfWidth)
            lineTo(centerX - squareHalfWidth, centerY)
            close()
        }
        drawPath(
            path = path,
            color = NavyPrimary.copy(alpha = 0.04f),
            style = Stroke(width = squareStrokeWidth)
        )
    }
}
