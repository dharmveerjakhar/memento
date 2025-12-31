package com.example.lifecalendar.generator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.lifecalendar.domain.CalendarMetrics
import com.example.lifecalendar.domain.LifeCalendarCalculator

/**
 * Generates life calendar images as bitmaps for use as wallpaper.
 *
 * This class uses Android's Canvas API to draw a grid of circles representing
 * weeks of life. Filled circles represent weeks lived, empty circles represent
 * future weeks.
 *
 * ## Grid Layout
 * - Columns: 52 (weeks per year)
 * - Rows: Life expectancy in years (default 80)
 * - Each cell is a circle (filled = lived, outline = remaining)
 *
 * ## Usage Example
 * ```kotlin
 * val generator = CalendarImageGenerator()
 * val config = CalendarConfig(width = 1080, height = 2400)
 * val bitmap = generator.generate(metrics, config)
 * ```
 *
 * @see CalendarConfig for customization options
 * @see CalendarMetrics for the input data
 */
class CalendarImageGenerator {

    /**
     * Generates a calendar bitmap based on the provided metrics and configuration.
     *
     * The generated image shows a grid where:
     * - Filled circles = weeks already lived
     * - Empty circles = weeks remaining
     * - The grid is centered on the canvas with proper padding
     *
     * @param metrics Life calendar metrics containing weeks lived/remaining
     * @param config Visual configuration for the calendar (colors, dimensions, etc.)
     * @return A [Bitmap] ready to be set as wallpaper
     */
    fun generate(metrics: CalendarMetrics, config: CalendarConfig): Bitmap {
        val bitmap = Bitmap.createBitmap(config.width, config.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw background
        canvas.drawColor(config.backgroundColor)

        // Calculate grid dimensions
        val gridParams = calculateGridParams(metrics.lifeExpectancy, config)

        // Draw the grid
        drawGrid(canvas, metrics, gridParams, config)

        return bitmap
    }

    /**
     * Calculates grid positioning and sizing parameters.
     *
     * Determines the optimal cell size and spacing to fit the entire
     * life calendar grid within the available canvas space while
     * maintaining proper aspect ratio and padding.
     *
     * @param lifeExpectancy Number of years (rows) in the grid
     * @param config Visual configuration containing canvas dimensions
     * @return [GridParams] with calculated dimensions and positions
     */
    private fun calculateGridParams(lifeExpectancy: Int, config: CalendarConfig): GridParams {
        val columns = LifeCalendarCalculator.WEEKS_PER_YEAR
        val rows = lifeExpectancy

        // Calculate available space (with padding)
        val availableWidth = config.width - (config.horizontalPadding * 2)
        val availableHeight = config.height - (config.verticalPadding * 2)

        // Calculate cell size to fit all weeks
        // Account for spacing between cells
        val maxCellWidth = (availableWidth - (columns - 1) * config.cellSpacing) / columns
        val maxCellHeight = (availableHeight - (rows - 1) * config.cellSpacing) / rows

        // Use the smaller dimension to maintain square cells
        val cellSize = minOf(maxCellWidth, maxCellHeight).coerceAtLeast(config.minCellSize)

        // Calculate actual grid dimensions
        val gridWidth = columns * cellSize + (columns - 1) * config.cellSpacing
        val gridHeight = rows * cellSize + (rows - 1) * config.cellSpacing

        // Center the grid
        val startX = (config.width - gridWidth) / 2f
        val startY = (config.height - gridHeight) / 2f

        return GridParams(
            startX = startX,
            startY = startY,
            cellSize = cellSize,
            columns = columns,
            rows = rows
        )
    }

    /**
     * Draws the complete life calendar grid on the canvas.
     *
     * Iterates through all weeks of life and draws each as a circle.
     * Weeks already lived are filled, remaining weeks are outlined.
     *
     * @param canvas The Android Canvas to draw on
     * @param metrics Life calendar metrics for determining filled vs empty
     * @param params Grid positioning and sizing parameters
     * @param config Visual configuration (colors, stroke width, etc.)
     */
    private fun drawGrid(
        canvas: Canvas,
        metrics: CalendarMetrics,
        params: GridParams,
        config: CalendarConfig
    ) {
        val filledPaint = createFilledPaint(config)
        val emptyPaint = createEmptyPaint(config)

        var weekIndex = 0

        for (row in 0 until params.rows) {
            for (col in 0 until params.columns) {
                val centerX = params.startX + col * (params.cellSize + config.cellSpacing) + params.cellSize / 2f
                val centerY = params.startY + row * (params.cellSize + config.cellSpacing) + params.cellSize / 2f
                val radius = (params.cellSize / 2f) - config.cellPadding

                val paint = if (weekIndex < metrics.weeksLived) filledPaint else emptyPaint
                canvas.drawCircle(centerX, centerY, radius, paint)

                weekIndex++
            }
        }
    }

    /**
     * Creates the paint object for filled (lived) week circles.
     *
     * @param config Configuration containing the filled color
     * @return Configured [Paint] object for filled circles
     */
    private fun createFilledPaint(config: CalendarConfig): Paint {
        return Paint().apply {
            color = config.filledColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    /**
     * Creates the paint object for empty (remaining) week circles.
     *
     * @param config Configuration containing the empty color and stroke width
     * @return Configured [Paint] object for empty circles
     */
    private fun createEmptyPaint(config: CalendarConfig): Paint {
        return Paint().apply {
            color = config.emptyColor
            style = Paint.Style.STROKE
            strokeWidth = config.emptyCircleStrokeWidth
            isAntiAlias = true
        }
    }
}

/**
 * Configuration for calendar image generation.
 *
 * This data class allows customization of all visual aspects of the
 * life calendar image, including colors, dimensions, and spacing.
 *
 * @property width Canvas width in pixels (typically screen width)
 * @property height Canvas height in pixels (typically screen height)
 * @property backgroundColor Background color of the wallpaper
 * @property filledColor Color for weeks already lived
 * @property emptyColor Color for remaining weeks (outline)
 * @property horizontalPadding Left/right padding from canvas edge
 * @property verticalPadding Top/bottom padding from canvas edge
 * @property cellSpacing Space between grid cells
 * @property cellPadding Padding inside each cell (affects circle radius)
 * @property minCellSize Minimum cell size to ensure visibility
 * @property emptyCircleStrokeWidth Stroke width for empty circle outlines
 */
data class CalendarConfig(
    val width: Int,
    val height: Int,
    val backgroundColor: Int = 0xFF000000.toInt(),  // Black
    val filledColor: Int = 0xFFFFFFFF.toInt(),      // White
    val emptyColor: Int = 0xFF4A4A4A.toInt(),       // Dark gray
    val horizontalPadding: Float = 40f,
    val verticalPadding: Float = 200f,              // Extra padding for status bar area
    val cellSpacing: Float = 2f,
    val cellPadding: Float = 0.5f,
    val minCellSize: Float = 4f,
    val emptyCircleStrokeWidth: Float = 1f
)

/**
 * Internal parameters for grid layout calculations.
 *
 * @property startX X coordinate of the grid's top-left corner
 * @property startY Y coordinate of the grid's top-left corner
 * @property cellSize Size of each cell in pixels
 * @property columns Number of columns (weeks per year = 52)
 * @property rows Number of rows (life expectancy in years)
 */
private data class GridParams(
    val startX: Float,
    val startY: Float,
    val cellSize: Float,
    val columns: Int,
    val rows: Int
)
