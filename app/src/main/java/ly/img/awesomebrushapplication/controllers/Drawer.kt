package ly.img.awesomebrushapplication.controllers

import ly.img.awesomebrushapplication.data.model.Brush
import ly.img.awesomebrushapplication.data.model.Draw

/**
 * This is an abstraction to make a [Draw] with a [Brush]
 *
 * - In order to proceed with this excercise and platform we created an Android
 * Specific Drawer implementation. @see [AndroidBezierDrawer] for mor details.
 */
internal interface Drawer {
    /**
     * Draw with a brush.
     */
    fun draw(draw: Draw, brush: Brush)

    /**
     * Indicates that we finished drawing.
     */
    fun finishDrawing()

    /**
     * Clears all the drawings in this drawer.
     */
    fun clear()
}