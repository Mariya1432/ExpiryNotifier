package com.example.expirynotifier;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.R;
import android.widget.RelativeLayout;

public class ProgressHandler {

    private Context context;
    private ProgressBar progressBar;

    public ProgressHandler(Context context){
        this.context = context;
        init();
    }

    private void init() {
        ViewGroup viewGroup = (ViewGroup) ((Activity) context).findViewById(R.id.content).getRootView();
        progressBar = new ProgressBar(context,null, R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        RelativeLayout rl =new  RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);
        rl.addView(progressBar);
        viewGroup.addView(rl, params);
        hide();
    }

    private ProgressHandler(){}


    public void show() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        progressBar.setVisibility(View.INVISIBLE);
    }

}

