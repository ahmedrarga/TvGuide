package com.example.tvguide.HomePage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.tvguide.NewPostActivity;
import com.example.tvguide.Account.SettingsActivity;
import com.example.tvguide.RecommenderSystem.recommenderActivity;
import com.example.tvguide.User.DiscoverActivity;
import com.example.tvguide.Account.MainActivity;
import com.example.tvguide.User.ProfileActivity;
import com.example.tvguide.R;
import com.example.tvguide.User.WatchlistActivity;
import com.example.tvguide.Account.resetPassword;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.wanderingcan.persistentsearch.PersistentSearchView;
import com.wanderingcan.persistentsearch.drawables.DrawerArrowDrawable;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements PersistentSearchView.OnSearchListener, PersistentSearchView.OnIconClickListener,
        NavigationView.OnNavigationItemSelectedListener, Feeds.OnFragmentInteractionListener, News.OnFragmentInteractionListener {
    private final String TAG = "HomeActivity";
    private static final int VOICE_RECOGNITION_CODE = 9999;
    boolean flag = true;
    private boolean connected = false;

    private boolean mMicEnabled;
    public static final String api_key = "f98d888dd7ebd466329c6a26f1018a55";


    private Handler handler;
    private RecyclerView rView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<Movie> movies;
    private PersistentSearchView mSearchView;
    private DrawerArrowDrawable mArrowDrawable;
    private DrawerLayout mDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Snackbar snackbar;
    public static TextView name;
    public static  TextView email;
    public static ImageView profile;
    public static ImageView cover;
    ProgressBar searchProg;
    LinearLayout search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            cover = findViewById(R.id.cover_profile);
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPAger);
            final FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
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
            search = findViewById(R.id.search);
            searchProg = findViewById(R.id.searchProg);
            rView.setAdapter(new SearchResultsRecyclerAdapter(movies, getApplicationContext(), "Home"));

            mArrowDrawable = new DrawerArrowDrawable(this);

            mDrawer = findViewById(R.id.drawer_layout);
            mDrawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    mArrowDrawable.setPosition(slideOffset);
                }
            });
            mMicEnabled = isIntentAvailable(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
            name = findViewById(R.id.name);
            email = findViewById(R.id.email_feild);
            profile = findViewById(R.id.back_profile);

            mSearchView = findViewById(R.id.search_bar);
            mSearchView.setNavigationDrawable(mArrowDrawable);
            mSearchView.setOnSearchListener(this);
            mSearchView.setOnIconClickListener(this);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).setChecked(true));
            Handler handler = new Handler(getMainLooper());
            handler.postDelayed(new Runnable() {
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
                                             name = findViewById(R.id.name);
                                             email = findViewById(R.id.email_feild);
                                            String n = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                            name.setText(n);
                                            email.setText(document.getData().get("EMAIL").toString());
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                    StorageReference pRef = ref.child("images/" + m + "/profile.jpg");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                profile = findViewById(R.id.back_profile);
                                Picasso.get()
                                        .load(task.getResult())
                                        .fit()
                                        .into(profile);
                            }else{
                                profile = findViewById(R.id.back_profile);
                                profile.setImageResource(R.drawable.ic_person);
                            }
                        }
                    });
                    pRef = ref.child("images/" + m + "/cover.jpg");
                    pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                cover = findViewById(R.id.cover_profile);
                                Picasso.get()
                                        .load(task.getResult())
                                        .fit()
                                        .into(cover);
                            }else{
                                cover = findViewById(R.id.cover_profile);
                                cover.setImageResource(R.drawable.ic_full);
                            }
                        }
                    });
                }
            }, 500);


        }
    }


    @Override
    public void onBackPressed() {
        if(mSearchView.isSearchOpen() || flag) {
            search.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);

        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSearchOpened() {
        mArrowDrawable.toggle();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        flag = false;
    }

    @Override
    public void onSearchClosed() {
        mArrowDrawable.toggle();
        if(!flag) {
            search.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchCleared() {
        rView.setVisibility(View.GONE);
        flag = true;
    }

    @Override
    public void onSearchTermChanged(CharSequence term) {
        final String query = term.toString();
        searchProg.setVisibility(View.VISIBLE);
        rView.setVisibility(View.GONE);
        new task().execute(query);



    }

    @Override
    public void onSearch(CharSequence text) {
        String q = text.toString();
        searchProg.setVisibility(View.VISIBLE);
        rView.setVisibility(View.GONE);
        new task().execute(q);
        flag = true;


    }
    @Override
    public void OnNavigationIconClick() {
        if(mSearchView.isSearchOpen()){
            mSearchView.closeSearch();
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
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
            mSearchView.openSearch();
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

        }  else if(id == R.id.Discover){
            Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.new_post){
            Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);
            startActivity(intent);
        } else if(id == R.id.setting){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.recommendations_page){
            Intent intent = new Intent(getApplicationContext(), recommenderActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.Logout) {
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

    public class task extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            try{
                Thread.sleep(1000);
            }catch (InterruptedException w){

            }
            String query = "https://api.themoviedb.org/3/search/multi?" +
                    "api_key=" + api_key +
                    "&language=en-US&" +
                    "query=" + strings[0] +
                    "&page=1";
            Response response = setResponse(query);
            List<Movie> movies = new ArrayList<>();
            if(response != null && response.code() == 200){
                try {
                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        if (!obj.getString("media_type").equals("person")) {
                            movies.add(new Movie(obj));
                        }
                    }
                }catch (IOException e1){

                } catch (JSONException e2){

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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            rView.setVisibility(View.GONE);
            searchProg.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            ((SearchResultsRecyclerAdapter)rView.getAdapter()).updateData(movies);
            rView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            rView.setVisibility(View.VISIBLE);
            searchProg.setVisibility(View.GONE);
        }
    }
}
