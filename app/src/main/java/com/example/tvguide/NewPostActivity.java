package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPostActivity extends BaseActivity implements View.OnClickListener {
    private Requests r;
    private ArrayList<Map<String, Object>> map;
    private List<Movie> movies;
    private  Movie choosed;
    Database db = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        initToolbar("New post");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                db.collection("watchlist").document(u).
                        get().
                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    synchronized (this) {
                                        try {
                                            map = (ArrayList)task.getResult().getData().get("listOfMovies");
                                            r = new Requests();
                                            movies = r.getMoviesFromHashMap(map);
                                            initRList();

                                        }catch (NullPointerException e){

                                        }
                                    }
                                }
                                else{
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Error in importing data", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage() + "ewfqqergkhjegfjWEHFGOWELFG ");
                    }
                });
            }
        });
    }

    private void initRList(){
        final RecyclerView view = findViewById(R.id.watchlist);
        view.setAdapter(new NewPostAdapter(movies, getApplicationContext(), new NewPostListener() {
            @Override
            public void clicked(Movie choose) {
                System.out.println(choose.getName());
                choosed = choose;
                System.out.println(db.getImagePosts(choosed.getName()));
                ImageView image = findViewById(R.id.choosed);
                Picasso.get()
                        .load(choosed.getPoster_path())
                        .into(image);
                view.setVisibility(View.GONE);
                ((TextView)findViewById(R.id.selectText)).setText("You choosed");
                final Button uploadIm = findViewById(R.id.upload_image);
                final Button uploadV = findViewById(R.id.upload_video);
                uploadV.setVisibility(View.VISIBLE);
                uploadIm.setVisibility(View.VISIBLE);


            }
        }));
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
    }

    public void onClick(View view) {
        if(view.getId() == R.id.upload_image){
            System.out.println("Upload Image");
            selectPhoto();
        } else if(view.getId() == R.id.upload_video){
            System.out.println("Upload Video");
            selectVideo();
        }

    }

    private void selectVideo(){
        requestMultiplePermissions();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 0);
    }

    private void selectPhoto(){
        requestMultiplePermissions();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 1);
    }
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        if (requestCode == 1) {
            if (data != null) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Uploaded", Snackbar.LENGTH_SHORT).show();
                Uri contentURI = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);


                    db.uploadImagePost(choosed.getName(), bitmap, "images", dtf.format(now));
                }catch (FileNotFoundException e){
                    System.out.println(e.getMessage());
                }catch (IOException e1){
                    System.out.println(e1.getMessage());
                }


            }

        } else if (requestCode == 0) {
            if (data != null) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Uploading..", Snackbar.LENGTH_INDEFINITE).show();
                Uri contentURI = data.getData();
                db.uploadVideoPost(choosed.getName(), contentURI, "videos", dtf.format(now));



            }
        }
    }

}
