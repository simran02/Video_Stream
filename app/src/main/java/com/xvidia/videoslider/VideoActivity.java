package com.xvidia.videoslider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_VIDEO = 0;

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final VideoView mVideoView = (VideoView) findViewById(R.id.videoView1);

    }





}
