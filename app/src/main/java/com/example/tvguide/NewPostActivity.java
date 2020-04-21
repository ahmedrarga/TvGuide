package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvguide.HomePage.CropFragment;
import com.example.tvguide.HomePage.Feeds;
import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.User.OnFinished;
import com.example.tvguide.User.WatchlistActivity;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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
import com.oginotihiro.cropview.CropView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

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

public class NewPostActivity extends BaseActivity implements View.OnClickListener
                                                    ,CropFragment.OnFragmentInteractionListener{
    private Requests r;
    private ArrayList<Map<String, Object>> map;
    private List<Movie> movies;
    private  Movie choosed;
    Database db;
    Button uploadIm;
    Button uploadV;
    Snackbar snackbar;
    CropView cropView;
    ProgressBar progressBar;
    TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        initToolbar("New post");
        db = new Database(getApplicationContext());
        uploadIm = findViewById(R.id.upload_image);
        uploadV = findViewById(R.id.upload_video);
        uploadV.setVisibility(View.GONE);
        uploadIm.setVisibility(View.GONE);
        cropView = (CropView) findViewById(R.id.cropView);
        progressBar = findViewById(R.id.progressBar4);
        message = findViewById(R.id.message);
        message.setVisibility(View.GONE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("watchlist").document(u).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                ArrayList<Map<String, Object>> map = (ArrayList) task.getResult().getData().get("listOfMovies");
                                new task().execute(map);

                            } catch (NullPointerException e) {

                            }
                        } else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage() + "ewfqqergkhjegfjWEHFGOWELFG ");
            }
        });
    }

    private void initRList(List<Movie> movies){
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
                        .fit()
                        .into(image);
                ((CardView)findViewById(R.id.card_image)).setForegroundGravity(Gravity.CENTER);
                image.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                TextView text = findViewById(R.id.selectText);
                text.setText(choosed.getName());
                text.setGravity(Gravity.CENTER);
                uploadV.setVisibility(View.VISIBLE);
                uploadIm.setVisibility(View.VISIBLE);


            }
        }));
        findViewById(R.id.progressBar4).setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
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


        if (requestCode == 1) {
            if (data != null) {
                Uri contentURI = data.getData();
                showCropFragment(contentURI, false);
            }

        } else if (requestCode == 0) {
            if (data != null) {
                snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Uploading..", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                Uri contentURI = data.getData();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                db.uploadVideoPost(String.valueOf(choosed.getId()), choosed.getName(), contentURI, "videos", dtf.format(now));



            }
        }
    }

    @Override
    public void onFragmentInteraction(Bitmap bitmap, boolean isCover) {
        snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Uploading", Snackbar.LENGTH_SHORT);
        snackbar.show();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        db.uploadImagePost(String.valueOf(choosed.getId()), choosed.getName(), bitmap, "images", dtf.format(now));
    }


    public class Database {
        private Database db;
        private FirebaseStorage storage;
        private FirebaseAuth mAuth;
        private FirebaseFirestore firestore;
        private FirebaseDatabase realTime;
        Post post;
        Context context;

        private Database(Context context){
            this.context = context;
            storage = FirebaseStorage.getInstance();
            mAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            realTime = FirebaseDatabase.getInstance();

        }



        public ArrayList<String> getImagesPaths(String mName){
            final ArrayList<String> toRet = new ArrayList<>();
            firestore.collection("posts")
                    .document(mName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){

                            }
                            else{
                            }
                        }
                    });


            return toRet;
        }

        public void uploadImagePost(final String name, final String title,final Bitmap image, final String type, final String time){
            final String mail = mAuth.getCurrentUser().getEmail();

            firestore.collection("posts").document(name)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post post = documentSnapshot.toObject(Post.class);
                            if (post == null) {
                                post = new Post();
                            }
                            String path = name + "/" + type + "/" + time;
                            post.setValue(mail, path, title);
                            firestore.collection("posts").document(name).set(post);
                            StorageReference ref = storage.getReference();
                            StorageReference pRef = ref.child(path);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = pRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    System.out.println("Failure ..................................");

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    System.out.println("Success ...................................");
                                    snackbar.dismiss();
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Uploaded", Snackbar.LENGTH_SHORT);
                                }
                            });
                        }

                    });
        }
        public void uploadVideoPost(final String name, final String title,final Uri uri, final String type, final String time){
            final String mail = mAuth.getCurrentUser().getEmail();
            final String path = name + "/" + type + "/" + time;

            firestore.collection("posts").document(name)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post post = documentSnapshot.toObject(Post.class);
                            if (post == null) {
                                post = new Post();
                            }
                            post.setValue(mail, path, title);
                            firestore.collection("posts").document(name).set(post);
                            StorageReference ref = storage.getReference();
                            StorageReference pRef = ref.child(path);

                            UploadTask uploadTask = pRef.putFile(uri);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    System.out.println("Failure ..................................");

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    System.out.println("Success ...................................");
                                    snackbar.dismiss();
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Uploaded", Snackbar.LENGTH_SHORT);
                                }
                            });
                        }

                    });
        }

        public ArrayList<Uri> getImagePosts(final String movie){
            final ArrayList<Uri> arrayList = new ArrayList<>();
            firestore.collection("posts").document(movie)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                post = task.getResult().toObject(Post.class);
                                if(post != null) {
                                    for (int i = 0; i < post.arrayList.size(); i++) {
                                        Map<String, String> map = post.arrayList.get(i);
                                        StorageReference ref = storage.getReference();
                                        StorageReference pRef = ref.child(map.get("path"));
                                        pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if(task.isSuccessful()){
                                                    arrayList.add(task.getResult());
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        }
                    });
            System.out.println(arrayList);
            return arrayList;


        }


    }
    public void showCropFragment(Uri uri, boolean isCover){
        CropFragment fragment = CropFragment.newInstance(uri.toString(), isCover);
        fragment.show(getSupportFragmentManager(), "Crop");
    }

    private class task extends AsyncTask<ArrayList<Map<String, Object>>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(ArrayList<Map<String, Object>>... voids) {
            final List<Movie> movies = new ArrayList<>();
            for(Map<String, Object> m : voids[0]){
                String query = "https://api.themoviedb.org/3/" +
                        (String)m.get("media_type") +
                        "/" + m.get("id") +
                        "?api_key=" + HomeActivity.api_key;
                Response response = setResponse(query);
                if(response != null && response.code() == 200){
                    try {
                        movies.add(new Movie(new JSONObject(response.body().string()), (String)m.get("media_type")));
                    }catch (IOException e1){

                    }catch (JSONException e2){

                    }

                }
            }
            return movies;
        }
        private Response setResponse(String query){
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            Request request = new Request.Builder()
                    .url(query)
                    .get()
                    .build();

            try {
                response = client.newCall(request).execute();
                return response;

            } catch (Exception e) {
                System.out.println("Error in doInBackground");
                System.out.println("Error:" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if(movies.size() == 0){
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            }else {
                initRList(movies);
            }

        }
    }

}
