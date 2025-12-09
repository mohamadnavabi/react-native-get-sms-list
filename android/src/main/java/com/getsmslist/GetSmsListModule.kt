package com.getsmslist

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.*

private const val PERMISSION = android.Manifest.permission.READ_SMS
private val TAG = GetSmsListModule::class.java.simpleName

class GetSmsListModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String = NAME

  @SuppressLint("Recycle")
  @ReactMethod
  fun readSMS(options: ReadableMap, promise: Promise) {
    if (!hasReadSMSPermissions()) {
      // فقط reject کن، permission رو JS بگیره
      promise.reject(
        "NO_PERMISSION",
        "READ_SMS permission is not granted. Request it from JS before calling readSMS."
      )
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
        (options.getString("orderBy") ?: "date") + limit
      )

      promise.resolve(cursorToWritableArray(cursor))
    } catch (e: Exception) {
      promise.reject("READ_SMS_ERROR", e)
    }
  }

  private fun hasReadSMSPermissions(): Boolean {
    // برای M به بعد چک runtime permission؛ قبلش همیشه مجازه
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      true
    } else {
      ContextCompat.checkSelfPermission(reactContext, PERMISSION) == PackageManager.PERMISSION_GRANTED
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

    options.getString("thread_id")?.let { threadId ->
      selectionParts.add("thread_id = ?")
      selectionArgs.add(threadId)
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

  companion object {
    const val NAME = "GetSmsList"
  }
}
