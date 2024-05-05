package com.icp.icp_app

import android.content.Context
import android.os.Environment
import com.icp.icp_app.CopyFromAssets.unzip
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

// Utility class used to copy the icp_bundle.zip file from assets to local storage and unpack it
class CopyInit {
    fun copy(context: Context) {
        val copy = CopyFromAssets()

        copy.copyAsset("icp_bundle.zip", "/MyFiles/", "", context)

        val zip = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/icp_bundle.zip")
        val destZip = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
        unzip(zip, destZip)
        zip.delete();
    }

    fun copyExternal(srcString: String, dstString: String, fileName: String) {
        File(srcString).copyTo(
            target = File(dstString, fileName)
        )
    }
}