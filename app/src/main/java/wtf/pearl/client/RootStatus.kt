package wtf.pearl.client

import android.os.Build
import java.io.File

object RootStatus {
    fun isLikelyRooted(): Boolean {
        val suPaths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/adb/magisk",
            "/data/adb/ksu"
        )
        return suPaths.any { File(it).exists() } ||
            Build.TAGS?.contains("test-keys") == true
    }
}
