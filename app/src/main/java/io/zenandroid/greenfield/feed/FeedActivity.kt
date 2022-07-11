package io.zenandroid.greenfield.feed

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import io.zenandroid.greenfield.R
import io.zenandroid.greenfield.rememberStateWithLifecycle
import io.zenandroid.greenfield.ui.GreenfieldTemplate2022Theme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class FeedActivity : AppCompatActivity() {

    private val viewModel: FeedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenfieldTemplate2022Theme {
                val state by rememberStateWithLifecycle(viewModel.state)

                FeedUI(
                        state = state,
                        listener = { onEvent(it) }
                )
            }
        }
    }

    private fun onEvent(action: FeedAction) {
        when(action) {
            is FeedAction.Share -> action.item.media?.m?.let(::sendEmail)
            is FeedAction.Browse -> action.item.link?.let(::browseURL)
            is FeedAction.Save -> action.item.media?.m?.let { save(it, action.item.title ?: "Default title", action.item.tags ?: "Default Description") }
            else -> viewModel.onEvent(action)
        }
    }

    private fun browseURL(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

    private fun save(url: String, title: String, description: String) {
        lifecycleScope.launch{
            val loader = ImageLoader(this@FeedActivity)
            val request = ImageRequest.Builder(this@FeedActivity)
                .data(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.61 Mobile Safari/537.36")
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            saveBitmapToGalleryImplWithPermissionCheck(bitmap, title, description)
        }
    }

    fun sendEmail(url: String) {
        val TO = arrayOf("")
        val CC = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, url)
            putExtra(Intent.EXTRA_EMAIL, TO)
            putExtra(Intent.EXTRA_CC, CC)
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)))
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun saveBitmapToGalleryImpl(bitmap: Bitmap, title: String, description: String) {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, title, description)
        if (path == null) {
            viewModel.onEvent(FeedAction.Error("Save failed"))
        } else {
            Toast.makeText(this, "Saved under $path", Toast.LENGTH_LONG).show()
        }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showPermissionDeniedMessage() {
        viewModel.onEvent(FeedAction.Error("Cannot save image because permission request was rejected"))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}