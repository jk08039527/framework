package com.jerry.myframwork;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jerry.app.R;
import com.jerry.baselib.base.BaseActivity;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer;

public class MainActivity extends BaseActivity {

    private static final String TAG = "SpeechTranslator";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    // 替换为你的 Azure 语音服务密钥和区域
    private static final String SPEECH_SUBSCRIPTION_KEY = "YOUR_AZURE_SPEECH_KEY";
    private static final String SERVICE_REGION = "eastasia";

    private Button btnStart, btnStop, btnPlay;
    private TextView tvOriginal, tvTranslated, tvStatus;

    private SpeechTranslationConfig speechConfig;
    private TranslationRecognizer recognizer;
    private SpeechSynthesizer synthesizer;
    private ExecutorService executorService;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;

    private boolean isRecognizing = false;
    private boolean isPlaying = false;
    private String lastTranslatedText = "";

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // 初始化音频管理器
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setupAudioFocusListener();

        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnPlay = findViewById(R.id.btn_play);
        tvOriginal = findViewById(R.id.tv_original);
        tvTranslated = findViewById(R.id.tv_translated);
        tvStatus = findViewById(R.id.tv_status);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnStart.setOnClickListener(v -> startTranslation());
        btnStop.setOnClickListener(v -> stopTranslation());
        btnPlay.setOnClickListener(v -> playTranslatedText());
        executorService = Executors.newFixedThreadPool(2);

