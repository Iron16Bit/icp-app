package com.icp.firefox_app

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class CopyAssets {
     fun copyAssetDir(context: Context, assetPath: String, destDirPath: String) {
        walkAssetDir(context, assetPath) {
            copyAssetFile(context, it, "$destDirPath/$it")
        }
    }

    fun walkAssetDir(context: Context, assetPath: String, callback: ((String) -> Unit)) {
        val children = context.assets.list(assetPath) ?: return
        if (children.isEmpty()) {
            callback(assetPath)
        } else {
            for (child in children) {
                walkAssetDir(context, "$assetPath/$child", callback)
            }
        }
    }

    private fun copyAssetFile(context: Context, assetPath: String, destPath: String): File {
        val destFile = File(destPath)
        File(destFile.parent).mkdirs()
        destFile.createNewFile()

        context.assets.open(assetPath).use { src ->
            FileOutputStream(destFile).use { dest ->
                src.copyTo(dest)
            }
        }

        return destFile
    }
}