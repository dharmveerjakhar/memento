package com.example.memento.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A Text composable that renders characters using a dot matrix style
 * to match the Memento wallpaper aesthetic.
 * 
 * Supports alphanumeric characters and newlines.
 */
@Composable
fun DotText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 3.dp,
    spacing: Dp = 1.dp,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    // Split text by newlines to handle multi-line text
    val lines = text.split("\n")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dotSize * 2), // Line spacing
        horizontalAlignment = alignment
    ) {
        lines.forEach { line ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(dotSize),
                verticalAlignment = Alignment.Bottom
            ) {
                line.forEach { char ->
                    DotChar(
                        char = char,
                        color = color,
                        dotSize = dotSize,
                        spacing = spacing
                    )
                }
            }
        }
    }
}

@Composable
private fun DotChar(
    char: Char,
    color: Color,
    dotSize: Dp,
    spacing: Dp
) {
    if (char == ' ') {
        // Render whitespace
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(width = dotSize * 2, height = dotSize)
        )
        return
    }

    val pattern = remember(char) { getPattern(char) }
    val rows = pattern.size
    val cols = if (pattern.isNotEmpty()) pattern[0].length else 0
    
    // Calculate size required for this char
    val width = (dotSize * cols) + (spacing * (cols - 1))
    val height = (dotSize * rows) + (spacing * (rows - 1))

    Canvas(modifier = Modifier.size(width, height)) {
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

private fun getPattern(char: Char): List<String> {
    // Variable width patterns for better kerning
    // 5-row height for letters, 6-row for digits usually
    return when (char.uppercaseChar()) {
        // DIGITS (4x6 standardized)
        '0' -> listOf(
            ".XX.",
            "X..X",
            "X..X",
            "X..X",
            "X..X",
            ".XX."
        )
        '1' -> listOf(
            ".X.",
            "XX.",
            ".X.",
            ".X.",
            ".X.",
            "XXX"
        )
        '2' -> listOf(
            ".XX.",
            "X..X",
            "...X",
            "..X.",
            ".X..",
            "XXXX"
        )
        '3' -> listOf(
            ".XX.",
            "X..X",
            "..X.",
            "...X",
            "X..X",
            ".XX."
        )
        '4' -> listOf(
            "X..X",
            "X..X",
            "XXXX",
            "...X",
            "...X",
            "...X"
        )
        '5' -> listOf(
            "XXXX",
            "X...",
            "XXX.",
            "...X",
            "X..X",
            ".XX."
        )
        '6' -> listOf(
            ".XX.",
            "X...",
            "XXX.",
            "X..X",
            "X..X",
            ".XX."
        )
        '7' -> listOf(
            "XXXX",
            "...X",
            "..X.",
            ".X..",
            ".X..",
            ".X.."
        )
        '8' -> listOf(
            ".XX.",
            "X..X",
            ".XX.",
            "X..X",
            "X..X",
            ".XX."
        )
        '9' -> listOf(
            ".XX.",
            "X..X",
            "X..X",
            ".XXX",
            "...X",
            ".XX."
        )

        // LETTERS (Variable width, generally 5 pixel height for compactness)
        'A' -> listOf(
            ".XX.",
            "X..X",
            "XXXX",
            "X..X",
            "X..X"
        )
        'B' -> listOf(
            "XXX.",
            "X..X",
            "XXX.",
            "X..X",
            "XXX."
        )
        'C' -> listOf(
            ".CCC",
            "C...",
            "C...",
            "C...",
            ".CCC"
        ).map { it.replace('C', 'X') }
        'D' -> listOf(
            "XX..",
            "X.X.",
            "X.X.",
            "X.X.",
            "XX.."
        )
        'E' -> listOf(
            "XXXX",
            "X...",
            "XXX.",
            "X...",
            "XXXX"
        )
        'F' -> listOf(
            "XXXX",
            "X...",
            "XXX.",
            "X...",
            "X..."
        )
        'G' -> listOf(
            ".XXX",
            "X...",
            "X.XX",
            "X..X",
            ".XX."
        )
        'H' -> listOf(
            "X..X",
            "X..X",
            "XXXX",
            "X..X",
            "X..X"
        )
        'I' -> listOf(
            "XXX",
            ".X.",
            ".X.",
            ".X.",
            "XXX"
        )
        'J' -> listOf(
            "..XX",
            "...X",
            "...X",
            "X..X",
            ".XX."
        )
        'K' -> listOf(
            "X..X",
            "X.X.",
            "XX..",
            "X.X.",
            "X..X"
        )
        'L' -> listOf(
            "X...",
            "X...",
            "X...",
            "X...",
            "XXXX"
        )
        'M' -> listOf(
            "X...X",
            "XX.XX",
            "X.X.X",
            "X...X",
            "X...X"
        )
        'N' -> listOf(
            "X..X",
            "XX.X",
            "X.XX",
            "X..X",
            "X..X"
        )
        'O' -> listOf(
            ".XX.",
            "X..X",
            "X..X",
            "X..X",
            ".XX."
        )
        'P' -> listOf(
            "XXX.",
            "X..X",
            "XXX.",
            "X...",
            "X..."
        )
        'Q' -> listOf(
            ".XX.",
            "X..X",
            "X..X",
            ".XX.",
            "...X"
        )
        'R' -> listOf(
            "XXX.",
            "X..X",
            "XXX.",
            "X.X.",
            "X..X"
        )
        'S' -> listOf(
            ".XXX",
            "X...",
            ".XX.",
            "...X",
            "XXX."
        )
        'T' -> listOf(
            "XXX",
            ".X.",
            ".X.",
            ".X.",
            ".X."
        )
        'U' -> listOf(
            "X..X",
            "X..X",
            "X..X",
            "X..X",
            ".XX."
        )
        'V' -> listOf(
            "X...X",
            "X...X",
            ".X.X.",
            ".X.X.",
            "..X.."
        )
        'W' -> listOf(
            "X...X",
            "X...X",
            "X.X.X",
            "XX.XX",
            "X...X"
        )
        'X' -> listOf(
            "X...X",
            ".X.X.",
            "..X..",
            ".X.X.",
            "X...X"
        )
        'Y' -> listOf(
            "X..X",
            ".XX.",
            "..X.",
            "..X.",
            "..X."
        )
        'Z' -> listOf(
            "XXXX",
            "...X",
            "..X.",
            ".X..",
            "XXXX"
        )
        
        // SYMBOLS
        '.' -> listOf(
            "....",
            "....",
            "....",
            "....",
            "....",
            ".X.."
        )
        '%' -> listOf(
            "X..X",
            "..X.",
            ".X..",
            "X..X",
            "....",
            "...."
        )
        '!' -> listOf(
            ".X.",
            ".X.",
            ".X.",
            "...",
            ".X.",
            "..."
        )
        '*' -> listOf(
            "..X.X..",
            ".XX.XX.",
            "XX...XX",
            ".X...X.",
            "XX...XX",
            ".XX.XX.",
            "..X.X.."
        )
        '<' -> listOf(
            "...X",
            "..X.",
            ".X..",
            "..X.",
            "...X"
        )
        '>' -> listOf(
            "X...",
            ".X..",
            "..X.",
            ".X..",
            "X..."
        )
        '[' -> listOf(
            "XX",
            "X.",
            "X.",
            "X.",
            "XX"
        )
        ']' -> listOf(
            "XX",
            ".X",
            ".X",
            ".X",
            "XX"
        )
        '+' -> listOf(
            "...",
            ".X.",
            "XXX",
            ".X.",
            "..."
        )
        '-' -> listOf(
            "...",
            "...",
            "XXX",
            "...",
            "..."
        )
        
        else -> listOf(
            "....",
            ".XX.",
            "....",
            ".XX.",
            "....",
            "...."
        ) // default ? style
    }
}