        checkAudioPermission();
    }

    private void setupAudioFocusListener() {
        audioFocusChangeListener = focusChange -> {
            Log.d(TAG, "音频焦点变化: " + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.i(TAG, "获得音频焦点");
                    if (isRecognizing && !isPlaying) {
                        resumeRecognition();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.i(TAG, "长时间失去音频焦点");
                    stopRecognitionTemporarily();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.i(TAG, "暂时失去音频焦点");
                    if (isRecognizing) {
                        pauseRecognition();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.i(TAG, "需要降低音量，暂停识别");
                    if (isRecognizing) {
                        pauseRecognition();
                    }
                    break;
            }
        };
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initializeSpeechService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechService();
            } else {
                Toast.makeText(this, "需要麦克风权限才能使用语音翻译", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeSpeechService() {
        executorService.execute(() -> {
            try {
                // 初始化语音翻译配置
                speechConfig = SpeechTranslationConfig.fromSubscription(
                    SPEECH_SUBSCRIPTION_KEY, SERVICE_REGION);

                // 设置识别语言（中文）
                speechConfig.setSpeechRecognitionLanguage("zh-CN");

                // 添加目标翻译语言
                speechConfig.addTargetLanguage("en"); // 英文
//                    speechConfig.addTargetLanguage("ja"); // 日文
//                    speechConfig.addTargetLanguage("ko"); // 韩文

                // 优化移动设备设置
                speechConfig.setProperty(PropertyId.SpeechServiceConnection_EnableAudioLogging, "False");
                speechConfig.setProperty(PropertyId.Speech_SegmentationSilenceTimeoutMs, "3000");

                // 使用默认麦克风
                AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();

                // 创建翻译识别器
                recognizer = new TranslationRecognizer(speechConfig, audioConfig);

                // 初始化语音合成器（TTS）
                initializeSynthesizer();

                setupRecognitionEvents();

                runOnUiThread(() -> {
                    tvStatus.setText("服务初始化完成");
                    btnStart.setEnabled(true);
                });

            } catch (Exception e) {
                Log.e(TAG, "初始化语音服务失败", e);
                runOnUiThread(() -> {
                    tvStatus.setText("初始化失败: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "语音服务初始化失败", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void initializeSynthesizer() {
        try {
            // 创建语音合成配置
            SpeechConfig synthConfig = SpeechConfig.fromSubscription(SPEECH_SUBSCRIPTION_KEY, SERVICE_REGION);

            // 设置语音合成语言和声音
            synthConfig.setSpeechSynthesisLanguage("en-US");
            synthConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");

            // 使用默认扬声器输出
            AudioConfig audioOutput = AudioConfig.fromDefaultSpeakerOutput();

            // 创建语音合成器
            synthesizer = new SpeechSynthesizer(synthConfig, audioOutput);

            Log.i(TAG, "语音合成器初始化完成");

        } catch (Exception e) {
            Log.e(TAG, "初始化语音合成器失败", e);
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "语音播放功能初始化失败", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupRecognitionEvents() {
        // 实时识别中事件
        recognizer.recognizing.addEventListener((sender, e) -> {
            if (e.getResult().getReason() == ResultReason.TranslatingSpeech) {
                String partialText = e.getResult().getText();
                String translation = e.getResult().getTranslations().get("en");

                runOnUiThread(() -> {
                    tvOriginal.setText("识别中: " + partialText);
                    if (translation != null && !translation.isEmpty()) {
                        tvTranslated.setText("翻译中: " + translation);
                    }
                });
            }
        });

        // 识别完成事件
        recognizer.recognized.addEventListener((sender, e) -> {
            if (e.getResult().getReason() == ResultReason.TranslatedSpeech) {
                String finalText = e.getResult().getText();
                String englishTranslation = e.getResult().getTranslations().get("en");

                runOnUiThread(() -> {
                    tvOriginal.setText("原文: " + finalText);

                    // 显示所有语言的翻译结果
                    StringBuilder translations = new StringBuilder();
                    e.getResult().getTranslations().forEach((language, translation) -> {
                        String langName = getLanguageName(language);
                        translations.append(langName).append(": ").append(translation).append("\n");
                    });

                    tvTranslated.setText(translations.toString());

                    // 保存英文翻译用于播放
                    if (englishTranslation != null && !englishTranslation.trim().isEmpty()) {
                        lastTranslatedText = englishTranslation;
                        btnPlay.setEnabled(true);
                    }
                });

                Log.i(TAG, "识别结果: " + finalText);
                Log.i(TAG, "英文翻译: " + englishTranslation);
            }
        });

        // 取消事件
        recognizer.canceled.addEventListener((sender, e) -> runOnUiThread(() -> {
            tvStatus.setText("识别已取消");
            if (e.getReason() == CancellationReason.Error) {
                String errorMsg = "错误: " + e.getErrorDetails();
                tvStatus.setText(errorMsg);
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "识别错误: " + e.getErrorDetails());
            }
        }));

        // 会话事件
        recognizer.sessionStarted.addEventListener((sender, e) -> runOnUiThread(() -> tvStatus.setText("会话开始")));

        recognizer.sessionStopped.addEventListener((sender, e) -> runOnUiThread(() -> tvStatus.setText("会话结束")));
    }

    private String getLanguageName(String languageCode) {
        switch (languageCode) {
            case "en":
                return "英文";
            case "ja":
                return "日文";
            case "ko":
                return "韩文";
            default:
                return languageCode;
        }
    }

    private void startTranslation() {
        if (recognizer == null) {
            Toast.makeText(this, "语音服务未初始化", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                recognizer.startContinuousRecognitionAsync().get();
                runOnUiThread(() -> {
                    isRecognizing = true;
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    btnPlay.setEnabled(false);
                    tvStatus.setText("正在监听...请开始说话");
                    tvOriginal.setText("");
                    tvTranslated.setText("");
                    lastTranslatedText = "";
                });
            } catch (Exception e) {
                Log.e(TAG, "启动识别失败", e);
                runOnUiThread(() -> {
                    tvStatus.setText("启动失败: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "启动识别失败", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void stopTranslation() {
        if (recognizer == null || !isRecognizing) {
            return;
        }

        executorService.execute(() -> {
            try {
                recognizer.stopContinuousRecognitionAsync().get();
                runOnUiThread(() -> {
                    isRecognizing = false;
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    tvStatus.setText("已停止监听");
                });
            } catch (Exception e) {
                Log.e(TAG, "停止识别失败", e);
                runOnUiThread(() -> tvStatus.setText("停止失败: " + e.getMessage()));
            }
        });
    }

    private void playTranslatedText() {
        if (lastTranslatedText == null || lastTranslatedText.trim().isEmpty()) {
            Toast.makeText(this, "没有可播放的文本", Toast.LENGTH_SHORT).show();
            return;
        }

        if (synthesizer == null) {
            Toast.makeText(this, "语音合成器未就绪", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPlaying) {
            Toast.makeText(this, "正在播放中，请稍候", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                runOnUiThread(() -> {
                    isPlaying = true;
                    btnPlay.setEnabled(false);
                    tvStatus.setText("正在播放...");
                });

                // 请求音频焦点
                int result = audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                );

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.i(TAG, "获得音频焦点，开始播放");

                    // 暂停语音识别
                    pauseRecognition();

                    // 执行语音合成
                    SpeechSynthesisResult speechResult = synthesizer.SpeakTextAsync(lastTranslatedText).get();

                    // 处理播放结果 - 修复这里
                    if (speechResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                        Log.i(TAG, "语音播放完成");
                    } else if (speechResult.getReason() == ResultReason.Canceled) {
                        // 修复这里：使用 SpeechSynthesisCancellationDetails
                        SpeechSynthesisCancellationDetails cancellation =
                            SpeechSynthesisCancellationDetails.fromResult(speechResult);
                        Log.e(TAG, "语音播放取消: " + cancellation.getErrorDetails());
                    }

                    // 短暂延迟，确保播放完全结束
                    Thread.sleep(300);
                } else {
                    Log.w(TAG, "无法获得音频焦点");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "无法播放，其他应用正在使用音频", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "语音播放失败", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                // 无论成功与否，都要执行清理
                runOnUiThread(() -> {
                    isPlaying = false;
                    btnPlay.setEnabled(true);
                });

                // 恢复识别
                if (isRecognizing) {
                    resumeRecognition();
                }

                // 释放音频焦点
                audioManager.abandonAudioFocus(audioFocusChangeListener);

                runOnUiThread(() -> tvStatus.setText(isRecognizing ? "正在监听..." : "准备就绪"));
            }
        });
    }

    private void pauseRecognition() {
        if (isRecognizing && recognizer != null) {
            try {
                recognizer.stopContinuousRecognitionAsync().get();
                Log.i(TAG, "语音识别已暂停");
            } catch (Exception e) {
                Log.e(TAG, "暂停识别失败", e);
            }
        }
    }

    private void resumeRecognition() {
        if (isRecognizing && recognizer != null) {
            try {
                // 短暂延迟确保环境稳定
                Thread.sleep(200);
                recognizer.startContinuousRecognitionAsync().get();
                Log.i(TAG, "语音识别已恢复");
            } catch (Exception e) {
                Log.e(TAG, "恢复识别失败", e);
            }
        }
    }

    private void stopRecognitionTemporarily() {
        if (isRecognizing && recognizer != null) {
            try {
                recognizer.stopContinuousRecognitionAsync().get();
                isRecognizing = false;
                runOnUiThread(() -> {
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    tvStatus.setText("音频焦点丢失，识别已停止");
                });
            } catch (Exception e) {
                Log.e(TAG, "停止识别失败", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTranslation();

        if (recognizer != null) {
            recognizer.close();
        }

        if (synthesizer != null) {
            synthesizer.close();
        }

        if (executorService != null) {
            executorService.shutdown();
        }

        // 释放音频焦点
        audioManager.abandonAudioFocus(audioFocusChangeListener);
    }

    @Override
    public void onClick(final View view) {

    }
}