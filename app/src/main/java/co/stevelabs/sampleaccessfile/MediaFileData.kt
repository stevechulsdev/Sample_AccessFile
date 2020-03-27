package co.stevelabs.sampleaccessfile

import android.net.Uri
import java.util.*

data class MediaFileData(
    val id: Long,
    val dateTaken: Date,
    val displayName: String,
    val contentUri: Uri,
    var realPath: String
)