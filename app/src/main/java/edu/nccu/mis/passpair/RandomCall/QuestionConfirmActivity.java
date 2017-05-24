package edu.nccu.mis.passpair.RandomCall;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.nccu.mis.passpair.R;

public class QuestionConfirmActivity extends AppCompatActivity {
    private TextView question_confirm;
    private String myid = "";
    private String otherid = "";
    private String nodeid = "";
    private int[] question_switch ={5,6,9,10};

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference question_Ref = database.getReference("question");

    private int question_consistence = 0;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_confirm);
        question_confirm = (TextView) findViewById(R.id.question_confirm);

        Bundle bundle = this.getIntent().getExtras();
        myid = bundle.getString("myid");
        otherid = bundle.getString("otherid");
        nodeid = bundle.getString("nodeid");
        Log.e("otherid",otherid);
        Log.e("nodeid",nodeid);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在等待對方回答...");
        progressDialog.show();

        question_Ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(nodeid).hasChild(otherid+"answer1_pos") &&
                        dataSnapshot.child(nodeid).hasChild(otherid+"answer2_pos")&&
                        dataSnapshot.child(nodeid).hasChild(otherid+"answer3_pos")){
                    Toast.makeText(getApplicationContext(),"取得資料",Toast.LENGTH_LONG).show();
                    int[] question = {dataSnapshot.child(nodeid).child("question1").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child("question2").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child("question3").getValue(Long.class).intValue()};
                    int[] my_answer_pos = {dataSnapshot.child(nodeid).child(myid+"answer1_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(myid+"answer2_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(myid+"answer3_pos").getValue(Long.class).intValue()};
                    int[] other_answer_pos = {dataSnapshot.child(nodeid).child(otherid+"answer1_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(otherid+"answer2_pos").getValue(Long.class).intValue(),
                            dataSnapshot.child(nodeid).child(otherid+"answer3_pos").getValue(Long.class).intValue()};
                    for (int i = 0 ; i < 3 ; i ++) {
                        for (int j = 0; j < 4; j++) {
                            if (question_switch[j] == question[i]) {
                                if (my_answer_pos[i] + other_answer_pos[i] == 3) {
                                    question_consistence = question_consistence + 1;
                                    Log.e("confirm", question_consistence + "");
                                    Log.e("confirmno", j + "  " + i);
                                }
                            }
                        }
                        if (question_switch[0] != question[i] && question_switch[1] != question[i] && question_switch[2] != question[i] && question_switch[3] != question[i]) {
                            if (my_answer_pos[i] == other_answer_pos[i]) {
                                question_consistence = question_consistence + 1;
                                Log.e("confirm", question_consistence + "");
                                Log.e("confirmno", "" + i);
                            }
                        }
                        question_confirm.setText("答案相同" + question_consistence + "題\n" + "相同率" + (question_consistence * 100) / 3 + "%");
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
