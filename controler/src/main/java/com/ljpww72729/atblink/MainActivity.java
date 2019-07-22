package com.ljpww72729.atblink;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ljpww72729.atblink.data.Constants;
import com.ljpww72729.atblink.databinding.ActivityMainBinding;
import com.ljpww72729.atblink.firebase.DeviceListActivity;
import com.ljpww72729.atblink.utils.SPUtils;

import ai.api.android.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.ui.AIButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceScanActivity.start(MainActivity.this);
            }
        });
        binding.realtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putInt(MainActivity.this, Constants.SP_FILE, Constants.DATABASE_ADDRESS, Constants.DATABASE_FIREBASE);
                DeviceListActivity.start(MainActivity.this, null);
            }
        });
        binding.wildDogRealtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putInt(MainActivity.this, Constants.SP_FILE, Constants.DATABASE_ADDRESS, Constants.DATABASE_WILDDOG);
                DeviceListActivity.start(MainActivity.this, null);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initLillianAssistant();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 10005);
        }
        TTS.init(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 10005) {
                initLillianAssistant();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initLillianAssistant() {
        final AIConfiguration config = new AIConfiguration("b3fb64f255ad41fb91181842412d16de",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));
        binding.assistant.initialize(config);
        binding.assistant.setResultsListener(new AIButton.AIButtonListener() {
            @Override
            public void onResult(final AIResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("ApiAi", "onResult" + response);
                        final Result result = response.getResult();
                        final String speech = result.getFulfillment().getSpeech();
                        Log.i("ApiAi", "Speech: " + speech);
                        TTS.speak(speech);
                    }
                });
            }

            @Override
            public void onError(final AIError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("ApiAi", "onError" + error);
                        // TODO process error here
                    }
                });
            }

            @Override
            public void onCancelled() {
                Log.i("ApiAi", "onCancelled");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.assistant.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.assistant.resume();
    }
}
