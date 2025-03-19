package expo.modules.detoxiemodule

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

class InstaMonitorAccessibilityService : AccessibilityService() {
    private val TAG = "InstaMonitorService"
    private var isInReels = false
    private var reelsStartTime: Long = 0
    private var totalReelsTime: Long = 0
    private val INSTAGRAM_PACKAGE = "com.instagram.android"
    
    // Handler for periodic data updates
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isInReels) {
                // Update ongoing session time
                val currentTime = System.currentTimeMillis()
                val sessionTime = currentTime - reelsStartTime
                
                // Send broadcast with updated time
                val intent = Intent("com.yourcompany.REELS_TIME_UPDATE")
                intent.putExtra("sessionTime", sessionTime)
                intent.putExtra("totalTime", totalReelsTime + sessionTime)
                sendBroadcast(intent)
            }
            
            // Schedule next update
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or 
                          AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                          AccessibilityEvent.TYPE_VIEW_CLICKED
        
        info.packageNames = arrayOf(INSTAGRAM_PACKAGE)
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        
        serviceInfo = info
        Log.d(TAG, "InstaMonitor Accessibility Service connected")
        
        // Start periodic updates
        handler.post(updateRunnable)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // Only process Instagram events
        if (event.packageName != INSTAGRAM_PACKAGE) return

        val rootNode = rootInActiveWindow ?: return
        
        // Check if we're in Reels
        val wasInReels = isInReels
        isInReels = isViewingReels(rootNode)
        
        if (!wasInReels && isInReels) {
            // User just entered Reels
            reelsStartTime = System.currentTimeMillis()
            Log.d(TAG, "Reels session started")
            
            // Notify app
            val intent = Intent("com.yourcompany.REELS_SESSION_STARTED")
            sendBroadcast(intent)
        } else if (wasInReels && !isInReels) {
            // User just exited Reels
            val currentTime = System.currentTimeMillis()
            val sessionTime = currentTime - reelsStartTime
            totalReelsTime += sessionTime
            
            Log.d(TAG, "Reels session ended. Duration: $sessionTime ms")
            
            // Notify app
            val intent = Intent("com.yourcompany.REELS_SESSION_ENDED")
            intent.putExtra("sessionTime", sessionTime)
            intent.putExtra("totalTime", totalReelsTime)
            sendBroadcast(intent)
        }
        
        // Clean up
        rootNode.recycle()
    }

    private fun isViewingReels(rootNode: AccessibilityNodeInfo): Boolean {
        // Method 1: Check for Reels-specific views
        val reelsIndicators = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/reel_viewer_container")
        if (reelsIndicators.isNotEmpty()) return true
        
        // Method 2: Check for Reels content description
        val reelsDesc = rootNode.findAccessibilityNodeInfosByText("Reels")
        for (node in reelsDesc) {
            if (node.isSelected) return true
        }
        
        // Method 3: Check for Reels navigation tab
        val bottomNavTabs = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/tab_bar")
        if (bottomNavTabs.isNotEmpty()) {
            val tabBar = bottomNavTabs[0]
            for (i in 0 until tabBar.childCount) {
                val child = tabBar.getChild(i) ?: continue
                // The middle tab (index 2) is typically Reels
                if (i == 2 && child.isSelected) return true
            }
        }
        
        return false
    }

    override fun onInterrupt() {
        Log.d(TAG, "InstaMonitor Accessibility Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        Log.d(TAG, "InstaMonitor Accessibility Service destroyed")
    }
}