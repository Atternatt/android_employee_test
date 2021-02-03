package ly.img.awesomebrushapplication

import android.app.Application
import ly.img.awesomebrushapplication.dependencyInjection.Provider

class BrushApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Provider.init(this.applicationContext)
    }
}