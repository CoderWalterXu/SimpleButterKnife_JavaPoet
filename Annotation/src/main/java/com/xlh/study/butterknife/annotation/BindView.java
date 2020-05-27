package com.xlh.study.butterknife.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD) // 注解作用在属性上
@Retention(RetentionPolicy.CLASS) // 编译期
public @interface BindView {

    // 返回R.id.xx
    int value();

}
