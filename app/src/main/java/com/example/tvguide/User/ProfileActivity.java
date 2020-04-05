package com.example.tvguide.User;

import androidx.annotation.Nullable;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.example.tvguide.BaseActivity;
import com.example.tvguide.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
public class ProfileActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private CircleImageView circleImageView;
    private FirebaseFirestore db;
    private TextView Firstname,Lastname,Email;
    private String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //profilePicImageView = findViewById(R.id.profile_pic_imageView);
        String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        circleImageView = (CircleImageView)findViewById(R.id.circleImage);

        storageReference.child("images/" + mail + "/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerInside().into(circleImageView);
                getusername();
            }
        });


    }

    private void getusername(){


        Firstname = findViewById(R.id.textviewfirstname);
        Lastname = findViewById(R.id.textviewlastname);
        Email = findViewById(R.id.textviewEmailAddress);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();




        db = FirebaseFirestore.getInstance();


        try {
            Task<QuerySnapshot> task = db.collection("users").whereEqualTo("EMAIL", email).get().
            addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(id, "onSuccess: LIST EMPTY");
                        return;
                    }else{
                        id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        String topass = id;
                        saveSess(topass);
                    }
                }
            });

            final DocumentReference documentReference = db.collection("users").document(getSess());
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    Firstname.setText(documentSnapshot.getString("FIRST_NAME"));
                    Lastname.setText(documentSnapshot.getString("LAST_NAME"));
                    Email.setText(documentSnapshot.getString("EMAIL"));
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

    }
    public void saveSess(String a){
        final SharedPreferences preferences = getSharedPreferences("com.blabla.yourapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("NameOfShared", a);
        editor.commit();
    }
    public String getSess(){
        final SharedPreferences mSharedPreference = getSharedPreferences("com.blabla.yourapp", Context.MODE_PRIVATE);
        String value=(mSharedPreference.getString("NameOfShared", "Default_Value"));
        return value;
    }

}
