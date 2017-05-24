package edu.nccu.mis.passpair;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;

public class MainActivity extends AppCompatActivity {
    ImageView InitRegister,InitLogin;
    static final String APP_ID = "56869";
    static final String AUTH_KEY = "QpumFnFvKmkz9TT";
    static final String AUTH_SECRET = "gypmSSEMVmdc62j";
    static final String ACCOUNT_KEY = "j1MhWQ2zAq66YGo2ca3Z";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitRegister = (ImageView) findViewById(R.id.initbutreg);
        InitLogin = (ImageView) findViewById(R.id.initbutlogin);
        InitRegister.setOnClickListener(btnListener);
        InitLogin.setOnClickListener(btnListener);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
        }
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }
    View.OnClickListener btnListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.initbutlogin :{
                    Intent intent1 = new Intent(MainActivity.this,Login.class);
                    startActivity(intent1);
                    break;
                }
                case R.id.initbutreg :{
                    Intent intent2 = new Intent(MainActivity.this,Register.class);
                    startActivity(intent2);
                    break;
                }
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("取得權限")
                        .setMessage("已取得權限,案確定鈕結束應用程式後重新啟動")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            } else {
                Toast.makeText(this, "未取得權限!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
