package com.xlh.study.butterknife.library;

import android.app.Activity;

/**
 * @author: Watler Xu
 * time:2020/5/26
 * description:
 * version:0.0.1
 */
public class WxButterKnife {

    public static void bind(Activity activity) {
        // 拼接类名，如：MainActivity$ViewBinder
        String className = activity.getClass().getName() + "$ViewBinder";

        try {
            // 加载上述拼接类（可能apt生成失败，这里会抛出ClassNotFountException异常）
            Class<?> viewBindClass = Class.forName(className);
            // 接口 = 接口实现类
            ViewBinder viewBinder = (ViewBinder) viewBindClass.newInstance();
            // 调用接口方法
            viewBinder.bind(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
