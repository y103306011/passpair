package edu.nccu.mis.passpair.RandomCall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import edu.nccu.mis.passpair.R;

public class QuestionRandomActivity extends AppCompatActivity {
    private Spinner answer_choice1;
    private Spinner answer_choice2;
    private Spinner answer_choice3;

    private String question1;
    private String question2;
    private String question3;
    private TextView question_text1;
    private TextView question_text2;
    private TextView question_text3;
    private int[] choice_index = new  int[]{R.array.question1, R.array.question2, R.array.question3, R.array.question4, R.array.question5, R.array.question6, R.array.question7, R.array.question8, R.array.question9, R.array.question10};
    private int[] question_index = new  int[]{R.string.question1, R.string.question2, R.string.question3, R.string.question4, R.string.question5, R.string.question6, R.string.question7, R.string.question8, R.string.question9, R.string.question10};
    private final int[] answers_pos = {50,50,50};
    private String[] answers = {"","",""};
    private boolean is_sent = false;

    private Button question_submit;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference question_Ref = database.getReference("question");

    private String myid = "";
    private String otherid = "";
    private String nodeid = "";

    private long timeCountInMilliSeconds = 30000;
    private CountDownTimer countDownTimer;

    private TextView question_countdown;
    private ProgressDialog progressDialog;
    private ProgressDialog q_progressDialog;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_random);

        answer_choice1 = (Spinner) findViewById(R.id.question_spinner1);
        answer_choice2 = (Spinner) findViewById(R.id.question_spinner2);
        answer_choice3 = (Spinner) findViewById(R.id.question_spinner3);
        question_text1 = (TextView) findViewById(R.id.question_text1);
        question_text2 = (TextView) findViewById(R.id.question_text2);
        question_text3 = (TextView) findViewById(R.id.question_text3);

        question_submit = (Button) findViewById(R.id.qustion_submit);
        question_countdown = (TextView) findViewById(R.id.question_countdown);

        Bundle bundle = this.getIntent().getExtras();
        myid = bundle.getString("UID");
        otherid = bundle.getString("recipient");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting data...");
        progressDialog.show();



        int min = 0;
        int max = 9;
        final int[] qmark = {55, 58, 50};
        for (int j = 0; j < 3; j++) {
            int Random = (int) (Math.random() * (max - min + 1) + min);
            qmark[j] = Random;
            if (qmark[0] == qmark[1]) {
                j = j - 1;
            }
            if (qmark[0] == qmark[2]) {
                j = j - 1;
            }
            if (qmark[1] == qmark[2]) {
                j = j - 1;
            }
        }

//        String ref_key =  question_Ref.push().getKey();
        if(myid.length() > otherid.length()){
            nodeid = myid + otherid;
        }else if (myid.length() < otherid.length()){
            nodeid = otherid + myid;
        }else if (myid.length() == otherid.length()){
            byte[] myid_byte = myid.getBytes();
            byte[] otherid_byte = otherid.getBytes();
            for (int i=0;i<myid.length()-1;i++){
                int byte_compare = Byte.compare(myid_byte[i], otherid_byte[i]);
                if ( byte_compare> 0){
                    nodeid = myid + otherid;
                }else if (byte_compare < 0){
                    nodeid = otherid + myid;
                }
            }
        }
        final DatabaseReference ref = question_Ref.child(nodeid);
        ref.child("question1").setValue(qmark[0]);
        ref.child("question2").setValue(qmark[1]);
        ref.child("question3").setValue(qmark[2]);

        question_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(nodeid) && !is_sent){
                    q_progressDialog = new ProgressDialog(QuestionRandomActivity.this);
                    q_progressDialog.setMessage("Getting data...");
                    q_progressDialog.show();
                    qmark[0] =  dataSnapshot.child(nodeid).child("question1").getValue(Long.class).intValue();
                    qmark[1] =  dataSnapshot.child(nodeid).child("question2").getValue(Long.class).intValue();
                    qmark[2] =  dataSnapshot.child(nodeid).child("question3").getValue(Long.class).intValue();

                    SettingLayouts(qmark,myid,otherid);
                    q_progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"請在30秒內回答問題!",Toast.LENGTH_LONG).show();
                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        startCountDownTimer();
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                question_countdown.setText("剩餘時間:  " + TimeFormatter(millisUntilFinished));
            }
            @Override
            public void onFinish() {

            }

        }.start();
        countDownTimer.start();
    }
    private String TimeFormatter(long milliSeconds) {
        String hms = String.format(":%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds));
        return hms;
    }
    private void SettingLayouts(int[] qmark, final String my_id, final String other_id){
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(QuestionRandomActivity.this, choice_index[qmark[0]], android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(QuestionRandomActivity.this, choice_index[qmark[1]], android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(QuestionRandomActivity.this, choice_index[qmark[2]], android.R.layout.simple_spinner_item);
        question1 = getResources().getString(question_index[qmark[0]]);
        question2 = getResources().getString(question_index[qmark[1]]);
        question3 = getResources().getString(question_index[qmark[2]]);


        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        answer_choice1.setAdapter(adapter1);
        answer_choice2.setAdapter(adapter2);
        answer_choice3.setAdapter(adapter3);
        question_text1.setText(question1);
        question_text2.setText(question2);
        question_text3.setText(question3);

        progressDialog.dismiss();

        answer_choice1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Random_Question.this, "您選擇"+ parent.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                answers[0] = parent.getSelectedItem().toString();
                answers_pos[0] = parent.getSelectedItemPosition();
                Log.e("postion1",parent.getSelectedItemPosition()+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        answer_choice2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Random_Question.this, "您選擇"+ parent.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                answers[1] = parent.getSelectedItem().toString();
                answers_pos[1] = parent.getSelectedItemPosition();
                Log.e("postion2",parent.getSelectedItemPosition()+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        answer_choice3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Random_Question.this, "您選擇"+ parent.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                answers[2] = parent.getSelectedItem().toString();
                answers_pos[2] = parent.getSelectedItemPosition();
                Log.e("postion3",parent.getSelectedItemPosition()+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        question_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_sent = true;
                final DatabaseReference ref =  question_Ref.child(nodeid);
                final ProgressDialog sending_data_progression = new ProgressDialog(QuestionRandomActivity.this);
                sending_data_progression.setMessage("Sending data...");
                sending_data_progression.show();
                for (int i =0;i<3;i++){
                    final String index =  Integer.toString(i+1);
                    final String child_answer = my_id + "answer" + index;
                    Log.e("index",child_answer);
                    final int pos = answers_pos[i];
                    ref.child(child_answer).setValue(answers[i], new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            ref.child(child_answer + "_pos").setValue(pos, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Intent intent = new Intent();
                                    Bundle bundle_question = new Bundle();
                                    bundle_question.putString("myid",myid);
                                    bundle_question.putString("otherid",otherid);
                                    bundle_question.putString("nodeid",nodeid);
                                    intent.setClass(QuestionRandomActivity.this,QuestionConfirmActivity.class);
                                    intent.putExtras(bundle_question);
                                    sending_data_progression.dismiss();
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
