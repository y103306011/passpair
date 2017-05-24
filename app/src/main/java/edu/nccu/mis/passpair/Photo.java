package edu.nccu.mis.passpair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.PhotoLoader;

import java.io.File;

public class Photo extends AppCompatActivity {
    Button PhotoSkip,PhotoUpload;
    ImageView PhotoPreview;

    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 1200;
    String photopath = "";
    private String uid = "";

    private StorageReference mStorageRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private UploadTask uploadTask;
    private Uri PhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        PhotoUpload = (Button) findViewById(R.id.PhotoUpload);
        PhotoPreview = (ImageView) findViewById(R.id.PhotoPreview) ;

        Bundle bundle_reg = this.getIntent().getExtras();
        uid = bundle_reg.getString("UID");

        galleryPhoto = new GalleryPhoto(getApplicationContext());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        PhotoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= galleryPhoto.openGalleryIntent();
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });
        PhotoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PhotoUri != null && !PhotoUri.equals(Uri.EMPTY)){
                    uploadfile(PhotoUri);
                }else {
                    Toast.makeText(getApplicationContext(),"請選擇相片",Toast.LENGTH_LONG).show();
                }

            }
        });


        /*Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String uid = bundle.getString("UID");*/

    }

    private void uploadfile(Uri file){
        File path = new File(file.getPath());
        StorageReference filepath;
        String filetype = "";
        //if there is a file to upload
        if (file != null) {
            if (file == PhotoUri){
                filepath = mStorageRef.child("Photo").child(path.getName());
                filetype = "相片";
                uploadTask = filepath.putFile(file);
            }
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(filetype+"上傳中");
            progressDialog.show();
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog
                    progressDialog.dismiss();
                    @SuppressWarnings("VisibleForTests")Uri downloadURL = taskSnapshot.getDownloadUrl();
                    DatabaseReference databaseReference = database.getReference().child("User").child(uid).child("基本資料").child("大頭照");
                    databaseReference.setValue(downloadURL.toString());
                    Toast.makeText(Photo.this, "上傳完成", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    //以bundle物件進行打包
                    Bundle bundle=new Bundle();
                    bundle.putString("UID", uid);
                    intent.putExtras(bundle);
                    intent.setClass(Photo.this, HomePage.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //if the upload is not successfull
                    //hiding the progress dialog
                    progressDialog.dismiss();

                    //and displaying error message
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == GALLERY_REQUEST){
                galleryPhoto.setPhotoUri(data.getData());
                photopath = galleryPhoto.getPath();
                PhotoUri = Uri.parse("file://"+photopath);
                try {
                    Bitmap bitmap = PhotoLoader.init().from(photopath).requestSize(512,512).getBitmap();
                    PhotoPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    PhotoPreview.setPadding(0,0,0,10);
                    PhotoPreview.setAdjustViewBounds(true);
                    PhotoPreview.setImageBitmap(bitmap);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error while loading image",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
 /**   Button PhotoSkip,PhotoUpload;
    ImageView PhotoPreview;
    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 1200;
    String photopath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        PhotoUpload = (Button) findViewById(R.id.PhotoUpload);
        PhotoPreview = (ImageView) findViewById(R.id.PhotoPreview) ;
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        PhotoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= galleryPhoto.openGalleryIntent();
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });
        PhotoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bitmap bitmap = PhotoLoader.init().from(photopath).requestSize(512,512).getBitmap();
                    final String encodedString = ImageBase64.encode(bitmap);
                    String url = "http://10.0.2.2/Pairpass/Login/PhotoUpload.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Error while uplaoding image",Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("image",encodedString);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String uid = bundle.getString("UID");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == GALLERY_REQUEST){
                galleryPhoto.setPhotoUri(data.getData());
                photopath = galleryPhoto.getPath();
                try {
                    Bitmap bitmap = PhotoLoader.init().from(photopath).requestSize(512,512).getBitmap();
                    PhotoPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    PhotoPreview.setPadding(0,0,0,10);
                    PhotoPreview.setAdjustViewBounds(true);
                    PhotoPreview.setImageBitmap(bitmap);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error while loading image",Toast.LENGTH_LONG).show();
                }
            }
        }
    }**/
}
