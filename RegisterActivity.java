package com.example.mymusic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mymusic.Dao.mySQLite;

public class RegisterActivity extends AppCompatActivity {
    private mySQLite mSQLite;
    private EditText username;
    private EditText userpassword;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.r_username);
        userpassword =findViewById( R.id.r_password);
        register=findViewById(R.id.register);
        findViewById(R.id.mytoolbar).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
        //设置注册数据库逻辑
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=username.getText().toString().trim();
                String password=userpassword.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"用户名不可为空！",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this,"密码不可为空！",Toast.LENGTH_SHORT).show();
                }else{
                    mSQLite.add(name,password);
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSQLite=new mySQLite(RegisterActivity.this);
    }
}