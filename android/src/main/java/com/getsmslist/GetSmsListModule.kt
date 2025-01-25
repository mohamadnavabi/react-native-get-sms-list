package com.getsmslist

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener

private const val REQUEST_ENABLE_BT = 1
private const val REQUEST_READ_SMS = 2
private const val EVENT_PERMISSION = "permission"
private const val PERM_DENIED = 2
private const val PERM_ALLOWED = 3
private const val PERMISSION = Manifest.permission.READ_SMS
private val TAG = GetSmsListModule::class.java.simpleName

class GetSmsListModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), PermissionListener, ActivityEventListener {

  init {
    reactContext.addActivityEventListener(this)
  }

  @SuppressLint("Recycle")
  @ReactMethod
  fun readSMS(options: ReadableMap, promise: Promise) {
    if (!hasReadSMSPermissions()) {
      requestReadSMSPermission()
      return
    }

    try {
      val (selection, selectionArgs) = buildSelection(options)

      val boxType: String = if (options.hasKey("type")) "/" + options.getString("type") else ""
      val limit: String = if (options.hasKey("limit")) " limit " + options.getInt("limit") else ""

      val cursor = reactContext.contentResolver.query(
        Uri.parse("content://sms$boxType"),
        arrayOf("_id", "address", "date", "body", "thread_id"),
        selection,
        selectionArgs,
        (options.getString("orderBy") ?: "date")  + limit
      )

      promise.resolve(cursorToWritableArray(cursor))
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  private fun buildSelection(options: ReadableMap): Pair<String?, Array<String>?> {
    val selectionParts = mutableListOf<String>()
    val selectionArgs = mutableListOf<String>()

    options.getString("minDate")?.let { minDate ->
      selectionParts.add("date > ?")
      selectionArgs.add(minDate)
    }

    options.getString("maxDate")?.let { maxDate ->
      selectionParts.add("date < ?")
      selectionArgs.add(maxDate)
    }

    options.getString("id")?.let { id ->
      selectionParts.add("_id = ?")
      selectionArgs.add(id)
    }

    options.getString("thread_id")?.let { thread_id ->
      selectionParts.add("thread_id = ?")
      selectionArgs.add(thread_id)
    }

    options.getString("address")?.let { address ->
      selectionParts.add("address = ?")
      selectionArgs.add(address)
    }

    return if (selectionParts.isNotEmpty()) {
      Pair(selectionParts.joinToString(" AND "), selectionArgs.toTypedArray())
    } else {
      Pair(null, null)
    }
  }

  private fun cursorToWritableArray(cursor: Cursor?): WritableArray {
    val array: WritableArray = WritableNativeArray()
    cursor?.use {
      while (it.moveToNext()) {
        val wm = Arguments.createMap().apply {
          putString("id", it.getString(0))
          putString("address", it.getString(1))
          putString("date", it.getString(2))
          putString("body", it.getString(3))
          putString("thread_id", it.getString(4))
        }
        array.pushMap(wm)
      }
    }
    return array
  }

  private fun hasReadSMSPermissions(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
      reactContext.checkSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED

  private fun requestReadSMSPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      (currentActivity as? PermissionAwareActivity)?.requestPermissions(
        arrayOf(PERMISSION),
        REQUEST_READ_SMS,
        this
      ) ?: Log.e(TAG, "requestReadSMSPermission: Activity is null or doesn't implement PermissionAwareActivity")
    }
  }

  override fun getName(): String = NAME

override fun onRequestPermissionsResult(
        requestCode: Int, 
        permissions: Array<String>, 
        grantResults: IntArray
    ): Boolean {
        return if (requestCode == REQUEST_READ_SMS) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                Log.d(TAG, "SMS permission granted")
            } else {
                Log.d(TAG, "SMS permission denied")
            }
            true
        } else {
            false
        }
    }

  private fun sendEvent(eventName: String, params: WritableMap?) {
    reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
  }

  override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
    activity?.let {
      Log.d(TAG, "onActivityResult, requestCode : $requestCode, resultCode : $resultCode")
      if (requestCode == REQUEST_ENABLE_BT) {
        sendEvent(EVENT_PERMISSION, Arguments.createMap().apply {
          putInt("type", REQUEST_ENABLE_BT)
          putInt("status", if (resultCode == Activity.RESULT_OK) PERM_ALLOWED else PERM_DENIED)
        })
      }
    }
  }

  override fun onNewIntent(intent: Intent?) {}

  companion object {
    const val NAME = "GetSmsList"
  }
}
