package com.example.tvguide.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.tvguide.Constants;
import com.example.tvguide.HomePage.CropFragment;
import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, CropFragment.OnFragmentInteractionListener {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private ImageView img;
    private Uri contentURI;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        img = findViewById(R.id.selectPhoto);

    }

    @Override
    public void onClick(View view) {
        hideKeyboard(view);
        final TextInputLayout firstName = findViewById(R.id.firstName);
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
                return;
            }
            if (lName.isEmpty()) {
                lastName.getEditText().requestFocus();
                lastName.getEditText().setError("Cannot be empty!");
                return;
            }
            if (mail.isEmpty()) {
                email.getEditText().requestFocus();
                email.getEditText().setError("Cannot be empty!");
                return;
            }
            if (pass.isEmpty()) {
                password.getEditText().requestFocus();
                password.getEditText().setError("Cannot be empty!");
                return;
            }
            if (pass.length() < 6) {
                password.getEditText().requestFocus();
                password.getEditText().setError("At least 6 characters!");
                return;
            }
            if (!pass.equals(passRe)) {
                rePass.getEditText().setError("Passwords doesn't match!");
                rePass.getEditText().requestFocus();
                return;
            }
        }else {
            final ScrollView reg = findViewById(R.id.scrollView);
            final LinearLayout con = findViewById(R.id.continue_reg);
            final View v = view;
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            db = FirebaseFirestore.getInstance();
            final Map<String, String> toAdd = new HashMap<>();
            toAdd.put(Constants.FIRST_NAME.toString(), fName);
            toAdd.put(Constants.LAST_NAME.toString(), lName);
            toAdd.put(Constants.EMAIL.toString(), mail);
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                db.collection("users")
                                        .add(toAdd)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                db.collection("tracking")
                                                        .document(mail).set(new HashMap<Integer, ArrayList<HashMap<String, String>>>());
                                                dialog.dismiss();
                                                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                //startActivity(intent);
                                                reg.setVisibility(View.INVISIBLE);
                                                con.setVisibility(View.VISIBLE);
                                                Snackbar.make(v, "User Registered Successfuly", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(v, "Error Occurred", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                System.out.println("createUserWithEmail:failure" + task.getException());
                                dialog.dismiss();
                                Snackbar.make(v, "This email is already in use",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void selectPhoto(View v){
        requestMultiplePermissions();
        choosePhotoFromGallary();
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 1);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }
    public void showCropFragment(Uri uri){
        CropFragment fragment = CropFragment.newInstance(uri.toString(), false);
        fragment.show(getSupportFragmentManager(), "Crop");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == 1) {
            if (data != null) {
                contentURI = data.getData();
                flag = true;
                showCropFragment(contentURI);


            }

        } else if (requestCode == 0) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(thumbnail);
            flag = true;
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    public void uploadPhoto(Bitmap bitmap){
        if(flag) {
            String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageReference.child("images/" + mail + "/profile.jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(), "Failed " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Uploaded", Snackbar.LENGTH_SHORT).show();
                    flag = false;
                    Button btn = findViewById(R.id.skip);
                    btn.setText("Done");
                    dialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                    int currentprogress = (int) progress;
                    dialog.setMessage("Uploading.. " + currentprogress + " %");
                }
            });
        }else {
            Intent intent = new Intent(getApplicationContext(), RatingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }



    @Override
    public void onFragmentInteraction(Bitmap bitmap, boolean isCover) {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Uploading..");
        dialog.show();
        uploadPhoto(bitmap);
    }
}
