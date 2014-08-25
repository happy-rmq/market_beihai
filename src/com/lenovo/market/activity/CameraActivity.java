package com.lenovo.market.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.CameraPreview;

@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CameraActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private FrameLayout frame_preview;
    private boolean isRecording = false;
    private ImageButton captureButton;
    private TextView tv_time;
    private Timer timer;
    private int seconds;
    private Handler mHandler;
    private String filePath;// the path of video
    private int from;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_preview);

        from = getIntent().getIntExtra("from", 0);
        initCameraPreview();
        mHandler = new CameraHandler();
    }

    private void initCameraPreview() {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        frame_preview = (FrameLayout) findViewById(R.id.camera_preview);
        frame_preview.addView(mPreview, 0);

        // Add a listener to the Capture button
        captureButton = (ImageButton) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(this);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Check if this device has a camera
     */
    // private boolean checkCameraHardware(Context context) {
    // if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
    // // this device has a camera
    // return true;
    // } else {
    // // no camera on this device
    // return false;
    // }
    // }

    private boolean prepareVideoRecorder() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = getCameraInstance();

        Camera.Parameters parameters = mCamera.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);// 针对android2.2和之前的版本
            parameters.setRotation(90);// 去掉android2.0和之前的版本
        } else {
            parameters.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
        mCamera.setParameters(parameters);

        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            // Step 3: Set output format and encoding (for versions prior to API Level 8)
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        } else {
            // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            if (profile != null) {
                mMediaRecorder.setProfile(profile);
            } else {
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            }

        }

        // Step 4: Set output file
        filePath = getOutputMediaFile(MarketApp.HANDLERMESS_TWO).toString();
        mMediaRecorder.setOutputFile(filePath);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mMediaRecorder.setOrientationHint(90);
        }

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder(); // if you are using MediaRecorder, release it first
        releaseCamera(); // release the camera immediately on pause event
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset(); // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyCameraApp");
        File mediaStorageDir = new File(Utils.getCacheDir(this, "video"));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("video", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MarketApp.HANDLERMESS_ONE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MarketApp.HANDLERMESS_TWO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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
        case R.id.button_capture:
            handleRecord();
            break;
        }
    }

    private void handleRecord() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop(); // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock(); // take camera access back from MediaRecorder

            // stop recode time
            timer.cancel();

            // inform the user that recording has stopped
            captureButton.setBackgroundResource(R.drawable.video_recorder_start_btn);
            isRecording = false;

            releaseCamera();
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("filePath", filePath);
            intent.putExtra("from", from);
            intent.putExtra("needSend", true);
            startActivity(intent);
            finish();
        } else {
            // initialize video camera
            new MediaPrepareTask().execute(null, null, null);
        }
    }

    class CameraHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MarketApp.HANDLERMESS_THREE:
                seconds++;
                tv_time.setText(seconds + "");
                if (seconds == 30) {
                    handleRecord();
                    Log.e("TimerTask", "30s is over!!!!!!");
                    timer.cancel();
                    seconds = 0;
                } else if (seconds == 20) {
                    Utils.showToast(CameraActivity.this, "还可以再录10秒");
                }
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                // prepare didn't work
                CameraActivity.this.finish();
            } else {
                // start recording
                captureButton.setBackgroundResource(R.drawable.video_recorder_stop_btn);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = mHandler.obtainMessage(MarketApp.HANDLERMESS_THREE);
                        message.sendToTarget();
                    }
                }, 1000, 1000);
            }
        }
    }
}
