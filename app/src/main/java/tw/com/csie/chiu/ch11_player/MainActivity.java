package tw.com.csie.chiu.ch11_player;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    Uri uri;
    TextView txvName,txvUri;
    boolean isVideo = false;

    Button btnPlay,btnStop;
    CheckBox ckbLoop;
    MediaPlayer mper;
    Toast tos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        txvName = (TextView)findViewById(R.id.txvName);
        txvUri = (TextView)findViewById(R.id.txvUri);
        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnStop = (Button)findViewById(R.id.btnStop);
        ckbLoop = (CheckBox)findViewById(R.id.ckbLoop);

        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fly);

        txvName.setText("fly.mp3");
        txvUri.setText("程式內的歌曲" + uri.toString());

        mper = new MediaPlayer();
        mper.setOnPreparedListener(this);
        mper.setOnErrorListener(this);
        mper.setOnCompletionListener(this);
        tos = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        prepareMusic();
    }

    void prepareMusic() {
        btnPlay.setText("播放");
        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);

        try {
            mper.reset();
            mper.setDataSource(this,uri);
            mper.setLooping(ckbLoop.isChecked());
            mper.prepareAsync();
        } catch(Exception e){
            tos.setText("指定音樂檔錯誤!"+e.toString());
            tos.show();
        }
    }


    public void onPick(View v){
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);

        if (v.getId() == R.id.btnPickAudio){
            it.setType("audio/*");
            startActivityForResult(it,100);
        }
        else{
            it.setType("video/*");
            startActivityForResult(it,101);
        }

    }

    protected void onActivityResult(int resquestCode,int resultCode,Intent data){
        super.onActivityResult(resquestCode,resultCode,data);

        if (resultCode == Activity.RESULT_OK){
            isVideo = (resquestCode == 101);

            uri = convertUri(data.getData());

            txvName.setText(uri.getLastPathSegment());

            txvUri.setText("檔案位置 : " + uri.getPath());

            if(!isVideo)prepareMusic();
        }
    }

    Uri convertUri(Uri uri){

        if(uri.toString().substring(0,7).equals("content")){

            String[] colName = {MediaStore.MediaColumns.DATA};

            Cursor cursor = getContentResolver().query(uri,colName,null,null,null);

            cursor.moveToFirst();
            uri = Uri.parse("file://" + cursor.getString(0));
            cursor.close();
        }
        return uri;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mper.seekTo(0);
        btnPlay.setText("播放");
        btnStop.setEnabled(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        tos.setText("發生錯誤，停止播放");
        tos.show();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        btnPlay.setEnabled(true);
    }


    public void onMpPlay(View v){
        if(isVideo){
            Intent it = new Intent(this,Video.class);

            it.putExtra("Uri",uri.toString());

            startActivity(it);
            return;
        }

        if (mper.isPlaying()){
            mper.pause();
            btnPlay.setText("繼續");
        }
        else{
         mper.start();
            btnPlay.setText("暫停");
            btnStop.setEnabled(true);
        }
    }

    public void onMpStop(View v){
        mper.pause();
        mper.seekTo(0);
        btnPlay.setText("播放");
        btnStop.setEnabled(false);
    }

    public void onMpLoop(View v){
        if (ckbLoop.isChecked())
            mper.setLooping(true);
        else
            mper.setLooping(false);
    }

    public void onMpBackward(View v){
        if(!btnPlay.isEnabled()) return;

        int len = mper.getDuration();
        int pos = mper.getCurrentPosition();
        pos -=10000;
        if (pos<0) pos = 0;
        mper.seekTo(pos);
        tos.setText("倒退10秒:"+pos/1000+"/"+len/1000);
        tos.show();
    }

    public void onMpForward(View v){
        if(!btnPlay.isEnabled()) return;

        int len = mper.getDuration();
        int pos = mper.getCurrentPosition();
        pos +=10000;
        if (pos>len) pos = len;
        mper.seekTo(pos);
        tos.setText("前進10秒:"+pos/1000+"/"+len/1000);
        tos.show();
    }

    @Override
    protected void onPause(){
        super.onPause();

        if (mper.isPlaying()){
            btnPlay.setText("繼續");
            mper.pause();
        }
    }

    @Override
    protected void onDestroy(){
        mper.release();
        super.onDestroy();
    }

}