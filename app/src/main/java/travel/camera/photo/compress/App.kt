package travel.camera.photo.compress

import android.app.Application
import android.content.Context
import com.jushi.library.base.BaseApplication
import com.jushi.library.utils.CrashHandler
import com.tencent.bugly.crashreport.CrashReport

class App : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        //全局异常捕获
//        CrashHandler.getInstance().init(this)
        CrashReport.initCrashReport(this, "9404b8a21a", true)
    }
}