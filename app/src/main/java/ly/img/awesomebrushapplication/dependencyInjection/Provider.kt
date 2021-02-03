package ly.img.awesomebrushapplication.dependencyInjection

import android.content.Context
import ly.img.awesomebrushapplication.controllers.AndroidGallerySaver
import ly.img.awesomebrushapplication.controllers.Saver

internal class Provider(private val context: Context) {

    companion object {
        lateinit var instance: Provider
        fun init(context: Context) {
            instance = Provider(context)
        }
    }

    fun providesSaver(): Saver = AndroidGallerySaver(context)
}