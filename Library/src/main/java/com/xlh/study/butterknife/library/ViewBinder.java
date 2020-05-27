package com.xlh.study.butterknife.library;

/**
 * @author: Watler Xu
 * time:2020/5/26
 * description: 接口绑定类（所有注解处理器生的类，都需要实现该接口，= 接口实现类）
 * <T> 被绑定者的类型，如：MainActivity
 * version:0.0.1
 */
public interface ViewBinder<T> {

    /**
     * 绑定方法
     *
     * @param target 被绑定者，如：MainActivity
     */
    void bind(T target);
}

