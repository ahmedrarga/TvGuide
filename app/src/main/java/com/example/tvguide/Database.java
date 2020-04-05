package com.example.tvguide;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tvguide.HomePage.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class Database {
    private static Database db;
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

    public static Database getInstance(Context context){
        if(db != null){
            return db;
        }
        return new Database(context);
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

    public void uploadImagePost(final String name, final Bitmap image, final String type, final String time){
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
                        post.setValue(mail, path);
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
                                Snackbar.make(((Activity)context).getWindow().getDecorView().getRootView(), "Uploaded", Snackbar.LENGTH_SHORT);
                            }
                        });
                    }

                });
    }
    public void uploadVideoPost(final String name, final Uri uri, final String type, final String time){
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
                        post.setValue(mail, path);
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
                                Snackbar.make(((Activity)context).getWindow().getDecorView().getRootView(), "Uploaded", Snackbar.LENGTH_SHORT);
                            }
                        });
                    }

                });
    }

    public synchronized ArrayList<Uri> getImagePosts(final String movie){
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
