package com.example.mymusic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mymusic.Bean.User;
import com.example.mymusic.Dao.mySQLite;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private Button log;
    private TextView register;
    private mySQLite mSQLite;
    private EditText username;
    private EditText userpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        log=findViewById(R.id.login);
        register=findViewById(R.id.l_register);
        username = findViewById(R.id.l_username);
        userpassword = findViewById(R.id.l_password);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转页面
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //进行登录数据库验证
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=username.getText().toString().trim();
                String password=userpassword.getText().toString().trim();
                if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(password)) {
                    ArrayList<User> data=mSQLite.getAllDATA();
                    boolean state=false;
                    for (int i=0;i<data.size();i++) {
                        User userdata= data.get(i);   //可存储账号数量
                        if (name.equals(userdata.getName())&&password.equals(userdata.getPassword())){
                            state=true;
                            break;
                        } else {
                            state=false;
                        }
                    }
                    if (state){
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("username",name);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mSQLite=new mySQLite(LoginActivity.this);

    }
}