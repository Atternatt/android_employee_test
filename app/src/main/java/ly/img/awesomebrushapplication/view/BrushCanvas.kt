package ly.img.awesomebrushapplication.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ly.img.awesomebrushapplication.data.model.Draw
import ly.img.awesomebrushapplication.data.model.DrawingOptions
import ly.img.awesomebrushapplication.data.model.HistoryPoint
import ly.img.awesomebrushapplication.data.model.Point
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class BrushCanvas @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    init {
        setWillNotDraw(false)
    }

    /**
     * the current drawing options for the canvas
     */
    private var drawingOptions: DrawingOptions by Delegates.observable(
        DrawingOptions(
            color = Color.BLACK,
            strokeSize = 10F
        )
    ) { _, _, new ->
        brushStrokePaint.color = new.color
        brushStrokePaint.strokeWidth = new.strokeSize
        invalidate()
    }

    /**
     * stores multiple drawings
     */
    private val draws: MutableList<Draw> = mutableListOf()

    /**
     * The current line being drawn in the canvas
     */
    private lateinit var currentDraw: Draw

    private val history: MutableList<HistoryPoint> = mutableListOf(
        HistoryPoint(
            Date().time,
            draws
        )
    )

    private var isFirstPoint: Boolean = true

    private var currentSnapshotIndex: Int = 0

    private val brushStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = drawingOptions.color
        strokeWidth = drawingOptions.strokeSize
        isAntiAlias = true
    }
    private val path = Path()

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // Checks for the event that occurs
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isFirstPoint = true
                currentDraw = Draw(
                    points = mutableListOf(Point(event.x, event.y)),
                    drawingOptions = drawingOptions
                )
                saveSnapshot()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                isFirstPoint = false
                currentDraw.points.add(Point(event.x, event.y))
                saveSnapshot()
            }
            MotionEvent.ACTION_UP -> {
                isFirstPoint = false
                currentDraw.also { draws.add(it) }

            }
            else -> return false
        }

        // Schedules a repaint.
        // Force a view to draw.
        postInvalidate()
        return true

    }

    fun updatePath() {
        // To get a very smooth path we do not simply want to draw lines between two consecutive points,
        // but rather draw a cubic Bezier curve between two consecutive points through two calculated control
        // points. The control points are calculated based on the previous point and the next point, which
        // means that you always have to draw one point in the past.
        //
        // Imagine the user is drawing on screen and as the user drags his finger around on the screen, you receive
        // multiple points. The last point that you receive is point P4. The point that you received prior to that 
        // is point P3 and so on. Now in order to get a smooth drawing, you'll want to draw a cubic Bezier curve between
        // P2 and P3 through control points that are calculated using P1 and P4.
        // 
        // This also means that in order to actually reach the last point that you've received (P4 in the above scenario),
        // you'll have to draw once more **after** the user's finger has already left the screen.
        //
        // If the user only taps on the screen instead of dragging their finger around, you should draw a point.

        // The algorithm below implements the described behavior from above. You only need to fetch the appropriate
        // points from your custom data structure.
        val points = currentDraw.points

        if (isFirstPoint) {
            val point: Point = points.first()
            path.moveTo(point.x, point.y)
        } else {
            if(points.size > 2) {
                val point: Point = points[points.size -2]

                val lastPoint: Point = points[points.size - 3]

                val nextPoint: Point = points[points.size - 1]
                val beforeLastPoint: Point = if (points.size > 4) points[points.size -4] else lastPoint

                val pointDx: Float
                val pointDy: Float
                if (nextPoint == null) {
                    pointDx = (point.x - lastPoint.x) / SMOOTH_VAL
                    pointDy = (point.y - lastPoint.y) / SMOOTH_VAL
                } else {
                    pointDx = (nextPoint.x - lastPoint.x) / SMOOTH_VAL
                    pointDy = (nextPoint.y - lastPoint.y) / SMOOTH_VAL
                }

                val lastPointDx = (point.x - beforeLastPoint.x) / SMOOTH_VAL
                val lastPointDy = (point.y - beforeLastPoint.y) / SMOOTH_VAL

                path.cubicTo(
                    lastPoint.x + lastPointDx,
                    lastPoint.y + lastPointDy,
                    point.x - pointDx,
                    point.y - pointDy,
                    point.x,
                    point.y
                )
            }

        }
    }

    //region public API
    /**
     * Set the stroke color
     * @param color the color of the stroke
     */
    fun setStrokeColor(color: Int) {
        drawingOptions = drawingOptions.copy(color = color)
        saveSnapshot()
    }

    /**
     * Sets the size of the stroke
     * @param size the size
     */
    fun setStrokeSize(size: Float) {
        drawingOptions = drawingOptions.copy(strokeSize = size)
        saveSnapshot()
    }

    /**
     * Move a step back in the history
     */
    fun stepBack() {
        currentSnapshotIndex = max(0, currentSnapshotIndex - 1)
        invalidate()
    }

    /**
     * Move a stepo forward i the history
     */
    fun stepForward() {
        currentSnapshotIndex = min(currentSnapshotIndex + 1, history.size - 1)
        invalidate()

    }

    /**Cleans the current canvas.*/
    fun clean() {
        path.reset()
        invalidate()
    }
    //endregion

    //region private API
    /**
     * Saves the current snapshot of the drawing
     */
    private fun saveSnapshot() {

        //clears all the history from the current hustory index if we make a change.
        if(currentSnapshotIndex != 0) {
            for (index in currentSnapshotIndex until history.size) {
                history.removeAt(index)
            }
        }

        //add next history item
        history.add(
            HistoryPoint(
                time = Date().time,
                draws = draws
            )
        )

        //move the history to the last point
        currentSnapshotIndex = history.size -1
    }
    //endregion

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(this::currentDraw.isInitialized) {
            updatePath()
            canvas.drawPath(path, brushStrokePaint)
        }

    }


    companion object {
        private const val SMOOTH_VAL = 3
    }
}