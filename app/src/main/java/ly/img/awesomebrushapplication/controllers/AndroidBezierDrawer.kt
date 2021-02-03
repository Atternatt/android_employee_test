package ly.img.awesomebrushapplication.controllers

import android.graphics.*
import android.util.Log
import ly.img.awesomebrushapplication.data.model.Brush
import ly.img.awesomebrushapplication.data.model.Draw
import ly.img.awesomebrushapplication.data.model.Point
import java.lang.ref.WeakReference

/**
 * This Drawer uses [Canvas], [Path] & [Paint] objects, attached to Androd Framework, in order to draw on top of images.
 *
 * - As it needs the canvas from the view to print everything we created a [WeakReference] in order to prevent leaking memory.
 */
internal class AndroidBezierDrawer(canvas: Canvas) : Drawer {

    companion object {
        private const val SMOOTH_VAL = 3
    }

    private val canvas: WeakReference<Canvas> by lazy { WeakReference(canvas) }

    private var path: Path? = null
    private var paint: Paint? = null

    private var actualDraw: Draw? = null

    private val allPaths: MutableList<Pair<Path, Paint>> = mutableListOf()

    override fun draw(draw: Draw, brush: Brush) {
        val points = draw.getPath()
        //we have a new drawing
        if (actualDraw?.getId() != draw.getId()) {
            //reset the path so we can create a new one for the new drawing
            path = Path()
            paint = Paint().apply {
                strokeWidth = brush.getSize()
                color = brush.getColor()
                style = Paint.Style.STROKE
            }
            actualDraw = draw
            points.firstOrNull()?.also {
                path?.moveTo(it.x, it.y)
            }
        }

        if (points.size > 2) {
            val point: Point = points[points.size - 2]

            val lastPoint: Point = points[points.size - 3]

            val nextPoint: Point = points[points.size - 1]
            val beforeLastPoint: Point =
                if (points.size > 4) points[points.size - 4] else lastPoint

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

            path?.cubicTo(
                lastPoint.x + lastPointDx,
                lastPoint.y + lastPointDy,
                point.x - pointDx,
                point.y - pointDy,
                point.x,
                point.y
            )
        }

        Log.d("PATH", path.toString())
        allPaths.forEach { (path, paint) -> canvas.get()?.drawPath(path, paint) }
        if (path != null && paint != null) {
            canvas.get()?.drawPath(path!!, paint!!)
        } else {
            canvas.get()?.drawPath(Path(), Paint())
        }
    }

    override fun finishDrawing() {
        if (path != null && paint != null) {
            allPaths.add(path!! to paint!!)
            if (actualDraw?.getPath()?.size == 1) {
                val point = actualDraw!!.getPath().first()
                canvas.get()?.drawCircle(
                    point.x,
                    point.y,
                    paint!!.strokeWidth / 2,
                    paint!!.apply {
                        strokeWidth /= 2
                        style = Paint.Style.FILL
                    })
            }
        }
    }

    //fixme: this is clearing the whole canvas including the bitmap implement a better solution.
    override fun clear() {
        path = null
        paint = null
        allPaths.clear()
        canvas.get()?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }
}