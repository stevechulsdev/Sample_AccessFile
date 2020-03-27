package co.stevelabs.sampleaccessfile

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "[${MainActivity::class.java.simpleName}]"

    private val filePath = "/storage/emulated/0/Movies/20200327-102853.mp4"

    private var isFirstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()

        checkPermission {
            if(isFirstRun) {
                isFirstRun = false
                val galleryList = FileManager().getFileList(this, FileManager.MediaStoreFileType.VIDEO)

                rv_gallery.setHasFixedSize(true)
                rv_gallery.adapter = AdapterGallery(galleryList) { imageUri ->
                    val realPath = FileManagerJava().getUriRealPath(this, imageUri)

                    Toast.makeText(this, "$realPath", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "$realPath")

                    stopVideo()
                    releaseVideo()

                    prepareVideo(realPath) {
                        playVideo()
                    }
                }
            }
        }
    }

    private fun checkPermission(addCompletePermissionListener: () -> Unit) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(it.areAllPermissionsGranted()) {
                            Log.e(TAG, "Permission All Granted")

                            addCompletePermissionListener.invoke()
                        }
                        else {
                            Log.e(TAG, "Permission denied")

                            Toast.makeText(this@MainActivity, "권한 거부", Toast.LENGTH_SHORT).show()

                            finish()
                        }
                    }?: run {
                        Log.e(TAG, "permission check error")
                        Toast.makeText(this@MainActivity, "report null", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.let {
                        Toast.makeText(this@MainActivity, "권한 재요청", Toast.LENGTH_SHORT).show()
                        it.continuePermissionRequest()
                    }?: run {
                        Toast.makeText(this@MainActivity, "token null", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            })
            .withErrorListener {
                Log.e(TAG, "Dexter Permission Error")
                Toast.makeText(this@MainActivity, "Dexter Permission Error", Toast.LENGTH_SHORT).show()
            }.check()
    }

    private fun prepareVideo(videoPath: String, addCompletePrepareListener: () -> Unit) {
        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultRenderersFactory(this), DefaultTrackSelector())

        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when(playbackState) {
                    Player.STATE_READY -> {
                        addCompletePrepareListener.invoke()
                    }
                }
            }
        })

        player.volume = 0f

        pv_original.player = player

        val userAgent = Util.getUserAgent(this, "test")
        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, userAgent)).createMediaSource(Uri.parse(videoPath))
        val loopingSource = LoopingMediaSource(mediaSource)
        player.prepare(loopingSource, true, false)
    }

    private fun playVideo() {
        pv_original.player?.playWhenReady = true
    }

    private fun stopVideo() {
        pv_original.player?.playWhenReady = false
    }

    private fun releaseVideo() {
        pv_original.player?.release()
        pv_original.player = null
    }
}
