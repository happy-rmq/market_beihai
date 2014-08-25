package com.lenovo.market.activity;

import java.io.File;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.lenovo.market.R;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;

/**
 * Created by zhouyang on 14-2-21.
 */
@SuppressWarnings("deprecation")
public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private SurfaceView player_surfaceView;
    private ImageButton btn_play;
    private MediaPlayer mPlayer;
    private String filePath;// the path of video
    private SurfaceHolder holder;
    private int from;
    private boolean needSend;// Decide whether to display the Send button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_media_player);
        filePath = getIntent().getStringExtra("filePath");
        from = getIntent().getIntExtra("from", 0);
        needSend = getIntent().getBooleanExtra("needSend", false);
    }

    @Override
    protected void findViewById() {
        setTitleBarText("视频");
        setTitleBarLeftBtnText();
        if (needSend) {
            setTitleBarRightBtnText("发送");
        }
        player_surfaceView = (SurfaceView) findViewById(R.id.player_surfaceView);
        btn_play = (ImageButton) findViewById(R.id.btn_play);

        holder = player_surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setKeepScreenOn(true);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        if (needSend) {
            btn_right_.setOnClickListener(this);
        }
        btn_play.setOnClickListener(this);
        player_surfaceView.setOnClickListener(this);
        holder.addCallback(new SurfaceListener());
    }

    /**
     * Called when a view has been clicked.
     * 
     * @param v
     *            The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_play:
            playVideo();
            break;
        case R.id.btn_left:
            if (needSend) {
                deleteVideo();
            }
            finish();
            break;
        case R.id.btn_right:
            sendVideo();
            break;
        case R.id.player_surfaceView:
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                btn_play.setVisibility(View.VISIBLE);
                btn_play.setEnabled(true);
            }
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && needSend) {
            deleteVideo();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void deleteVideo() {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            log.d(filePath + "已经删除--------------");
        }
    }

    private void sendVideo() {
        Message message = null;
        switch (from) {
        case 1:
            message = ChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_FIVE);
            break;
        case 2:
            message = GroupChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_FIVE);
            break;
        case 3:
            message = HomePageFragment.handler.obtainMessage(MarketApp.HANDLERMESS_TEN);
            break;
        case 4:
            message = PublicChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_TEN);
            break;
        }
        if (message == null) {
            log.e("请设置from参数");
            return;
        }
        message.obj = filePath;
        message.sendToTarget();
        finish();
    }

    private void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.reset(); // clear recorder configuration
            mPlayer.release(); // release the recorder object
            mPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    private void playVideo() {
        mPlayer.start();
        btn_play.setVisibility(View.GONE);
        btn_play.setEnabled(false);
    }

    private void initMeidaPlayer() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Utils.showToast(VideoPlayerActivity.this, "视频文件路径错误");
                return;
            }
            mPlayer = new MediaPlayer();

            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mPlayer.setDataSource(filePath);
            // 把视频画面输出到SurfaceView
            mPlayer.setDisplay(player_surfaceView.getHolder());
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    btn_play.setVisibility(View.VISIBLE);
                    btn_play.setEnabled(true);
                }
            });
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btn_play.setVisibility(View.VISIBLE);
                    btn_play.setEnabled(true);
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    releaseMediaPlayer();
                    initMeidaPlayer();
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SurfaceListener implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initMeidaPlayer();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }
}
