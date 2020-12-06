# DL10RoundMenuView
Android 圆形遥控器按钮盘 带中间按钮
在此项目基础上添加的 遥控模拟按键事件 仅用于TV遥控按键事件按调试
[![fork来自DL10RoundMenuView](https://jitpack.io/v/D10NGYANG/DL10RoundMenuView.svg)](https://jitpack.io/#D10NGYANG/DL10RoundMenuView)

链接：https://blog.csdn.net/sinat_38184748/article/details/89182372

# 效果图
![](/img/sc_1.png)
![](/img/sc_2.png)
![](/img/sc_3.png)
![](/img/sc_4.png)
![](/img/sc_5.png)
# 使用说明
## 注意说明
**版本使用androidx+kotlin代码**
## 添加依赖
Step 1. Add the JitPack repository to your build file 

```java
	allprojects {
		repositories {
			jcenter()
		}
	}
```
Step 2. Add the dependency

```java
	dependencies {
	        implementation 'com.mhy.tv:tvcontrol:1.0.0'
	}
```
## 在布局中使用
```java
	 override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            //检查是否已经授予权限，大于6.0的系统适用，小于6.0系统默认打开，无需理会
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                //没有权限，需要申请权限，因为是打开一个授权页面，所以拿不到返回状态的，所以建议是在onResume方法中从新执行一次校验
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + getPackageName())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(intent,100)
            } else {
                TVControl.initTV(applicationContext)
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
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(intent,100)
            }
        }
    }
```

# 混淆规则
```kotlin
-keep class com.dlong.rep.dlroundmenuview.** {*;}
-keep class com.mhy.tv.remotecontrol.** {*;}
-dontwarn com.dlong.rep.dlroundmenuview.**
```
