package com.dlong.rep.dl10roundmenuview

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dlong.rep.dl10roundmenuview.databinding.ActivityMainBinding
import com.dlong.rep.dlroundmenuview.Interface.OnMenuClickListener
import com.dlong.rep.dlroundmenuview.Interface.OnMenuLongClickListener
import com.dlong.rep.dlroundmenuview.Interface.OnMenuTouchListener
import com.mhy.tv.remotecontrol.TVControl

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //检查是否已经授予权限，大于6.0的系统适用，小于6.0系统默认打开，无需理会
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //没有权限，需要申请权限，因为是打开一个授权页面，所以拿不到返回状态的，所以建议是在onResume方法中从新执行一次校验
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + getPackageName())
           // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivityForResult(intent,100)
        } else {
            TVControl.initTV(applicationContext)
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
                // 单击
                Log.i("lambda 单击", "点击了：$position")
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
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + getPackageName())
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(intent,100)
            }
        }
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