package com.sarker.backbenchersextended;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.sarker.backbenchersextended.adapter.PageAdapter;
import com.sarker.backbenchersextended.dayfragment.Monday;
import com.sarker.backbenchersextended.dayfragment.Saturday;
import com.sarker.backbenchersextended.dayfragment.Sunday;
import com.sarker.backbenchersextended.dayfragment.Thursday;
import com.sarker.backbenchersextended.dayfragment.Tuesday;
import com.sarker.backbenchersextended.dayfragment.Wednesday;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Routine extends AppCompatActivity{


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem saturday,sunday,monday,tuesday,wednesday,thursday;
    public PagerAdapter pagerAdapter;

    private Calendar calendar = Calendar.getInstance();
    private int dayName;
    private String day;
    private ImageView back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_routine);


        Intent i = getIntent();
        day = i.getStringExtra("day");


        dayName = calendar.get(Calendar.DAY_OF_WEEK);



        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        back = findViewById(R.id.ic_back);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

    }

    private void setUpViewPager(ViewPager viewPager) {

        PageAdapter viewPagerAdapter = new PageAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Saturday(), "SATURDAY");
        viewPagerAdapter.addFragment(new Sunday(), "SUNDAY");
        viewPagerAdapter.addFragment(new Monday(), "MONDAY");
        viewPagerAdapter.addFragment(new Tuesday(), "TUESDAY");
        viewPagerAdapter.addFragment(new Wednesday(), "WEDNESDAY");
        viewPagerAdapter.addFragment(new Thursday(), "THURSDAY");
        viewPager.setAdapter(viewPagerAdapter);


        if(day!=null) {

            switch (day) {

                case "Saturday":
                    viewPager.setCurrentItem(0);
                    break;

                case "Sunday":
                    viewPager.setCurrentItem(1);
                    break;

                case "Monday":
                    viewPager.setCurrentItem(2);
                    break;

                case "Tuesday":
                    viewPager.setCurrentItem(3);
                    break;

                case "Wednesday":
                    viewPager.setCurrentItem(4);
                    break;
                case "Thursday":
                    viewPager.setCurrentItem(5);
                    break;

                default:
                    viewPager.setCurrentItem(0);
                    return;
            }

        }else {


            switch (dayName){

                case Calendar.SATURDAY:
                    viewPager.setCurrentItem(0);
                    break;

                case Calendar.SUNDAY:
                    viewPager.setCurrentItem(1);
                    break;

                case Calendar.MONDAY:
                    viewPager.setCurrentItem(2);
                    break;

                case Calendar.TUESDAY:
                    viewPager.setCurrentItem(3);
                    break;

                case Calendar.WEDNESDAY:
                    viewPager.setCurrentItem(4);
                    break;

                case Calendar.THURSDAY:
                    viewPager.setCurrentItem(5);
                    break;

                default:
                    viewPager.setCurrentItem(0);

            }

        }

    }
}
