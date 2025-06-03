//package com.example.mymusic;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import android.view.View;
//import android.widget.TextView;
//import com.example.mymusic.Fragment.frag1;
//import com.example.mymusic.Fragment.frag2;
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    //创建需要用到的控件的变量
//    private TextView ml,pc;//对应歌单与个人中心
//    private FragmentManager fm;
//    private FragmentTransaction ft;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        //绑定控件
//        ml=(TextView)findViewById(R.id.menu1);
//        pc=(TextView)findViewById(R.id.menu2);
//        //设置监听器
//        ml.setOnClickListener(this);
//        pc.setOnClickListener(this);
//        fm=getSupportFragmentManager();
//        ft=fm.beginTransaction();
//        //控制页面的变化
//        ft.replace(R.id.content,new frag1());
//        ft.commit();
//    }
//    @Override
//    //控件的点击事件
//    public void onClick(View v){
//        ft=fm.beginTransaction();//切换不同的布局19220337
//        if (v.getId()==R.id.menu1) {
//            ft.replace(R.id.content,new frag1());
//        } else if (v.getId()==R.id.menu2) {
//            ft.replace(R.id.content,new frag2());
//        }
//        ft.commit();
//    }
//}
//
package com.example.mymusic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mymusic.Fragment.frag1;
import com.example.mymusic.Fragment.frag2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private BottomNavigationView bottomNavigationView;//底部导航栏
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent nameintent=getIntent();
        if(nameintent!=null){
            userName=nameintent.getStringExtra("username");//获取用户名
        }
        ImageView mp=findViewById(R.id.music_play);
        mp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MusicPlayActivity.class);
                startActivity(intent);
                finish();
            }
            });
        bottomNavigationView = findViewById(R.id.user_bottom_menu);
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        showFragment(new frag1());
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            Fragment selectedFragment=null; // 初始化为 null
            switch (item.getItemId()){
                case R.id.menu1:
                    selectedFragment=new frag1(); // 选择 frag1
                    break;
                case R.id.menu2:
                    selectedFragment=new frag2(); // 选择 frag2
                    break;
            }
            //点击时进行替换
            if (selectedFragment!=null){
                FragmentManager fm=getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                ft.replace(R.id.content, selectedFragment);
                ft.commit();
            }
            return true;
        });
    }
    private void showFragment(Fragment fragment) {
        ft = fm.beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }
    public String getUserName(){
        return userName;
    }
}
