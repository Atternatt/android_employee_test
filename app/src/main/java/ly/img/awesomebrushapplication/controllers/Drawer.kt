package ly.img.awesomebrushapplication.controllers

import ly.img.awesomebrushapplication.data.model.Brush
import ly.img.awesomebrushapplication.data.model.Draw

internal interface Drawer {
    fun draw(draw: Draw, brush: Brush)
    fun finishDrawing()
    fun clear()
}