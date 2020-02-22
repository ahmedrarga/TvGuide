package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class resetPassword extends BaseActivity {
    private TextInputLayout CurrentPassword;
    private TextInputLayout NewPass;
    private TextInputLayout ConfirmPass;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initToolbar("Reset password");


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void resetpassword(View view) {
        hideKeyboard(view);
        CurrentPassword = findViewById(R.id.currpass);
        NewPass = findViewById(R.id.NewPass);
        ConfirmPass = findViewById(R.id.NewPassConf);
        String Emailtocheck = mAuth.getCurrentUser().getEmail();
        String Currpass = CurrentPassword.getEditText().getText().toString();
        final String newpass = NewPass.getEditText().getText().toString();
        final String newpassconfirm = ConfirmPass.getEditText().getText().toString();

        if (Currpass.isEmpty() || newpass.isEmpty() || newpassconfirm.isEmpty()) {
            if (Currpass.isEmpty()) {
                CurrentPassword.getEditText().setError("Current Password required");
                CurrentPassword.getEditText().requestFocus();
                return;
            }
            if (newpass.isEmpty()) {
                NewPass.getEditText().setError("New password required");
                NewPass.getEditText().requestFocus();
                return;
            }
            if (newpassconfirm.isEmpty()) {
                ConfirmPass.getEditText().setError("Confirm password required");
                ConfirmPass.getEditText().requestFocus();
                return;
            }
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Updating..");
            dialog.show();
            final View v = view;
            mAuth.signInWithEmailAndPassword(Emailtocheck, Currpass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && newpass.equals(newpassconfirm)) {
                                // Sign in success, update UI with the signed-in user's information
                                System.out.println("signInWithEmail:success");
                                mAuth.getCurrentUser().updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            CurrentPassword.getEditText().setText("");
                                            NewPass.getEditText().setText("");
                                            ConfirmPass.getEditText().setText("");
                                            dialog.dismiss();
                                            Snackbar.make(v, "Password changed", Snackbar.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Snackbar.make(v, "Error occurred!", Snackbar.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                System.out.println("Error With Information:failure" + task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

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
