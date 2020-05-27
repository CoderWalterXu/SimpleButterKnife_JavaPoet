package com.xlh.study.butterknife.compiler.utils;

/**
 * @author: Watler Xu
 * time:2020/5/26
 * description: 常量类
 * version:0.0.1
 */
public class Constants {

    // 注解处理器中支持的注解类型
    public static final String BINDVIEW_ANNOTATION_TYPES = "com.xlh.study.butterknife.annotation.BindView";
    public static final String ONCLICK_ANNOTATION_TYPES = "com.xlh.study.butterknife.annotation.OnClick";


    // 布局、控件绑定实现接口
    public static final String VIEWBINDER = "com.xlh.study.butterknife.library.ViewBinder";

    public static final String CLICKLISTENER = "com.xlh.study.butterknife.library.DebouncingOnClickListener";

    public static final String VIEW = "android.view.View";

    // bind方法名
    public static final String BIND_METHOD_NAME = "bind";

    // bind方法的参数名target
    public static final String TARGET_PARAMETER_NAME = "target";

}
