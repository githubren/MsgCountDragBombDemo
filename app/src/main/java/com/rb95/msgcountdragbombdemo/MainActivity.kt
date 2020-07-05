package com.rb95.msgcountdragbombdemo

import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.EventLog
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permission
import java.security.Permissions
import java.util.jar.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.Settings
import java.util.*

class MainActivity : AppCompatActivity(),ActivityCompat.OnRequestPermissionsResultCallback {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MyAdapter(this@MainActivity)
        }

//        val permissionCheck = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE)
//        if (permissionCheck != PERMISSION_GRANTED)
//            ActivityCompat.requestPermissions(this, arrayOf(READ_PHONE_STATE),1)


//        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
////        val imei = tm.imei
////        Log.e("TAG","imei:$imei")
//        val androidId = Settings.System.getString(contentResolver,Settings.Secure.ANDROID_ID)
//        Log.e("TAG","androidId:$androidId")
////        Log.e("TAG","ssn:${tm.simSerialNumber}")
////        Log.e("TAG","subscriberId :${tm.line1Number}")
////        Log.e("TAG","deviceId:${tm.deviceId}")
//        val uuid = UUID.randomUUID().toString()
//        Log.e("TAG","uuid:$uuid")
//        val systemVersion = Build.VERSION.RELEASE
//        Log.e("TAG","systemVersion:$systemVersion")
//        val deviceModel = Build.MODEL
//        Log.e("TAG","deviceModel:$deviceModel")
//        val deviceBrand = Build.BRAND
//        Log.e("TAG","deviceBrand:$deviceBrand")
//        val time = System.currentTimeMillis()/1000
//        Log.e("TAG","time:$time")
//        val mdstr = MD5Util.getMD5Str(androidId+uuid+systemVersion+deviceModel+deviceBrand+time,MD5Util.MD5_UPPER_CASE)
//        Log.e("TAG","mdstr:$mdstr")
    }

//    @SuppressLint("MissingPermission")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == 1){
//            permissions.forEachIndexed { index, s ->
//                if (grantResults[index] == PERMISSION_GRANTED){
//                    val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                    val deviceId = tm.deviceId
//                    Log.e("TAG","deviceId:$deviceId")
//                }
//            }
//        }
//    }
}
