package edu.nccu.mis.passpair;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.squareup.picasso.Picasso;

public class HomePage extends AppCompatActivity
       // implements NavigationView.OnNavigationItemSelectedListener
{
    BottomBar mBottomBar;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView name = (TextView)findViewById(R.id.txtNameHome);
        final ImageView img = (ImageView)findViewById(R.id.imageHome);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final String HomeUID = bundle.getString("UID");

        DatabaseReference myRef = database.getReference("User/"+HomeUID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String n = dataSnapshot.child("基本資料").child("暱稱").getValue(String.class);
//                String uri = dataSnapshot.child("img").getValue(String.class);
                String url = dataSnapshot.child("基本資料").child("大頭照").getValue(String.class);
                Log.d("TAG", "Value is: " + n);

                name.setText(n);
                Picasso.with(getApplicationContext()).load(url).into(img);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        mBottomBar = BottomBar.attach(findViewById(R.id.Replace_Home_Fragment),savedInstanceState);
        mBottomBar = (BottomBar) findViewById(R.id.BottombarHome);
        mBottomBar.setItems(R.menu.menu_home_page);
        mBottomBar.setOnMenuTabClickListener( new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottom_post){
                    PostFragment postFragment = new PostFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.Replace_Home_Fragment,postFragment).commit();
                }else if (menuItemId == R.id.bottom_friend){
                    HomeFragment homeFragment = new HomeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Username", "Hello,Fragment");
                    homeFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.Replace_Home_Fragment,homeFragment).commit();

                }else if (menuItemId == R.id.bottom_info){
                    ProfileFragment profileFragment = new ProfileFragment();

                    Bundle bundlePro=new Bundle();
                    bundlePro.putString("ProUID",HomeUID);
                    profileFragment.setArguments(bundlePro);

                    getSupportFragmentManager().beginTransaction().replace(R.id.Replace_Home_Fragment,profileFragment).commit();

                }else if (menuItemId == R.id.bottom_manu){
                    ManuFragment manuFragment = new ManuFragment();

                    Bundle bundleManu = new Bundle();
                    bundleManu.putString("ManuUID",HomeUID);
                    manuFragment.setArguments(bundleManu);

                    getSupportFragmentManager().beginTransaction().replace(R.id.Replace_Home_Fragment,manuFragment).commit();
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
        mBottomBar.mapColorForTab(0, "#BDBDBD");
        mBottomBar.mapColorForTab(1, "#BDBDBD");
        mBottomBar.mapColorForTab(2, "#BDBDBD");
        mBottomBar.mapColorForTab(3, "#BDBDBD");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
 /**       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
**/
    }
/**
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
 **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final String HomeUID = bundle.getString("UID");
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
  //          return true;
    //    }

        if(id == R.id.action_refresh){

            Intent intentF = new Intent();
            intentF.setClass(HomePage.this,FriendMail.class);
            Bundle bundleFri = new Bundle();
            bundleFri.putString("UIDFri", HomeUID);
            intent.putExtras(bundleFri);
            startActivity(intentF);
        }
        if(id == R.id.action_refresh2){

            Intent intentF = new Intent();
            intentF.setClass(HomePage.this,FriendMail.class);
            startActivity(intentF);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_pair) {

        } else if (id == R.id.nav_location) {

        } else if (id == R.id.nav_call) {

        } else if (id == R.id.nav_voicephoto) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    **/
}
