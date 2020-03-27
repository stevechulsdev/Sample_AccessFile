package co.stevelabs.sampleaccessfile

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class FileManager {
    private val TAG = "[${FileManager::class.java.simpleName}]"

    enum class MediaStoreFileType(
        val externalContentUri: Uri,
        val mimeType: String,
        val pathByDCIM: String
    ) {
        IMAGE(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*", "/image"),
        AUDIO(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "audio/*", "/audio"),
        VIDEO(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*", "/video")
    }

    fun getFileList(context: Context, type: MediaStoreFileType): ArrayList<MediaFileData> {
        val fileList = ArrayList<MediaFileData>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_TAKEN
//            MediaStore.Files.FileColumns.ORIENTATION
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"

        val cursor = context.contentResolver.query(
            type.externalContentUri,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dateTakenColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val displayNameColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
//            val orientationColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.ORIENTATION)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val dateTaken = Date(it.getLong(dateTakenColumn))
                val displayName = it.getString(displayNameColumn)
                val contentUri = Uri.withAppendedPath(
                    type.externalContentUri,
                    id.toString()
                )
//                val orientation = it.getInt(orientationColumn)

//                Log.e(TAG, "id: $id, display_name: $displayName, date_taken: $dateTaken, content_uri: $contentUri, orientation: $orientation\n")
                Log.e(TAG, "id: $id, display_name: $displayName, date_taken: $dateTaken, content_uri: $contentUri\n")

                fileList.add(MediaFileData(id, dateTaken, displayName, contentUri, ""))
            }

            it.close()
        }

        return fileList
    }
}