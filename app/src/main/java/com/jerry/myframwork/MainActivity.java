package com.jerry.myframwork;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jerry.baselib.R;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.base.BaseActivity;
import com.jerry.myframwork.view.WaveView;

public class MainActivity extends BaseActivity {

    private static final int REQ_CODE = 200;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private String outputPath;
    private WaveView waveView;

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setTitle(R.string.home);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        waveView = findViewById(R.id.waveView);
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.btn_start) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE);
            } else {
                startRecording();
            }
        } else if (id == R.id.btn_stop) {
            stopRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "需要录音权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        if (isRecording) {
            return;
        }
        int minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (ActivityCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "需要录音权限", Toast.LENGTH_SHORT).show();
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, minBuffer);
        File dir = getExternalFilesDir("records");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        outputPath = new File(dir, "record_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
        isRecording = true;
        audioRecord.startRecording();
        Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show();
        AppTask.with(this).assign(() -> {
            byte[] buffer = new byte[minBuffer];
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        fos.write(buffer, 0, read);
                        waveView.updateWaveform(buffer, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).execute();
    }

    private void stopRecording() {
        if (!isRecording) {
            return;
        }
        try {
            audioRecord.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioRecord.release();
        audioRecord = null;
        isRecording = false;
        Toast.makeText(this, "录音已保存: " + outputPath, Toast.LENGTH_LONG).show();
    }
}
