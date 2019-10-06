package com.project.safewheels;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.project.safewheels.Tools.SliderAdaptor;

public class Instruction extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdaptor sliderAdaptor;
    private TextView[] mDots;
    private int mCurrentPage;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        sliderAdaptor = new SliderAdaptor(this);
        mSlideViewPager.setAdapter(sliderAdaptor);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[5];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.places_text_white_alpha_26));
            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.quantum_white_100));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
