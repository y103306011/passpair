package edu.nccu.mis.passpair.RandomCall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.nccu.mis.passpair.R;

public class CallSelectActivity extends AppCompatActivity {
    String[] age_range = {"15~20歲","21~30歲","31~40歲","40歲以上"};
    String[] match_range = {"60%~70%","71%~80%","81%~90%","90%以上"};
    Spinner age_spn,match_spn;
    Button start;
    String age_select;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_select);

        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();
        final String UID = bundle.getString("UID");

        age_spn = (Spinner) findViewById(R.id.call_select_age_spinner);
        match_spn = (Spinner) findViewById(R.id.call_select_match_spinner);
        start = (Button) findViewById(R.id.call_select_btn);

        ArrayAdapter<String> adapter_age = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,age_range);
        ArrayAdapter<String> adapter_match = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,match_range);
        adapter_age.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_match.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        age_spn.setAdapter(adapter_age);
        match_spn.setAdapter(adapter_match);

        age_spn.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age_select = adapterView.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),"您選擇了:"+age_select,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        match_spn.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加入通話清單,但尚未接通
                databaseReference.child("callonline").child(UID).setValue(0);
                //尋找未接通的使用者,並且配對
                final List<String> online_user = new ArrayList<String>();
                final List<Integer> online_status = new ArrayList<Integer>();
                databaseReference.child("callonline").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Toast.makeText(getApplicationContext(),UID + "",Toast.LENGTH_SHORT).show();
                            if (!TextUtils.equals(ds.getKey(),UID)){
                                online_user.add(ds.getKey());
                                online_status.add(ds.getValue(Long.class).intValue());
                                Toast.makeText(getApplicationContext(),ds.getKey() + "",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),TextUtils.equals(ds.getKey(),UID)+"",Toast.LENGTH_SHORT).show();
                            }
                        }
                        Long online_count =  dataSnapshot.getChildrenCount();
                        int online_user_include = online_user.size()+1;
                        if (online_count.intValue() == online_user_include){
                            databaseReference.child("callonline").child(UID).setValue(1);
                            int random_count = (int)Math.random()*online_user.size();
                            String recipient = online_user.get(random_count);
                            Intent intent_select = new Intent();
                            Bundle bundle_select = new Bundle();
                            bundle_select.putString("UID",UID);
                            bundle_select.putString("recipient",recipient);
                            intent_select.putExtras(bundle_select);
                            intent_select.setClass(CallSelectActivity.this,CallMainActivity.class);
                            startActivity(intent_select);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //篩選資格
//                databaseReference.child("User").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });


            }
        });
    }
}
