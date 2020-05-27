package com.xlh.study.butterknife.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Watler Xu
 * time:2020/5/26
 * description:
 * version:0.0.1
 */
@Target(ElementType.METHOD) // 注解作用在方法上
@Retention(RetentionPolicy.CLASS) // 编译期
public @interface OnClick {
    int value();
}
