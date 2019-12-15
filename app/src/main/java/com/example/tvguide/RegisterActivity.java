package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        TextInputLayout firstName = findViewById(R.id.firstName);
        TextInputLayout lastName = findViewById(R.id.lastName);
        TextInputLayout email = findViewById(R.id.email);
        TextInputLayout password = findViewById(R.id.password);
        TextInputLayout rePass = findViewById(R.id.rePass);
        final String fName = firstName.getEditText().getText().toString();
        final String lName = lastName.getEditText().getText().toString();
        final String mail = email.getEditText().getText().toString();
        final String pass = password.getEditText().getText().toString();
        final String passRe = rePass.getEditText().getText().toString();
        if(fName.isEmpty() || lName.isEmpty() || mail.isEmpty() || pass.isEmpty()
                || pass.length() < 6 || !pass.equals(passRe)) {
            if (fName.isEmpty()) {
                firstName.getEditText().requestFocus();
                firstName.getEditText().setError("Cannot be empty!");
            }
            if (lName.isEmpty()) {
                lastName.getEditText().requestFocus();
                lastName.getEditText().setError("Cannot be empty!");
            }
            if (mail.isEmpty()) {
                email.getEditText().requestFocus();
                email.getEditText().setError("Cannot be empty!");
            }
            if (pass.isEmpty()) {
                password.getEditText().requestFocus();
                password.getEditText().setError("Cannot be empty!");
            }
            if (pass.length() < 6) {
                password.getEditText().requestFocus();
                password.getEditText().setError("At least 6 characters!");
            }
            if (!pass.equals(passRe)) {
                rePass.getEditText().setError("Passwords doesn't match!");
                rePass.getEditText().requestFocus();
            }
        }else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();
            db = FirebaseFirestore.getInstance();
            final Map<String, String> toAdd = new HashMap<>();
            toAdd.put(Constants.FIRST_NAME.toString(), fName);
            toAdd.put(Constants.LAST_NAME.toString(), lName);
            toAdd.put(Constants.EMAIL.toString(), mail);
            Query query = db.collection("users").whereEqualTo(Constants.EMAIL.toString(), mail);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (Objects.requireNonNull(task.getResult().size() > 0)) {
                        Toast.makeText(getApplicationContext(), "E-mail has registered before!", Toast.LENGTH_SHORT).show();

                    } else {
                        createUser(toAdd, pass);
                    }
                }
            });
        }
    }

    private void createUser(Map<String, String> user, String pass){
        final Map<String, String> u = user;
        mAuth.createUserWithEmailAndPassword(user.get(Constants.EMAIL.toString()), pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            db.collection("users")
                                    .add(u)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(), "User Registered Successfuly", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                            System.out.println("createUserWithEmail:success");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("createUserWithEmail:failure" + task.getException());
                            Toast.makeText(getApplicationContext(), "Incorrect E-mail or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
        dialog.dismiss();
    }
}
