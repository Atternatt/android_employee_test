package ly.img.awesomebrushapplication

import android.app.Application
import ly.img.awesomebrushapplication.dependencyInjection.Provider

/**
 * This Custom application is created just to initialize the dependency injection provider.
 */
class BrushApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Provider.init(this.applicationContext)
    }
}