package com.example.mymusic.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mymusic.MusicPlayActivity;
import com.example.mymusic.R;

public class frag1 extends Fragment {
    private View view;
    //创建歌曲的String数组和歌手图片的int数组
    public String[] name={"逆战-张杰","一个人的远方-张杰","绽放-张杰","想见你想见你想见你-张杰","这就是爱-张杰","着魔-张杰","只要平凡-张杰 张碧晨","爱人啊-张杰","身骑白马-张杰","MySunshine-张杰"};
    //public static int[] icons={R.drawable.music_icon,R.drawable.music_icon,R.drawable.music_icon};
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.music_list,null);
        ListView listView=view.findViewById(R.id.playlist);
        //自定义适配器
        MyBaseAdapter adapter=new MyBaseAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建Intent对象，参数就是从frag1跳转到MusicActivity
                Intent intent=new Intent(frag1.this.getContext(), MusicPlayActivity.class);
                intent.putExtra("name",name[position]);
                intent.putExtra("position",String.valueOf(position));
                startActivity(intent);
            }
        });
        return view;
    }
    //这里是创建一个自定义适配器
    class MyBaseAdapter extends BaseAdapter{
        @Override
        public int getCount(){return  name.length;}
        @Override
        public Object getItem(int i){return name[i];}
        @Override
        public long getItemId(int i){return i;}
        @Override
        public View getView(int i ,View convertView, ViewGroup parent) {
            //绑定好VIew，然后绑定控件
            View view=View.inflate(frag1.this.getContext(),R.layout.music_item,null);
            TextView tv_name=view.findViewById(R.id.music_name);
            //ImageView iv=view.findViewById(R.id.iv);
            //设置控件显示的内容，就是获取的歌曲名和歌手图片
            tv_name.setText(name[i]);
            //iv.setImageResource(icons[i]);
            return view;
        }
    }

}

