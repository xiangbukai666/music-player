package com.example.mymusic;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.Integer.parseInt;

import com.example.mymusic.Fragment.frag1;
import com.example.mymusic.Service.MusicService;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener {
    static NotificationManager mNotificationManager;
    static NotificationCompat.Builder mNotificationBuilder;
    Context context;
    //进度条
    private static SeekBar seekBar;
    private static TextView currentTime, totalTime, songName;
    private String name;
    private MusicService musicService;
    //设置动画
    private ObjectAnimator animator;
    private MusicService.MusicControl musicControl;
    private Intent intent1, intent2;
    private MyServiceConn conn;
    //记录服务是否被解绑192203333
    private boolean isUnbind = false;
    private int currentSongIndex;
    private ImageView btnPlay;
    private ImageView btnStatus;
    private int playImageIndex = 0;
    private int statusImageIndex = 0;
    private int[] playImages = {R.drawable.zantingbofang, R.drawable.jixubofang};
    private int[] statusImages = {R.drawable.shunxu, R.drawable.suiji, R.drawable.xunhuan,};
    static String lastUpdatedSongName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        // 返回首页
        findViewById(R.id.mytoolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicPlayActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // 获取从frag1歌曲列表传来的信息19220333
        intent1 = getIntent();
        init();
        btnPlay = findViewById(R.id.btn_play);
        btnStatus = findViewById(R.id.btn_status);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查当前图片，决定播放还是暂停
                if (btnPlay.getDrawable().getConstantState().equals(getResources().getDrawable(playImages[0]).getConstantState())) {
                    // 如果是暂停图标，那么开始播放
                    String position = intent1.getStringExtra("position");
                    int i = parseInt(position);
                    musicControl.play(i);
                    btnPlay.setImageResource(playImages[1]); // 切换到播放图标
                } else {
                    // 如果是播放图标，那么暂停播放19220333
                    musicControl.pausePlay();
                    btnPlay.setImageResource(playImages[0]); // 切换到暂停图标
                }
                animator.start();
            }
        });
        btnStatus.setOnClickListener(view -> {
            statusImageIndex = (statusImageIndex + 1) % statusImages.length;
            btnStatus.setImageResource(statusImages[statusImageIndex]);
            // 更新播放模式
            musicControl.setPlayMode(statusImageIndex);
        });
        conn = new MyServiceConn();
        bindService(intent2, conn, BIND_AUTO_CREATE);
        context = this;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(context, "default_channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Default notification channel";
            int importance = NotificationManager.IMPORTANCE_LOW; // 设置为低重要性，不发出声音
            NotificationChannel channel = new NotificationChannel("default_channel", name, importance);
            channel.setDescription(description);
            channel.enableLights(false); // 不使用LED灯
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE); // 锁屏不显示
            mNotificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(context, MusicPlayActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        //配置通知内容
        mNotificationBuilder = new NotificationCompat.Builder(context, "default_channel")
                .setContentTitle(songName.getText().toString())
                .setContentText("正在播放...")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.music)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        //确保应用具有发送通知的权限19220333
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return; // 等待权限请求结果后再继续执行发送通知的代码
            }
        }
        // 显示通知
        mNotificationManager.notify(1, mNotificationBuilder.build());
    }
    // 更新通知内容为当前歌曲信息
    private static void updateNotification() {
        String currentSongName = songName.getText().toString();

        if (!currentSongName.equals(lastUpdatedSongName)) { // 检查歌曲名称是否变化
            lastUpdatedSongName = currentSongName; // 更新最后更新的歌曲名称
            mNotificationBuilder.setContentTitle(currentSongName)
                    .setContentText("正在播放...");
            mNotificationManager.notify(1, mNotificationBuilder.build()); // 发送更新后的通知
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，发送通知
                mNotificationManager.notify(1, mNotificationBuilder.build());
            } else {
                // 权限被拒绝
                mNotificationManager.cancel(1);
            }
        }
    }
    private void init() {
        currentTime = (TextView) findViewById(R.id.tv_progress);
        totalTime = (TextView) findViewById(R.id.tv_total);
        seekBar = (SeekBar) findViewById(R.id.time_seekbar); // 控制进度条
        songName = (TextView) findViewById(R.id.song_name); // 显示播放的歌名
        findViewById(R.id.btn_play).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_prev).setOnClickListener(this);
        findViewById(R.id.btn_status).setOnClickListener(this);
        name = intent1.getStringExtra("name"); // 设置歌曲名称
        songName.setText(name);
        //跳转到Service，方便进行音乐的播放
        intent2 = new Intent(this, MusicService.class);
        conn = new MyServiceConn(); // 创建服务连接对象
        bindService(intent2, conn, BIND_AUTO_CREATE); // 绑定服务
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == seekBar.getMax()) {
                    animator.pause(); // 停止播放动画
                }
            }

            @Override
            // 滑动条开始滑动时调用
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            // 滑动条停止滑动时调用
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 根据拖动的进度改变音乐播放进度
                int progress = seekBar.getProgress();
                musicControl.seekTo(progress); // 改变播放进度
            }
        });
        // 声明并绑定音乐播放器的iv_music控件
        ImageView iv_music = (ImageView) findViewById(R.id.music_pic);
        String position = intent1.getStringExtra("position");
        // 控件的旋转动画
        animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
    }

    //handler实现线程间的通信19220333
    public static Handler handler = new Handler() { // 创建消息处理器对象
        // 在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            String currentSongName = bundle.getString("currentSongName");
            // 对进度条进行设置
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            songName.setText(currentSongName);
            // 设置时间
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinute = null;
            String strSecond = null;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + "";
            }
            totalTime.setText(strMinute + ":" + strSecond);
            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + " ";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + " ";
            }
            currentTime.setText(strMinute + ":" + strSecond);
            updateNotification(); // 更新通知
        }

    };

    // 实现连接服务
    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicControl = (MusicService.MusicControl) service;
            String initialSongName = musicControl.getCurrentSongName();
            songName.setText(initialSongName);
            updateNotification();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    // 判断服务是否被解绑
    private void unbind(boolean isUnbind) {
        if (!isUnbind) {
            musicControl.pausePlay(); // 音乐暂停播放
            unbindService(conn); // 解绑服务
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_prev) {
            musicControl.pre();
        } else if (v.getId() == R.id.btn_next) {
            musicControl.next();
        }
        // 更新通知内容为当前歌曲信息
        updateNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbind(isUnbind); // 解绑服务
    }
}
