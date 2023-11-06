package com.example.myfirstxposedmodule;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import java.lang.reflect.Field;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.*;


public class MyModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("xxx Loaded app: " + lpparam.packageName);

        // 过滤不必要的应用
        if (!lpparam.packageName.equals("me.kyuubiran.xposedapp")) return;
        // 执行Hook
        hook(lpparam);
    }

    private void hook(XC_LoadPackage.LoadPackageParam lpparam) {
    // 第二种方式 填Class
    // 首先你得加载它的类 我们使用XposedHelpers.findClass即可 参数有两个 一个是类名 一个是类加载器
    Class<?> clazz = XposedHelpers.findClass("me.kyuubiran.xposedapp.MainActivity", lpparam.classLoader);
    XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param){
            XposedBridge.log("xxx hooked: " + lpparam.packageName);
            // 由于我们需要在Activity创建之后再弹出Toast，所以我们Hook方法执行之后
            Toast.makeText((Activity) param.thisObject, "模块加载成功！", Toast.LENGTH_SHORT).show();
        }
    });

    XposedHelpers.findAndHookMethod("me.kyuubiran.xposedapp.Guess",
            lpparam.classLoader,
            "isDraw",
            int.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    try {                        
                        // 首先 我们要拿到他的字段
                        Field fAnswer = param.thisObject.getClass().getDeclaredField("answer");
                        // 并且让它可访问 否则会报非法访问的错误
                        fAnswer.setAccessible(true);
                        int win = 0;
                        // 根据猜拳应用的逻辑 0是石头 1是剪刀 2是布
                        switch ((int) param.args[0]) {  // 首先我们拿到方法的第一个参数 他是int类型
                            case 0:         // 石头->剪刀
                                win = 1;
                                break;
                            case 1:         // 剪刀->布
                                win = 2;
                                break;
                            case 2:         // 布->石头
                                win = 0;
                                break;
                        }
                        // 最后设置answer的值让对手根据我们的出拳来“演戏”
                        fAnswer.set(param.thisObject, win);
                    } catch (Exception ignored) {
                    }
                }
            });

    }
}