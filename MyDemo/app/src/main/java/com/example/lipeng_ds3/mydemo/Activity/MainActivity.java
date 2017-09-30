package com.example.lipeng_ds3.mydemo.Activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lipeng_ds3.mydemo.Progress.CircleProgress;
import com.example.lipeng_ds3.mydemo.R;

import java.util.Random;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int[] COLORS = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
    private Button mBtnReset;
    private CircleProgress mCircleProgress1, mCircleProgress2, mCircleProgress3;
    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mBtnReset = (Button) findViewById(R.id.reset);
        mCircleProgress1 = (CircleProgress) findViewById(R.id.circle_progress_bar1);
        mCircleProgress2 = (CircleProgress) findViewById(R.id.circle_progress_bar2);
        mCircleProgress3 = (CircleProgress) findViewById(R.id.circle_progress_bar3);

        mBtnReset.setOnClickListener(this);
        mCircleProgress1.setOnClickListener(this);
        mCircleProgress2.setOnClickListener(this);
        mCircleProgress3.setOnClickListener(this);
        mRandom = new Random();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.reset:
                mCircleProgress1.reset();
                mCircleProgress2.reset();
                mCircleProgress3.reset();
                break;
            case R.id.circle_progress_bar1:
                mCircleProgress1.setValue(mRandom.nextInt((int) mCircleProgress1.getMaxValue()));
                break;
            case R.id.circle_progress_bar2:
                mCircleProgress2.setValue(mRandom.nextFloat() * mCircleProgress2.getMaxValue());
                break;
            case R.id.circle_progress_bar3:
                mCircleProgress3.setGradientColors(COLORS);
                mCircleProgress3.setValue(mRandom.nextFloat() * mCircleProgress3.getMaxValue());
                break;
        }
    }
}
