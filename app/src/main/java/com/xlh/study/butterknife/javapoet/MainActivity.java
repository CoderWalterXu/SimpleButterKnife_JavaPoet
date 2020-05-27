package com.xlh.study.butterknife.javapoet;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xlh.study.butterknife.annotation.BindView;
import com.xlh.study.butterknife.annotation.OnClick;
import com.xlh.study.butterknife.library.WxButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.btn)
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WxButterKnife.bind(this);
    }

    @OnClick(R.id.tv)
    public void tvClick(View view) {
        Toast.makeText(this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn)
    public void btnClick(View view) {
        Toast.makeText(this, btn.getText().toString(), Toast.LENGTH_SHORT).show();
    }

}
