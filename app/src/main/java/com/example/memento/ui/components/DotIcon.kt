package com.example.memento.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Types of custom icons supported by the DotIcon component.
 */
enum class DotIconType {
    EDIT,
    SETTINGS
}

/**
 * A custom icon component that renders shapes using a dot matrix style
 * to match the Memento aesthetic.
 */
@Composable
fun DotIcon(
    type: DotIconType,
    color: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 3.dp,
    spacing: Dp = 1.dp
) {
    val pattern = remember(type) { getIconPattern(type) }
    val rows = pattern.size
    val cols = if (pattern.isNotEmpty()) pattern[0].length else 0
    
    val width = (dotSize * cols) + (spacing * (cols - 1))
    val height = (dotSize * rows) + (spacing * (rows - 1))

    Canvas(modifier = modifier.size(width, height)) {
        val dotPx = dotSize.toPx()
        val spacePx = spacing.toPx()
        val radius = dotPx / 2f

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (pattern[r][c] == 'X') {
                    val x = c * (dotPx + spacePx) + radius
                    val y = r * (dotPx + spacePx) + radius
                    drawCircle(
                        color = color,
                        radius = radius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

private fun getIconPattern(type: DotIconType): List<String> {
    return when (type) {
        DotIconType.EDIT -> listOf(
            "....XX.",
            "...X..X",
            "..X..X.",
            ".X..X..",
            "X..X...",
            "XXX....",
            "XX....."
        )
        DotIconType.SETTINGS -> listOf(
            "        XXXXX        ",
            "        XXXXX        ",
            "        XXXXX        ",
            "    XXXXXXXXXXXXX    ",
            "   XXXXXXXXXXXXXXX   ",
            "  XXXXXX     XXXXXX  ",
            "  XXXXX       XXXXX  ",
            "  XXXX         XXXX  ",
            "XXXXX           XXXXX",
            "XXXXX           XXXXX",
            "XXXXX           XXXXX",
            "XXXXX           XXXXX",
            "XXXXX           XXXXX",
            "  XXXX         XXXX  ",
            "  XXXXX       XXXXX  ",
            "  XXXXXX     XXXXXX  ",
            "   XXXXXXXXXXXXXXX   ",
            "    XXXXXXXXXXXXX    ",
            "        XXXXX        ",
            "        XXXXX        ",
            "        XXXXX        "
        )
    }
}
