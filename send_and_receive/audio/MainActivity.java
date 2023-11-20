package com.renhui.audiodemo;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.content.Context;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import  java.util.List;

import android.Manifest;
import android.app.Service;

import android.content.pm.PackageManager;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



//import static com.renhui.audiodemo.GlobalConfig.AUDIO_FORMAT;
//import static com.renhui.audiodemo.GlobalConfig.CHANNEL_CONFIG;
//import static com.renhui.audiodemo.GlobalConfig.SAMPLE_RATE_INHZ;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST = 1001;
    private static final String TAG = "jqd";

    private Button mBtnControl;
    private Button mBtnPlay;

    private SensorManager mSensorMgr;
    private TextView tvx;
    private TextView tvy;
    private TextView tvz;

    private  List<String> LS;


    TextView txt;
    private int count = 0;
    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 被用户拒绝的权限列表
     */
    private List<String> mPermissionList = new ArrayList<>();
    private boolean isRecording;
    private AudioRecord audioRecord;
    private Button mBtnConvert;
    private AudioTrack audioTrack;
    private byte[] audioData;
    private FileInputStream fileInputStream;
    private AudioManager am;
    private MediaPlayer mediaPlayer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button click_btn = findViewById(R.id.click_button);
        click_btn.setOnClickListener(this);
        txt = (TextView)findViewById(R.id.click_num);

        Button bt = findViewById(R.id.bt_dsp);
        bt.setOnClickListener(this);

        Button bt_stop=findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(this);

        LS=new ArrayList<String>();
        tvx = findViewById(R.id.tvx);
        tvy = findViewById(R.id.tvy);
        tvz = findViewById(R.id.tvz);
        mSensorMgr=(SensorManager)getSystemService(Context.SENSOR_SERVICE);


        mBtnControl = (Button) findViewById(R.id.button);
        mBtnControl.setOnClickListener(this);
        mBtnControl = (Button) findViewById(R.id.btn_control);
        mBtnControl.setOnClickListener(this);
        mBtnConvert = (Button) findViewById(R.id.btn_convert);
        mBtnConvert.setOnClickListener(this);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);
        am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        mediaPlayer2 = new MediaPlayer();
        checkPermissions();
    }
    protected void onPause()
    {
        super.onPause();
        mSensorMgr.unregisterListener(this);
    }

    protected void onResume()
    {
        super.onResume();
    }
    protected void onStop()
    {
        super.onStop();
        mSensorMgr.unregisterListener(this);

    }


    public void Play_wav(){

        mediaPlayer2.start();
    }


    public void Stop_wav(){
        mediaPlayer2.stop();
        mediaPlayer2.release();
        mediaPlayer2 = null;
    }
    public void ConvertToCall(){
        am.setSpeakerphoneOn(false);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public void ConvertToOpen(){
        am.setMode(AudioManager.MODE_NORMAL);
        am.setSpeakerphoneOn(true);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click_button:
                count++;
                txt.setText(String.valueOf(count));
                break;
            case R.id.bt_dsp:
                mSensorMgr.unregisterListener(this,mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                mSensorMgr.registerListener(this,
                        mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
                LS.clear();
                break;
            case R.id.bt_stop:
                mSensorMgr.unregisterListener(this);
                writeLS(LS);
                break;
            case R.id.btn_control:
                Button button = (Button) view;
                if (button.getText().toString().equals(getString(R.string.start_record))) {

                    button.setText(getString(R.string.stop_record));
                    mediaPlayer2  = MediaPlayer.create(this, R.raw.f10000);

                    startRecord();
                    Play_wav();
                } else {

                    button.setText(getString(R.string.start_record));
                    Stop_wav();
                    stopRecord();
                }

                break;
            case R.id.btn_convert:
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(date);
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(48000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                File pcmFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
                File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), time + ".wav");
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created");
                }
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());

                break;
            case R.id.btn_play:
                Button btn = (Button) view;
                String string = btn.getText().toString();
                if (string.equals(getString(R.string.start_play))) {
                    btn.setText(getString(R.string.stop_play));
                    playInModeStream();
                    //playInModeStatic();
                } else {
                    btn.setText(getString(R.string.start_play));
                    stopPlay();
                }
                break;
            case R.id.button:
                Button button3 = (Button) view;
                if (button3.getText().toString().equals(getString(R.string.open))) {
                    button3.setText(getString(R.string.call));
                    ConvertToCall();

                } else {
                    button3.setText(getString(R.string.open));
                    ConvertToOpen();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！");
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }


    public void startRecord() {
        final int minBufferSize = AudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 48000,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

        final byte data[] = new byte[minBufferSize];
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }

        audioRecord.startRecording();
        isRecording = true;



        new Thread(new Runnable() {
            @Override
            public void run() {

                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != os) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Log.i(TAG, "run: close file output stream !");
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void stopRecord() {
        isRecording = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            //recordingThread = null;
        }
    }


    private void checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
    }


    /**
     * 播放，使用stream模式
     */
    private void playInModeStream() {
        /*
        * SAMPLE_RATE_INHZ 对应pcm音频的采样率
        * channelConfig 对应pcm音频的声道
        * AUDIO_FORMAT 对应pcm音频的格式
        * */
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        final int minBufferSize = AudioTrack.getMinBufferSize(48000, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder().setSampleRate(48000)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        try {
            fileInputStream = new FileInputStream(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] tempBuffer = new byte[minBufferSize];
                        while (fileInputStream.available() > 0) {
                            int readCount = fileInputStream.read(tempBuffer);
                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                    readCount == AudioTrack.ERROR_BAD_VALUE) {
                                continue;
                            }
                            if (readCount != 0 && readCount != -1) {
                                audioTrack.write(tempBuffer, 0, readCount);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 停止播放
     */
    private void stopPlay() {
        if (audioTrack != null) {
            Log.d(TAG, "Stopping");
            audioTrack.stop();
            Log.d(TAG, "Releasing");
            audioTrack.release();
            Log.d(TAG, "Nulling");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            float[] values=sensorEvent.values;

            tvx.setText("ACC_X: "+Float.toString(values[0]));
            tvy.setText("ACC_Y: "+Float.toString(values[1]));
            tvz.setText("ACC_Z: "+Float.toString(values[2]));

            Date date=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=formatter.format(date);
            String s="";
            s=time+" "+Float.toString(values[0])+" "+Float.toString(values[1])+" "+Float.toString(values[2])+"\n";
            LS.add(s);

        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }


    private static final String TAG2 = "ACCCollection:";

    public void writeLS(List<String> LS) {
        try {
//            String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/aaa/";
//            File folde=new File(path);
            File folde = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"/log/");
            Log.i(TAG2, "write: -------1");
            if (!folde.exists() || !folde.isDirectory())
            {
                Log.i(TAG2, "write: --------2");
                folde.mkdirs();
            }
//            File file=new File(path,"aa.csv");
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"/log/"+System.currentTimeMillis()+".csv");
            if(!file.exists())
            {
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            int i;
            for(i=0;i<LS.size();i++)
            {
                bw.write(LS.get(i));
                bw.newLine();// 行换行
            }
            bw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
