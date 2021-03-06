package ly.img.awesomebrushapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.awesomebrushapplication.controllers.Saver
import ly.img.awesomebrushapplication.dependencyInjection.Provider
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private val saver: Saver = Provider.instance.providesSaver()


    /**
     * todo: resize saved image in order to fit the image size not the window size
     * todo: create a history so the app can navigate back to let a user fix drawings.
     *
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_image_btn.setOnClickListener { onPressLoadImage() }
        save_image_btn.setOnClickListener { onPressSave() }

        slider_stroke_width.addOnChangeListener { _, value, _ ->
            onSizeChanged(value)
        }

        red.setOnClickListener {
            onChangeColor(Color.RED)
            slider_stroke_width.setThumbStrokeColorResource(R.color.red)
        }


        black.setOnClickListener {
            slider_stroke_width.setThumbStrokeColorResource(R.color.black)
            onChangeColor(Color.BLACK)
        }

        clean_btn.setOnClickListener { canvas.clean() }

    }

    private fun onPressLoadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            intent.type = "image/*"
        } else {
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        startActivityForResult(intent, GALLERY_INTENT_RESULT)
    }

    private fun handleGalleryImage(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        launch {
            canvas.loadImage(bitmap)
        }
    }

    @MainThread
    private fun onPressSave() {
        launch {
            saveBrushToGallery()
        }
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }

    private fun onChangeColor(@ColorInt color: Int) {
        // ColorInt (8bit) color is ok, do not waste time here.
        canvas.setStrokeColor(color)
    }

    private fun onSizeChanged(size: Float) {
        canvas.setStrokeSize(size)
    }

    @WorkerThread
    private fun saveBrushToGallery() {
        val bitmap: Bitmap? = canvas.getCurrentBitmap()
        if(bitmap != null) {
            saver.save(bitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK && requestCode == GALLERY_INTENT_RESULT) {
            val uri = data.data
            if (uri != null) {
                handleGalleryImage(uri)
            }
        }
    }

    companion object {
        const val GALLERY_INTENT_RESULT = 0
    }

}