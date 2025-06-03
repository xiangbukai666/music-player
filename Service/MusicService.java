package com.example.mymusic.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import com.example.mymusic.MusicPlayActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public String[] names={"逆战-张杰","一个人的远方-张杰","绽放-张杰","想见你想见你想见你-张杰","这就是爱-张杰","着魔-张杰","只要平凡-张杰 张碧晨","爱人啊-张杰","身骑白马-张杰","MySunshine-张杰"};
    private MediaPlayer player;//加载播放的音乐
    private Timer timer;//计时器引用
    private int currentSongIndex;
    private int status = 0;
    public MusicService() {}
    @Override
    public  IBinder onBind(Intent intent){
        // TODO: Return the communication channel to the service.
        return new MusicControl();
    }
    @Override
    public void onCreate(){
        super.onCreate();
        //创建音乐播放器对象19220333
        player=new MediaPlayer();
    }
    public void addTimer(){//添加计时器用于设置音乐播放器中的播放进度条
        if(timer==null){
            timer=new Timer();//创建计时器对象
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    if (player==null) return;
                    int duration=player.getDuration();//获取歌曲总时长
                    int currentPosition=player.getCurrentPosition();//获取播放进度
                    String currentSongName = names[currentSongIndex];
                    Message msg= MusicPlayActivity.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    bundle.putString("currentSongName", currentSongName);
                    msg.setData(bundle);//再将bundle封装到msg消息对象中
                    MusicPlayActivity.handler.sendMessage(msg);//最后将消息发送到主线程的消息队列
                }
            };
            //0.5syi一次更新音乐播放的进度19220337
            timer.schedule(task,5,500);
        }
    }
    //Binder是一种跨进程的通信方式
    public class MusicControl extends Binder{
        public void play(int i) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "music" + i);
            try {
                player.reset();
                player = MediaPlayer.create(MusicService.this, uri);
                // 设置OnCompletionListener
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 当前音乐播放完成，播放下一首
                        next();
                    }
                });
                player.start();
                addTimer();
                currentSongIndex = i;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public int getCurrentSongIndex() {
            return currentSongIndex;
        }
        public String getCurrentSongName() {
            return names[currentSongIndex];
        }
        public void pre() {
            switch (status) {
                case 0: //顺序播放
                    currentSongIndex--;
                    if (currentSongIndex < 0) currentSongIndex = names.length - 1;
                    break;
                case 1: //随机播放19220333
                    Random random = new Random();
                    currentSongIndex = random.nextInt(names.length);
                    break;
                case 2: //单曲循环
                    player.start();
                    break;
            }
            play(currentSongIndex);
        }
        public void next() {
            switch (status) {
                case 0: // 顺序播放
                    currentSongIndex++;
                    if (currentSongIndex >= names.length) currentSongIndex = 0;
                    break;
                case 1: // 随机播放
                    Random random = new Random();
                    currentSongIndex = random.nextInt(names.length);
                    break;
                case 2: // 单曲循环
                    player.start();
                    break;
            }
            play(currentSongIndex);
        }
        public void pausePlay(){
            player.pause();//暂停播放音乐
        }
        public void continuePlay(){
            player.start();//继续播放音乐
        }
        public void seekTo(int progress){
            player.seekTo(progress);//设置音乐的播放位置
        }
        public void setPlayMode(int mode) {
            status = mode;
        }
        public int getPlayMode() {
            return status;
        }
    }
    @Override
    public void onDestroy(){//销毁多媒体播放器19220333
        super.onDestroy();
        if (player != null) {
            if (player.isPlaying()) player.stop();
            player.release();
            player = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}




