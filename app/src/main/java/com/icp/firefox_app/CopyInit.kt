package com.icp.firefox_app

import android.content.Context
import android.os.Environment
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

        val subDir1 = Environment.getExternalStorageDirectory().absolutePath + "/MyFiles/utils/"
        val dir1: File = File(subDir1)
        if (!dir1.exists()) {
            dir1.mkdirs()
        }

        val subDir2 = "$subDir1/cpp/"
        val dir2: File = File(subDir2)
        if (!dir2.exists()) {
            dir2.mkdirs()
        }

        val listAssets2 = context.assets.list("utils/cpp/")?.clone()
        if (listAssets2 != null) {
            if (listAssets2.isNotEmpty()) {
                for (file in listAssets2) {
                    copy.copyAsset(file, "/MyFiles/utils/cpp/", "utils/cpp/", context)
                }
            }
        }

        val subDir3 = "$subDir1/java/"
        val dir3: File = File(subDir3)
        if (!dir3.exists()) {
            dir3.mkdirs()
        }

        val listAssets3 = context.assets.list("utils/java/")?.clone()
        if (listAssets3 != null) {
            if (listAssets3.isNotEmpty()) {
                for (file in listAssets3) {
                    copy.copyAsset(file, "/MyFiles/utils/java/", "utils/java/", context)
                }
            }
        }

        val subDir4 = "$subDir1/python/"
        val dir4: File = File(subDir4)
        if (!dir4.exists()) {
            dir4.mkdirs()
        }

        val listAssets4 = context.assets.list("utils/python/")?.clone()
        if (listAssets4 != null) {
            if (listAssets4.isNotEmpty()) {
                for (file in listAssets4) {
                    copy.copyAsset(file, "/MyFiles/utils/python/", "utils/python/", context)
                }
            }
        }

        val subDir5 = "$subDir4/pyodide/"
        val dir5: File = File(subDir5)
        if (!dir5.exists()) {
            dir5.mkdirs()
        }

        val listAssets5 = context.assets.list("utils/python/pyodide/")?.clone()
        if (listAssets5 != null) {
            if (listAssets5.isNotEmpty()) {
                for (file in listAssets5) {
                    copy.copyAsset(file, "/MyFiles/utils/python/pyodide/", "utils/python/pyodide/", context)
                }
            }
        }
    }
}