package com.example.tvguide;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wanderingcan.persistentsearch.PersistentSearchView;
import com.wanderingcan.persistentsearch.drawables.DrawerArrowDrawable;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements PersistentSearchView.OnSearchListener, PersistentSearchView.OnIconClickListener,
        NavigationView.OnNavigationItemSelectedListener, Feeds.OnFragmentInteractionListener, News.OnFragmentInteractionListener {
    private final String TAG = "HomeActivity";
    private static final int VOICE_RECOGNITION_CODE = 9999;

    private boolean mMicEnabled;


    private Handler handler;
    private RecyclerView rView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<Movie> movies;
    private PersistentSearchView mSearchView;
    private DrawerArrowDrawable mArrowDrawable;
    private DrawerLayout mDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPAger);
            final FragmentAdapter adapter = new FragmentAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            rView = findViewById(R.id.rView);
            mArrowDrawable = new DrawerArrowDrawable(this);

            mDrawer = findViewById(R.id.drawer_layout);
            mDrawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    mArrowDrawable.setPosition(slideOffset);
                }
            });
            mMicEnabled = isIntentAvailable(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));


            mSearchView = findViewById(R.id.search_bar);
            mSearchView.setNavigationDrawable(mArrowDrawable);
            mSearchView.setOnSearchListener(this);
            mSearchView.setOnIconClickListener(this);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).setChecked(true));

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String m = user.getEmail();
                    System.out.println(m);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .whereEqualTo("EMAIL", m)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            TextView name = findViewById(R.id.name);
                                            TextView mail = findViewById(R.id.email_feild);
                                            String n = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                            name.setText(n);
                                            mail.setText(document.getData().get("EMAIL").toString());
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                    StorageReference pRef = ref.child("images/" + m + "/profile.jpg");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    pRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            LinearLayout im = findViewById(R.id.p_back);
                            Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            im.setBackground(image);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Error getting documents: " + e.getMessage());

                        }
                    });
                }
            });

        }
    }



    private void getMovies(String query) {
        Requests requests = new Requests(query);
        movies = requests.getMovies();
        rView.setAdapter(new SearchResultsRecyclerAdapter(movies, getApplicationContext(), "Home"));
        rView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
    }

    @Override
    public void onBackPressed() {
        if(mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSearchOpened() {
        mArrowDrawable.toggle();
    }

    @Override
    public void onSearchClosed() {
        mArrowDrawable.toggle();
    }

    @Override
    public void onSearchCleared() {
        rView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSearchTermChanged(CharSequence term) {
        final String query = term.toString();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMovies(query);
                rView.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    @Override
    public void onSearch(CharSequence text) {
        mArrowDrawable.toggle();
        getMovies(text.toString());
        rView.setVisibility(View.VISIBLE);
    }
    @Override
    public void OnNavigationIconClick() {
        if(mSearchView.isSearchOpen()){
            mSearchView.closeSearch();
        }else{
            mDrawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void OnEndIconClick() {
        startVoiceRecognition();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mSearchView.populateSearchText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startVoiceRecognition() {
        if (mMicEnabled) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    this.getString(R.string.speak_now));
            startActivityForResult(intent, VOICE_RECOGNITION_CODE);
        }
    }

    private boolean isIntentAvailable(Intent intent) {
        PackageManager mgr = getPackageManager();
        if (mgr != null) {
            List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        return false;
    }
    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void passreset() {
        Intent intent = new Intent(getApplicationContext(), resetPassword.class);
        startActivity(intent);
    }
    public void profile_clicked(View v){
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        System.out.println(id);
        if (id == R.id.nav_home) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        } else if(id == R.id.watchlist){
            Intent intent = new Intent(getApplicationContext(), WatchlistActivity.class);
            startActivity(intent);

        }  else if (id == R.id.Logout) {
            signOut();
        } else if (id == R.id.ResetPassword) {
            passreset();
        }
        return true;
        /* DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.animate();
        drawer.closeDrawer(GravityCompat.START);*/
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
