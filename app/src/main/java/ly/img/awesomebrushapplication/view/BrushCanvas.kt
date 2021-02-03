package ly.img.awesomebrushapplication.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ly.img.awesomebrushapplication.controllers.AndroidBezierDrawer
import ly.img.awesomebrushapplication.controllers.Drawer
import ly.img.awesomebrushapplication.controllers.ImageLoader
import ly.img.awesomebrushapplication.controllers.Saver
import ly.img.awesomebrushapplication.data.model.*

class BrushCanvas @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr),
    ImageLoader{


    init {
        setWillNotDraw(false)
    }

    /**
     * The current Draw
     */
    private var currentDraw: Draw? = null

    /**
     * The current brush used to draw.
     */
    private var currentBrush: Brush = SimpleBrush(
        brushColor = Color.BLACK,
        brushSize = 10F
    )

    /**
     * the drawer object used to draw all the paths
     * take a look into [Drawer] and it's current implementation [AndroidBezierDrawer]
     */
    private var drawer: Drawer? = null

    /**
     * The bitmap that will be loaded
     */
    private var bitmap: Bitmap? = null

    /**
     * The canvas that will render the bitmap and all the drawings
     */
    private var imageCanvas: Canvas? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // Checks for the event that occurs
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentDraw = SimpleDraw()
                currentDraw?.addPoint(Point(event.x, event.y))
            }
            MotionEvent.ACTION_MOVE -> {
                currentDraw?.addPoint(Point(event.x, event.y))
            }
            MotionEvent.ACTION_UP -> {
                drawer?.finishDrawing()

            }
            else -> return false
        }

        // Schedules a repaint.
        // Force a view to draw.
        postInvalidate()
        return true

    }

    //region public API
    /**
     * Set the stroke color
     * @param color the color of the stroke
     */
    fun setStrokeColor(color: Int) {
        currentBrush = SimpleBrush(
            brushColor = color,
            brushSize = currentBrush.getSize()

        )
    }

    /**
     * Sets the size of the stroke
     * @param size the size
     */
    fun setStrokeSize(size: Float) {
        currentBrush = SimpleBrush(
            brushColor = currentBrush.getColor(),
            brushSize = size

        )
    }

    /**
     * Cleans the current canvas.
     */
    fun clean() {
        drawer?.clear()
        invalidate()
    }

    /**
     * Loads a specific bitmap into the canvas in order to draw on top of it
     * @param bitmap the selected image
     * @see [ImageLoader] to see the interface's full signature.
     */
    override suspend fun loadImage(bitmap: Bitmap) {
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val scaleWidth: Float = this.width.toFloat() / width
        val scaleHeight: Float = this.height.toFloat() / height

        // create a matrix for the manipulation
        val matrix = Matrix()

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)

        // recreate the new Bitmap
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        imageCanvas = Canvas(this.bitmap!!)
        drawer = AndroidBezierDrawer(imageCanvas!!)
    }

    /**
     * Gets the current bitmap with all its drawings.
     * @return [Bitmap] the image with all the drawings.
     */
    fun getCurrentBitmap(): Bitmap? = bitmap

    //endregion

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (imageCanvas != null) {
            currentDraw?.also {
                drawer?.draw(it, currentBrush)
            }
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        }
    }
}