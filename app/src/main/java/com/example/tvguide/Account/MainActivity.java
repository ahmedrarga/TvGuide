package com.example.tvguide.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private ImageView icon;
    private Button createAccount;
    private TextInputLayout email;
    private TextInputLayout pass;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);
        icon = findViewById(R.id.app_icon);
        //icon.setImageResource(R.mipmap.ic_launcher_main);
        Picasso.get()
                .load(R.mipmap.icon_full)
                .into(icon);
        createAccount = findViewById(R.id.create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.forgot).getVisibility() == View.VISIBLE){
            findViewById(R.id.forgot).setVisibility(View.GONE);
            findViewById(R.id.login).setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onStart();
    }

    public void signInClicked(View view) {
        hideKeyboard(view);
        email = findViewById(R.id.email_feild);
        pass = findViewById(R.id.pass_field);
        String mail = email.getEditText().getText().toString();
        final String password = pass.getEditText().getText().toString();
        if(mail.isEmpty() || password.isEmpty()) {
            if (mail.isEmpty()) {
                email.getEditText().setError("E-mail required");
                return;
            }
            if (password.isEmpty()) {
                pass.getEditText().setError("Password required");
                return;
            }
        }else {
            final View v = view;
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            mAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                dialog.dismiss();
                                System.out.println("signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                System.out.println("signInWithEmail:failure" + task.getException());
                                dialog.dismiss();
                                Snackbar.make(v, "Password incorrect", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
    public void forgotPage(View v){
        findViewById(R.id.login).setVisibility(View.INVISIBLE);
        findViewById(R.id.forgot).setVisibility(View.VISIBLE);
    }
    public void forgot_clicked(View view){
        TextInputLayout EmailAddress = findViewById(R.id.emailaddressreset);
        final String mail = EmailAddress.getEditText().getText().toString();
        hideKeyboard(view);
        if(mail.isEmpty()){
            EmailAddress.getEditText().setError("Cannot be empty");
        }else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Submitting..");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            System.out.println(mail);
            final View v = view;
            mAuth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Snackbar.make(v, "Open your email to reset password", Snackbar.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Snackbar.make(v, "This email doesn't exist", Snackbar.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
}
