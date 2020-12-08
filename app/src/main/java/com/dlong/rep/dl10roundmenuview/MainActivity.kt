package com.dlong.rep.dl10roundmenuview

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dlong.rep.dl10roundmenuview.databinding.ActivityMainBinding
import com.mhy.tv.remotecontrol.TVControl

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //检查是否已经授予权限，大于6.0的系统适用，小于6.0系统默认打开，无需理会
        if (TVControl.hasOverlayPermission(this)) {
            TVControl.initTV(applicationContext)
        } else {
           TVControl.openOverlay(this,100)
        }

//        // 单击
//        binding.dlRmv.setOnMenuClickListener(object : OnMenuClickListener {
//            override fun OnMenuClick(position: Int) {
//                //Toast.makeText(mContext, "点击了："+position,Toast.LENGTH_SHORT).show();
//                Log.i("单击", "点击了：$position")
//            }
//        })
//
//        // 长按
//        binding.dlRmv.setOnMenuLongClickListener(object : OnMenuLongClickListener{
//            override fun OnMenuLongClick(position: Int) {
//                Log.i("长按", "点击了：$position")
//            }
//        })
//
//        // 触摸
//        binding.dlRmv.setOnMenuTouchListener(object : OnMenuTouchListener {
//            override fun OnTouch(event: MotionEvent?, position: Int) {
//                Log.v("触摸", "事件=${event.toString()}")
//                Log.d("触摸", "位置=$position")
//            }
//        })

        // 统一lambda接口
        binding.dlRmv.setOnMenuListener {
            onMenuClick { position ->
                when(position){
//                    0-> XXPermissions.with(this@MainActivity)
//                        .permission(Permission.SYSTEM_ALERT_WINDOW)
//                        .request(object : OnPermissionCallback {
//                            override fun onGranted(
//                                permissions: MutableList<String>?,
//                                all: Boolean
//                            ) {
//
//                            }
//
//                            override fun onDenied(
//                                permissions: MutableList<String>?,
//                                never: Boolean
//                            ) {
//
//                            }
//
//                        })
                    1-> TVControl.openOverlay(this@MainActivity,100)
                    -1-> TVControl.initTV(applicationContext)
                }

                // 单击
//                Log.i("lambda 单击", "点击了：$position")
            }

            onMenuLongClick { position ->
                // 长按
                Log.i("lambda 长按", "点击了：$position")
            }

            onTouch { event, position ->
                // 触摸
                Log.v("lambda 触摸", "事件=${event.toString()}")
                Log.d("lambda 触摸", "位置=$position")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==100){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)){
                TVControl.initTV(applicationContext)
             }else{
                TVControl.openOverlay(this,100)
            }
        }
//        if (requestCode==XXPermissions.REQUEST_CODE){
//            Toast.makeText(this, "xxper", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        Log.e("mhylog","keyCode=${event?.keyCode},action=${event?.action}")
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.e("mhylogdown","keyCode=$keyCode,action=${event?.action}")
        return super.onKeyDown(keyCode, event)
    }
}