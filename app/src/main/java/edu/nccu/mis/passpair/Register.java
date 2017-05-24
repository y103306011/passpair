package edu.nccu.mis.passpair;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText RgUsername, RgPassword, RgConfirm, RgNickname, RgBirthday, RgEmail;
    Button RgButton;
    Spinner RgSexual;
    String[] sexual = new String[]{"男", "女"};
    String userSexual;
    String Error = "請輸入";
    String Uncorrect = "";
    DatePicker RgdatePicker;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerChatSession();

        RgUsername = (EditText) findViewById(R.id.RgUsername);
        RgPassword = (EditText) findViewById(R.id.RgPassword);
        RgConfirm = (EditText) findViewById(R.id.RgConfirm);
        RgNickname = (EditText) findViewById(R.id.RgNickname);
//        RgBirthday = (EditText) findViewById(R.id.RgBirthday);
        RgButton = (Button) findViewById(R.id.RgBotton);
        RgSexual = (Spinner) findViewById(R.id.RgSexual);
        RgdatePicker = (DatePicker) findViewById(R.id.datePicker);
        int year = RgdatePicker.getYear();
        int month = RgdatePicker.getMonth() + 1;
        int day = RgdatePicker.getDayOfMonth();
        RgButton.setOnClickListener(btnListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexual);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RgSexual.setAdapter(adapter);
        RgSexual.setOnItemSelectedListener(spnListener);

        String birth = String.valueOf(year+"/"+month+"/"+day);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    Button.OnClickListener btnListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {

            onSignIn();
        }
    };

    private void onSignIn(){
        final String email = RgUsername.getText().toString();
        final String pass = RgPassword.getText().toString();
        String confirmpass = RgConfirm.getText().toString();
        final String Nickname = RgNickname.getText().toString();

        int year = RgdatePicker.getYear();
        int month = RgdatePicker.getMonth() + 1;
        int day = RgdatePicker.getDayOfMonth();

        final String birth = String.valueOf(year+"/"+month+"/"+day);

        if (!email.isEmpty() && !pass.isEmpty()) {
            if(pass.equals(confirmpass)&& !Nickname.isEmpty() && !userSexual.isEmpty()){
                QBUser qbUser = new QBUser(email,pass);
                //設定Quickblox使用者姓名
                qbUser.setFullName(Nickname);
                //註冊Quickblox資料
                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getApplicationContext(),"Sign up successfully",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getApplicationContext(),"" + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });


                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(Register.this,"註冊成功"+authResult.getUser().getEmail(),Toast.LENGTH_SHORT).show();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                                    // authenticate with your backend server, if you have one. Use
                                    // FirebaseUser.getToken() instead.
                                    String uid = user.getUid();

                                    DatabaseReference myRef = database.getReference();
                                    //製作個人基本資料
                                    myRef.child("User").child(uid).child("基本資料").child("使用者信箱").setValue(email);
                                    myRef.child("User").child(uid).child("基本資料").child("密碼").setValue(pass);
                                    myRef.child("User").child(uid).child("基本資料").child("性別").setValue(userSexual);
                                    myRef.child("User").child(uid).child("基本資料").child("生日").setValue(birth);
                                    myRef.child("User").child(uid).child("基本資料").child("暱稱").setValue(Nickname);

                                    Intent intent = new Intent();
                                    //以bundle物件進行打包
                                    Bundle bundle=new Bundle();
                                    bundle.putString("UID", uid);
                                    intent.putExtras(bundle);

                                    intent.setClass(Register.this, Photo.class);
                                    startActivity(intent);

                                    //製作map所需資訊
                                    // myRef.child("Map資訊").child("其他使用者").setValue(Nickname);
                                    //移除本身多餘的資訊
                                    //  myRef.child(uid).child("Map資訊").child("其他使用者").child(Nickname).removeValue();

                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,"註冊失敗請重試",Toast.LENGTH_SHORT).show();

                    }
                });

            }else{

                Toast.makeText(Register.this,"密碼不一致",Toast.LENGTH_SHORT).show();

            }

        }


    }

  /**  Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (RgUsername.getText().toString().equals("")){
                Error = Error + " 帳號";
            }
            if (RgPassword.getText().toString().equals("")){
                Error = Error + " 密碼";
            }
            if (RgConfirm.getText().toString().equals("")) {
                Error = Error + " 確認密碼";
            }
            if (!RgPassword.getText().toString().equals(RgConfirm.getText().toString())){
                if (!RgPassword.getText().toString().equals("") && !RgConfirm.getText().toString().equals("")){
                    Uncorrect = Uncorrect + "密碼不一致!";
                }
            }
            if (RgNickname.getText().toString().equals("")){
                Error = Error + " 暱稱";
            }
            if (RgBirthday.getText().toString().equals("")){
                Error = Error + " 生日";
            }
            if (RgEmail.getText().toString().equals("")){
                Error = Error + " 電子郵件";
            }
            if (!Error.equals("請輸入")){
                Toast.makeText(getApplicationContext(),Error,Toast.LENGTH_LONG).show();
                Error = "請輸入";
            }else{
                if (!Uncorrect.equals("")){
                    Toast.makeText(getApplicationContext(),Uncorrect,Toast.LENGTH_LONG).show();
                    Uncorrect = "";
                }else {
                    String url = "http://10.0.2.2/PairPass/Login/Register.php";
                    StringRequest stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("Insert Success!")) {
                                Toast.makeText(getApplicationContext(), "註冊成功!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register.this,Photo.class);
                                startActivity(intent);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(getApplicationContext().getClass().getSimpleName(),error.toString());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("username",RgUsername.getText().toString());
                            params.put("password",RgPassword.getText().toString());
                            params.put("nickname",RgNickname.getText().toString());
                            params.put("date",RgBirthday.getText().toString());
                            params.put("mail",RgEmail.getText().toString());
                            params.put("sexuality",userSexual);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringrequest);
                }
            }
        }
    };**/
    private Spinner.OnItemSelectedListener spnListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            userSexual = adapterView.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private void registerChatSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }

}
