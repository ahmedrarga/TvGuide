package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FloatingSearchView searchView;
    List<SearchSuggestion> movies;
    RecyclerView rView;
    private Handler handler;
    private TextInputLayout CurrentPassword;
    private TextInputLayout NewPass;
    private TextInputLayout ConfirmPass;
    private ConstraintLayout MainHomePage;
    private ConstraintLayout ResetPasswordPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }///
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        searchView = findViewById(R.id.searchView);
        rView = findViewById(R.id.moviesLst);
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(final String oldQuery, final String newQuery) {
                if (newQuery.endsWith(" ")) {
                    handler = new Handler();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getSuggestions(oldQuery, newQuery);
                        }
                    }, 500);
                }
            }
        });
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Intent intent = new Intent(getApplicationContext(), MovieProfileActivity.class);
                int id = ((MovieSuggestion)searchSuggestion).getId();
                String media_type = (((MovieSuggestion)searchSuggestion).getMedia_type());
                intent.putExtra("id", id);
                intent.putExtra("media_type", media_type);
                startActivity(intent);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                Requests requests = new Requests(currentQuery);
                rView.setVisibility(View.VISIBLE);
                rView.setAdapter(new SearchResultsRecyclerAdapter(requests.getMovies(), getApplicationContext()));
                rView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }
        });
        MainHomePage = findViewById(R.id.MainHomePage);
        ResetPasswordPage = findViewById(R.id.ResetPasswordPage);

        MainHomePage.setVisibility(View.VISIBLE);
        ResetPasswordPage.setVisibility(View.INVISIBLE);

        /*
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        final LinearLayout left_drawer = findViewById(R.id.left_drawer);
        left_drawer.setVisibility(View.INVISIBLE);
        searchView.attachNavigationDrawerToMenuButton(drawerLayout);
        searchView.setCloseSearchOnKeyboardDismiss(true);
        searchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                left_drawer.setVisibility(View.VISIBLE);
                left_drawer.setGravity(Gravity.RIGHT);
                searchView.setLeftMenuOpen(true);

            }

            @Override
            public void onMenuClosed() {
                drawerLayout.closeDrawer(left_drawer);
            }
        });
*/

    }
    private void getSuggestions(String oldQuery, String newQuery){
        rView.setVisibility(View.INVISIBLE);
        if (!oldQuery.equals("") && newQuery.equals("")) {
            searchView.clearSuggestions();
        } else {
            searchView.showProgress();
            Requests requests = new Requests(newQuery);
            movies = requests.getSuggestions();
            searchView.swapSuggestions(movies);
            searchView.hideProgress();
        }
    }
    @Override
    protected void onStart() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onStart();
    }

    public void signOut(View view){
        mAuth.signOut();
        user = null;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void resetpassword(View view) {
        CurrentPassword = findViewById(R.id.currpass);
        NewPass = findViewById(R.id.NewPass);
        ConfirmPass = findViewById(R.id.NewPassConf);
        String Emailtocheck = user.getEmail();
        String Currpass = CurrentPassword.getEditText().getText().toString();
        final String newpass = NewPass.getEditText().getText().toString();
        final String newpassconfirm = ConfirmPass.getEditText().getText().toString();

        if (Currpass.isEmpty() || newpass.isEmpty() || newpassconfirm.isEmpty()) {
            if (Currpass.isEmpty()) {
                CurrentPassword.getEditText().setError("Current Password required");
                CurrentPassword.getEditText().requestFocus();
            }
            if (newpass.isEmpty()) {
                NewPass.getEditText().setError("New Password required");
                NewPass.getEditText().requestFocus();
            }
            if (newpassconfirm.isEmpty()) {
                ConfirmPass.getEditText().setError("Repeated Password required");
                ConfirmPass.getEditText().requestFocus();
            }
        } else {

            mAuth.signInWithEmailAndPassword(Emailtocheck, Currpass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful() && newpass.equals(newpassconfirm)) {
                                // Sign in success, update UI with the signed-in user's information
                                System.out.println("signInWithEmail:success");
                                user.updatePassword(newpass);
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                System.out.println("Error With Information:failure" + task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
    public void passreset(View view)
    {
        MainHomePage.setVisibility(View.INVISIBLE);
        ResetPasswordPage.setVisibility(View.VISIBLE);
    }
}
