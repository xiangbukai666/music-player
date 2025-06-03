package com.example.mymusic.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mymusic.Dao.mySQLite;
import com.example.mymusic.LoginActivity;
import com.example.mymusic.MainActivity;
import com.example.mymusic.R;

public class frag2 extends Fragment {
    private EditText editText;
    private Button btn_s,btn_d;
    private TextView textView;
    private mySQLite mSQLite;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag2_layout, container, false);
        editText=view.findViewById(R.id.editText);
        btn_s=view.findViewById(R.id.submit);
        textView=view.findViewById(R.id.user_name);
        btn_d=view.findViewById(R.id.delete_user);
        mSQLite=new mySQLite(getActivity());
        //显示用户名19220337
        if(getActivity() instanceof MainActivity){
            String name=((MainActivity)getActivity()).getUserName();
            if(name!=null){
                textView.setText(name);
            }
        }
        btn_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback=editText.getText().toString();
                Log.d("Feedback", feedback);
                Toast.makeText(getActivity(),"提交成功",Toast.LENGTH_SHORT).show();
            }
        });
        btn_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDelete();
            }
        });
        return view;
    }
    private void showDelete(){
        new AlertDialog.Builder(getActivity()).setTitle("注销确认").setMessage("您确定要注销吗？").setPositiveButton("确定",new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialogInterface,int which){
               String username=textView.getText().toString();
               mSQLite.deleteUser(username);
               Toast.makeText(getActivity(),"注销成功！",Toast.LENGTH_SHORT).show();
               Intent intent=new Intent(getActivity(), LoginActivity.class);
               startActivity(intent);
               getActivity().finish();
           }
        }).setNegativeButton("取消",null).show();
    }
}
