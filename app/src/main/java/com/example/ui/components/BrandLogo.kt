package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealSuccess

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    heightDp: Int = 120
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(heightDp.dp)
                .padding(8.dp)
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw Left Side Building Skyline (Orange)
            val skylinePathLeft = Path().apply {
                moveTo(w * 0.05f, h * 0.85f)
                lineTo(w * 0.05f, h * 0.65f) // Building 1
                lineTo(w * 0.12f, h * 0.65f)
                lineTo(w * 0.12f, h * 0.58f) // Building 2
                lineTo(w * 0.22f, h * 0.58f)
                lineTo(w * 0.22f, h * 0.70f)
                lineTo(w * 0.28f, h * 0.70f)
                lineTo(w * 0.28f, h * 0.85f)
                close()
            }
            drawPath(skylinePathLeft, color = OrangeAccent, style = Fill)

            // Draw Crane (Orange lines) on the left side
            // Crane Tower
            drawLine(
                color = OrangeAccent,
                start = Offset(w * 0.18f, h * 0.58f),
                end = Offset(w * 0.18f, h * 0.38f),
                strokeWidth = 3f
            )
            // Crane arm
            drawLine(
                color = OrangeAccent,
                start = Offset(w * 0.10f, h * 0.42f),
                end = Offset(w * 0.30f, h * 0.38f),
                strokeWidth = 3f
            )
            // Crane trusses
            drawLine(
                color = OrangeAccent,
                start = Offset(w * 0.18f, h * 0.38f),
                end = Offset(w * 0.10f, h * 0.42f),
                strokeWidth = 2f
            )
            drawLine(
                color = OrangeAccent,
                start = Offset(w * 0.18f, h * 0.38f),
                end = Offset(w * 0.24f, h * 0.40f),
                strokeWidth = 2f
            )

            // 2. Draw Right Side Skyline (Orange & Grey)
            val skylinePathRight = Path().apply {
                moveTo(w * 0.72f, h * 0.85f)
                lineTo(w * 0.72f, h * 0.52f) // Tower block
                lineTo(w * 0.78f, h * 0.52f)
                lineTo(w * 0.78f, h * 0.45f) // Spire base
                lineTo(w * 0.84f, h * 0.45f)
                lineTo(w * 0.84f, h * 0.85f)
                close()
            }
            drawPath(skylinePathRight, color = OrangeAccent, style = Fill)

            // Dynamic Spire
            drawLine(
                color = OrangeAccent,
                start = Offset(w * 0.81f, h * 0.45f),
                end = Offset(w * 0.81f, h * 0.25f),
                strokeWidth = 4f
            )

            // 3. Draw Stylized "A" Bridge (Navy Primary)
            val leftLeg = Path().apply {
                moveTo(w * 0.48f, h * 0.22f)
                lineTo(w * 0.52f, h * 0.22f)
                lineTo(w * 0.75f, h * 0.83f)
                lineTo(w * 0.62f, h * 0.83f)
                cubicTo(w * 0.56f, h * 0.70f, w * 0.52f, h * 0.55f, w * 0.48f, h * 0.22f)
                close()
            }
            val rightLeg = Path().apply {
                moveTo(w * 0.52f, h * 0.22f)
                lineTo(w * 0.48f, h * 0.22f)
                lineTo(w * 0.25f, h * 0.83f)
                lineTo(w * 0.38f, h * 0.83f)
                cubicTo(w * 0.44f, h * 0.70f, w * 0.48f, h * 0.55f, w * 0.52f, h * 0.22f)
                close()
            }
            drawPath(leftLeg, color = NavyPrimary, style = Fill)
            drawPath(rightLeg, color = NavyPrimary, style = Fill)

            // High-tension Bridge cable stays bridging the 'A' (Teal / Accent grey)
            drawLine(
                color = Color.LightGray,
                start = Offset(w * 0.25f, h * 0.83f),
                end = Offset(w * 0.60f, h * 0.50f),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.LightGray,
                start = Offset(w * 0.75f, h * 0.83f),
                end = Offset(w * 0.40f, h * 0.50f),
                strokeWidth = 3f
            )

            // Modern Top Arch (Orange cap highlight)
            val orangeCap = Path().apply {
                moveTo(w * 0.48f, h * 0.20f)
                lineTo(w * 0.50f, h * 0.12f)
                lineTo(w * 0.52f, h * 0.20f)
                close()
            }
            drawPath(orangeCap, color = OrangeAccent, style = Fill)

            // Base platform line (Navy)
            drawLine(
                color = NavyPrimary,
                start = Offset(w * 0.15f, h * 0.85f),
                end = Offset(w * 0.85f, h * 0.85f),
                strokeWidth = 6f
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Abhiyantri ",
                    color = NavyPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Setu",
                    color = OrangeAccent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                text = "DIGITAL INFRA PVT. LTD.",
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.W500,
                letterSpacing = 1.6.sp
            )
        }
    }
}
