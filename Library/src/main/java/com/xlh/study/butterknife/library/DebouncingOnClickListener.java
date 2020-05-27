package com.xlh.study.butterknife.library;

import android.view.View;

/**
 * @author: Watler Xu
 * time:2020/5/26
 * description: 点击监听接口，实现类（抽象类 + 抽象方法）
 * version:0.0.1
 */
public abstract class DebouncingOnClickListener implements View.OnClickListener{
    @Override
    public void onClick(View v){
        // 调用抽象方法
        doClick(v);
    }

    protected abstract void doClick(View v);
}
