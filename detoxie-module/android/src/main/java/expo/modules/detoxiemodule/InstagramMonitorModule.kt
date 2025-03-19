package expo.modules.detoxiemodule  

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class InstagramMonitorModule : Module() {
    private var sessionTime: Long = 0
    private var totalTime: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.yourcompany.REELS_SESSION_STARTED" -> {
                    sendEvent("reelsSessionStarted", mapOf())
                }
                "com.yourcompany.REELS_SESSION_ENDED" -> {
                    sessionTime = intent.getLongExtra("sessionTime", 0)
                    totalTime = intent.getLongExtra("totalTime", 0)
                    sendEvent("reelsSessionEnded", mapOf(
                        "sessionTime" to sessionTime,
                        "totalTime" to totalTime
                    ))
                }
                "com.yourcompany.REELS_TIME_UPDATE" -> {
                    val currentSessionTime = intent.getLongExtra("sessionTime", 0)
                    val currentTotalTime = intent.getLongExtra("totalTime", 0)
                    sendEvent("reelsTimeUpdate", mapOf(
                        "sessionTime" to currentSessionTime,
                        "totalTime" to currentTotalTime
                    ))
                }
            }
        }
    }

    override fun definition() = ModuleDefinition {
        Name("InstagramMonitor")

        Events("reelsSessionStarted", "reelsSessionEnded", "reelsTimeUpdate")

        OnCreate {
            val filter = IntentFilter().apply {
                addAction("com.yourcompany.REELS_SESSION_STARTED")
                addAction("com.yourcompany.REELS_SESSION_ENDED")
                addAction("com.yourcompany.REELS_TIME_UPDATE")
            }
            appContext.registerReceiver(receiver, filter)
        }

        OnDestroy {
            appContext.unregisterReceiver(receiver)
        }

        Function("openAccessibilitySettings") {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            appContext.startActivity(intent)
        }

        Function("isAccessibilityServiceEnabled") {
            val enabledServices = Settings.Secure.getString(
                appContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""
            
            return@Function enabledServices.contains("expo.modules.detoxiemodule/.InstaMonitorAccessibilityService")  
        }

        Function("getCurrentStats") {
            return@Function mapOf(
                "sessionTime" to sessionTime,
                "totalTime" to totalTime
            )
        }
    }
}
