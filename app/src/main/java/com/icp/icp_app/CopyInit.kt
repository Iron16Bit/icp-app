package com.icp.icp_app

import android.content.Context
import android.os.Environment
import com.icp.icp_app.CopyFromAssets.unzip
import java.io.File

class CopyInit {
    fun copy(context: Context) {
        val copy = CopyFromAssets()

        val listAssets = context.assets.list("")?.clone()
        if (listAssets != null) {
            if (listAssets.isNotEmpty()) {
                for (file in listAssets) {
                    copy.copyAsset(file, "/MyFiles/", "", context)
                }
            }
        }

        val zip = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/icp_bundle.zip")
        val destZip = File(Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/")
        unzip(zip, destZip)
        zip.delete();
    }
}