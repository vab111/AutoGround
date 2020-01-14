package com.example.autoground;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Farmingtool extends Fragment {
    public GridView item_pick;
    private List Avatarlist;
    private ArrayAdapter avatarAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.farmingtool, container, false);
        item_pick = (GridView) view.findViewById(R.id.farming);
        initdata();//初始化英雄头像，名字
        avatarAdapter = new ArrayAdapter(getContext(),R.layout.farmtype,Avatarlist){
            @Override
            public int getCount() {
                return Avatarlist.size()+1;
            }
            @Override
            public Object getItem(int position) {
                return Avatarlist.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
                //TODO 添加历史记录条目
                if (position < Avatarlist.size()) {
                    FarmTool user = (FarmTool) Avatarlist.get(position);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.farmtype, null);
                    TextView nameText = (TextView) view.findViewById(R.id.juli);
                    TextView ageText = (TextView) view.findViewById(R.id.type);
                    nameText.setText(String.format("%.2f", (float)user.NJWidth/100));
                    ageText.setText(user.NJType);
                    return view;
                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.newfarmtype, null);

                    return view;
                }
            }
        };
        item_pick.setAdapter(avatarAdapter);
        //给GridView中每个英雄设置监听器以便跳转到HeroDetailActivity
        item_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id<Avatarlist.size()) {
                    FarmTool avatar = (FarmTool) Avatarlist.get(position);
                }
                Intent intent = new Intent(Farmingtool.this.getActivity(), FarmtoolDetail.class);
//                intent.putExtra("Avatar", Avatarlist.get(position));
//                intent.putExtra("file_name", Avatarlist.get(position).getDetailFileName());
                startActivity(intent);
            }
        });
        return view;
    }

    /**    初始化英雄头像，名字的方法*/
    private void initdata() {
        Avatarlist = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/Farmtool.json");

            String result = "";
            try {
                FileInputStream f = new FileInputStream(fs);
                BufferedReader bis = new BufferedReader(new InputStreamReader(f));
                String line = "";
                while ((line = bis.readLine()) != null) {
                    result += line;
                }
                bis.close();
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.length()>0) {
                Gson gson = new Gson();
                Avatarlist = gson.fromJson(result, new TypeToken<List<FarmTool>>() {
                }.getType());
            }


    }

}
