package tw.com.csie.chiu.ch11_player;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class Video extends AppCompatActivity {

    VideoView vdv;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent it = getIntent();
        Uri uri = Uri.parse(it.getStringExtra("uri"));

        if (savedInstanceState!= null)
            pos = savedInstanceState.getInt("pos",0);
        vdv = (VideoView)findViewById(R.id.vdv);

        MediaController mediaCtrl = new MediaController(this);

        vdv.setMediaController(mediaCtrl);
        vdv.setVideoURI(uri);
    }

    @Override
    protected void onResume(){
        super.onResume();
        pos = vdv.getCurrentPosition();
        vdv.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pos = vdv.getCurrentPosition();
        vdv.stopPlayback();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("pos",pos);
    }

}
