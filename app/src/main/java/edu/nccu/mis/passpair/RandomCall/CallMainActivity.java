package edu.nccu.mis.passpair.RandomCall;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.nccu.mis.passpair.R;
import me.itangqi.waveloadingview.WaveLoadingView;

public class CallMainActivity extends AppCompatActivity {
    private static final String APP_KEY = "7fc486e6-dbaa-41f1-b34a-10d09feaf9e8";
    private static final String APP_SECRET = "B/R/DCPiuEGSe3B3kCK+bA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private Call call;
    private TextView callState,callers;
    private SinchClient sinchClient;
    private Button button,answer;
    private String callerId;
    private String recipientId;
    private AudioManager audioManager;
    private WaveLoadingView mWaveLoadingView;
    private long timeCountInMilliSeconds = 30000;
    private CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_main);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        Intent intent = getIntent();
        callerId = intent.getStringExtra("UID");
        recipientId = intent.getStringExtra("recipient");
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        button = (Button) findViewById(R.id.call_dial_btn);
        answer = (Button)findViewById(R.id.call_answer_btn);
        callState = (TextView) findViewById(R.id.callState);
        callers = (TextView) findViewById(R.id.callers);

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        callers.setText("callerid: " + callerId + "\nrecipientId: " + recipientId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    //button.setText("Hang Up");
                } else {
                    call.hangup();
                    Intent intent_main = new Intent();
                    Bundle bundle_select = new Bundle();
                    bundle_select.putString("UID",callerId);
                    bundle_select.putString("recipient",recipientId);
                    intent_main.putExtras(bundle_select);
                    intent_main.setClass(CallMainActivity.this,QuestionRandomActivity.class);
                    startActivity(intent_main);
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            button.setText("Call");
            callState.setText("disconnected");
            setVolumeControlStream(audioManager.USE_DEFAULT_STREAM_TYPE);
            Intent intent_main = new Intent();
            Bundle bundle_select = new Bundle();
            bundle_select.putString("UID",callerId);
            bundle_select.putString("recipient",recipientId);
            intent_main.putExtras(bundle_select);
            intent_main.setClass(CallMainActivity.this,QuestionRandomActivity.class);
            startActivity(intent_main);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            button.setText("Hang Up");
            audioManager.setSpeakerphoneOn(true);
            setVolumeControlStream(audioManager.STREAM_MUSIC);
            startCountdown();
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {}
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            callState.setText("Have an Incoming call");
            //設定接電話按鈕
            answer.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    call.answer();
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");
                }
            });
//            call.answer();
//            call.addCallListener(new SinchCallListener());
//            button.setText("Hang Up");
        }
    }
    // 消除登入狀態
    @Override
    protected void onStop() {
        super.onStop();
        if (call != null) {
            call.hangup();
        }
        //掛掉電話
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private void startCountdown(){
        // call to initialize the progress bar values
        setProgressBarValues();
        // call to start the count down timer
        startCountDownTimer();
    }

    private void setProgressBarValues() {
        mWaveLoadingView.setProgressValue(100);
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Long second_long = millisUntilFinished;
                int second_int_percent = second_long.intValue()*100;
                mWaveLoadingView.setProgressValue(second_int_percent/ 30000);
                mWaveLoadingView.setCenterTitle(TimeFormatter(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                // call to initialize the progress bar values
                mWaveLoadingView.setProgressValue(0);
                mWaveLoadingView.setCenterTitle(":00");
                if (call != null) {
                    call.hangup();
                }
            }

        }.start();
        countDownTimer.start();
    }
    private String TimeFormatter(long milliSeconds) {
        String hms = String.format(":%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds));
        return hms;
    }
}
