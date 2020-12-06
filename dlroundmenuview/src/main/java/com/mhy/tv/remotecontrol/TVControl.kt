package com.mhy.tv.remotecontrol

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.dlong.rep.dlroundmenuview.DLRoundMenuView
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader


/**
 * author    : mahongyin
 * e-mail    : mhy.work@foxmail.com
 * date      : 2020-12-05 14:08
 * introduce : tv开发 遥控器辅助
 */
object TVControl {
    private var wmParams: WindowManager.LayoutParams? = null//悬浮窗的布局

    /**
     * 变量
     */
    private var mWindowManager: WindowManager? = null//创建浮动窗口设置布局参数的对象
    private var view: View? = null
    fun initTV(appContext: Context) {

        //已经有权限，可以直接显示悬浮窗
        view = LayoutInflater.from(appContext).inflate(R.layout.tv_control, null)
        setTVEvent(view)
        if (mWindowManager == null) {
            mWindowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            wmParams = getParams() //设置好悬浮窗的参数
            mWindowManager?.addView(view, wmParams)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTVEvent(view: View?) {
//        view?.findViewById<ImageView>(R.id.img_on)?.setOnClickListener {
//            if (view.findViewById<RelativeLayout>(R.id.tvcontrol)?.visibility == View.VISIBLE) {
//                view.findViewById<RelativeLayout>(R.id.tvcontrol)?.visibility =
//                    View.GONE
//            } else {
//                view.findViewById<RelativeLayout>(R.id.tvcontrol)?.visibility =
//                    View.VISIBLE
//            }
//        }
        var width = 0
        var height = 0
        var mStartX = 0f
        var mStartY = 0f
        var touchStartX = 0f
        var touchStartY = 0f
        var touchStartTime = 0L
        view?.findViewById<ImageView>(R.id.img_on)
            ?.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val action = event.action
                    val mStopX = event.rawX
                    val mStopY = event.rawY
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            // 以当前父视图左上角为原点
                            mStartX = event.rawX
                            mStartY = event.rawY
                            touchStartX = event.rawX
                            touchStartY = event.rawY
                            touchStartTime = System.currentTimeMillis() //获取当前时间戳
                        }
                        MotionEvent.ACTION_MOVE -> {
                            width = (mStopX - mStartX).toInt()
                            height = (mStopY - mStartY).toInt()
                            mStartX = mStopX
                            mStartY = mStopY
                            updateSuspend(width, height)
                        }
                        MotionEvent.ACTION_UP -> {
                            width = (mStopX - mStartX).toInt()
                            height = (mStopY - mStartY).toInt()
                            updateSuspend(width, height)
//                        val layoutParams = view.getLayoutParams()
//缓存一下当前位置save(layoutParams.x + width, layoutParams.y + height)
                            if (mStopX - touchStartX < 30 && mStartY - touchStartY < 30 && System.currentTimeMillis() - touchStartTime < 300) {
                                //左右上下移动距离不超过30的，并且按下和抬起时间少于300毫秒，算是单击事件，进行回调
                                if (view.findViewById<RelativeLayout>(R.id.tvcontrol)?.visibility == View.VISIBLE) {
                                    view?.findViewById<RelativeLayout>(R.id.tvcontrol)?.visibility =
                                        View.GONE
                                } else {
                                    view?.findViewById<RelativeLayout>(R.id.tvcontrol)?.visibility =
                                        View.VISIBLE
                                }
                                return false
                            }
                        }
                    }
                    return true
                }

            })


        view?.findViewById<Button>(R.id.v0)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN) }
        view?.findViewById<Button>(R.id.v1)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_VOLUME_UP) }
        view?.findViewById<Button>(R.id.power)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_TV_POWER) }
        view?.findViewById<Button>(R.id.setting)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_SETTINGS) }
        view?.findViewById<Button>(R.id.menu)?.setOnLongClickListener {
            sendKeyCode(KeyEvent.KEYCODE_APP_SWITCH)
            true
        }
        view?.findViewById<Button>(R.id.menu)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_MENU) }
        view?.findViewById<Button>(R.id.home)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_HOME) }
        view?.findViewById<Button>(R.id.back)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_BACK) }
        view?.findViewById<Button>(R.id.before)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_PAGE_UP) }
        view?.findViewById<Button>(R.id.after)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_PAGE_DOWN) }
        view?.findViewById<Button>(R.id.tv)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_TV) }
        view?.findViewById<Button>(R.id.win)
            ?.setOnClickListener { sendKeyCode(KeyEvent.KEYCODE_WINDOW) }
        view?.findViewById<DLRoundMenuView>(R.id.dl_rmv)?.setOnMenuListener {
            onMenuClick { position ->
                // 单击
                Log.i("lambda 单击", "点击了：$position")
                when (position) {
                    -1 -> sendKeyCode(KeyEvent.KEYCODE_DPAD_CENTER)
                    0 -> sendKeyCode(KeyEvent.KEYCODE_DPAD_UP)
                    1 -> sendKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT)
                    2 -> sendKeyCode(KeyEvent.KEYCODE_DPAD_DOWN)
                    3 -> sendKeyCode(KeyEvent.KEYCODE_DPAD_LEFT)
                }
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

    fun hide() {
        mWindowManager?.removeView(view);
    }

    fun getParams(): WindowManager.LayoutParams? {
        wmParams = WindowManager.LayoutParams()
        //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
        //因为部分type在部分系统中已经废弃，懒得看文档，下面是我亲测是兼容7.0和8.0系统的方法
        //wmParams.type = LayoutParams.TYPE_PHONE;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            wmParams?.type = WindowManager.LayoutParams.TYPE_PHONE;//
        } else {
            wmParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        //        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        //设置图片格式，效果为背景透明
        wmParams?.format = PixelFormat.RGBA_8888
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        //wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置可以显示在状态栏上
//        wmParams?.flags =
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
//                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        wmParams?.flags =//WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        // 设置窗体宽度和高度
        wmParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        wmParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        wmParams?.gravity = Gravity.CENTER_VERTICAL
        // 设置窗体显示的位置，否则在屏幕中心显示
        wmParams?.x = 50
        wmParams?.y = 50

        return wmParams
    }

    private fun sendKeyCode(keyCode: Int) {

        Thread() {
            try {
                Instrumentation().sendKeyDownUpSync(keyCode)
//                exeCmd("input keyevent $keyCode")
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }.start()
    }

    private fun exeCmd(command: String) {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("/system/bin/sh")
//            process = Runtime.getRuntime().exec("pm list package -3")
            os = DataOutputStream(process.outputStream)
            os.writeBytes(
                """
                $command

                """.trimIndent()
            )
            os.writeBytes("exit\n")
            os.flush()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuffer()
            var read: Int = 0
            val buffer = CharArray(1024)
            while (reader.read(buffer).also({ read = it }) > 0) {
                output.append(buffer, 0, read)
            }
            process.waitFor()
            reader.close()
            val content = output.toString()
            Log.d("mhylog", "content = $content")
        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            try {
                if (os != null) {
                    os.close()
                }
                process?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSuspend(x: Int, y: Int) {
        if (view != null) {
            //必须是当前显示的视图才给更新
            val layoutParams = view?.layoutParams as WindowManager.LayoutParams
            layoutParams.x += x
            layoutParams.y += y
            mWindowManager!!.updateViewLayout(view, layoutParams)
        }
    }

}