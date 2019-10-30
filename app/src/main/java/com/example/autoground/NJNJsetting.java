package com.example.autoground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class NJNJsetting extends BaseActivity {
    private Fragment clszFragment;
    private Fragment njszFragment;
    private Fragment txszFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_njnjsetting);
        toolbar = findViewById(R.id.njnjbar);
        setToolbar();
        initFragment1();
    }
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);//设计隐藏标题

        //设置显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish();
                finish();
            }
        });
    }




        //显示第一个fragment

        private void initFragment1(){

            //开启事务，fragment的控制是由事务来实现的

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



            //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个

            if(clszFragment == null){

                clszFragment = new clFragment();

                transaction.add(R.id.njsetting_frame, clszFragment);

            }

            //隐藏所有fragment

            hideFragment(transaction);

            //显示需要显示的fragment

            transaction.show(clszFragment);



            //第二种方式(replace)，初始化fragment

//        if(f1 == null){

//            f1 = new MyFragment("消息");

//        }

//        transaction.replace(R.id.main_frame_layout, f1);



            //提交事务

            transaction.commit();

        }



        //显示第二个fragment

        private void initFragment2(){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



            if(njszFragment == null){

                njszFragment = new njFragment();

                transaction.add(R.id.njsetting_frame,njszFragment);

            }

            hideFragment(transaction);

            transaction.show(njszFragment);



//        if(f2 == null) {

//            f2 = new MyFragment("联系人");

//        }

//        transaction.replace(R.id.main_frame_layout, f2);



            transaction.commit();

        }



        //显示第三个fragment

        private void initFragment3(){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();



            if(txszFragment == null){

                txszFragment = new txFragment();

                transaction.add(R.id.njsetting_frame,txszFragment);

            }

            hideFragment(transaction);

            transaction.show(txszFragment);



//        if(f3 == null) {

//            f3 = new MyFragment("动态");

//        }

//        transaction.replace(R.id.main_frame_layout, f3);



            transaction.commit();

        }



        //隐藏所有的fragment

        private void hideFragment(FragmentTransaction transaction){

            if(clszFragment != null){

                transaction.hide(clszFragment);

            }

            if(njszFragment != null){

                transaction.hide(njszFragment);

            }

            if(txszFragment != null){

                transaction.hide(txszFragment);
            }

        }


    public void clSetting(View view) {
        initFragment1();
    }

    public void njSetting(View view) {
        initFragment2();
    }

    public void txSetting(View view) {
        initFragment3();
    }
}
