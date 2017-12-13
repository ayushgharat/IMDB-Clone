package com.example.ayush.imdbdummyapp;

import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Datalistener {
    ListView listView;
    ArrayList<Model> modelArrayList;
    CustomAdapter customAdapter;
    String url;
    Database database;
    LinearLayout linearLayout;
    int flag ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(MainActivity.this);
        //sets logo for the Action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        modelArrayList = new ArrayList<>();
        url = "http://api.themoviedb.org/3/movie/upcoming?api_key=8496be0b2149805afa458ab8ec27560c";
        listView = (ListView) findViewById(R.id.listview);
        checkConnection();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("getid", modelArrayList.get(position).getId());
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //on click of a particular item, a new activity starts which recieves the id of the specific list item chosen
            }
        });
    }

    public void checkConnection() {
        if (isConnectedToInternet()) {
            //calling Async Task
            DataProcess dataProcess = new DataProcess(MainActivity.this, url, this,linearLayout);
            dataProcess.execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Error: No Internet");
            builder.setMessage("No internet available. Pleae switch on Wi-fi to continue");
            builder.setNegativeButton("Turn on Wi-Fi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                }
            }).show();
            //creates a dialog which takes you to the settings of your phone to switch on wifi

        }

        //custom adapter for displaying listitems
        customAdapter = new CustomAdapter(this, modelArrayList);
        //setting adapter to listview
        listView.setAdapter(customAdapter);

    }

    private boolean isConnectedToInternet() {
        boolean isconnected = false;
        //connectivity managee for checking the internet connection of the device
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            isconnected = true;
        }
        return isconnected;
    }

    @Override
    public void updatelist(String data) {
        try {
            //creating jsonObject
            JSONObject jsonObject = new JSONObject(data);
            //getting array from the json object
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                //getting object i from the JsonArray
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                //getting data from i object in the array
                String title = jsonObject1.getString("title");
                String release_date = jsonObject1.getString("release_date");
                String popularity = jsonObject1.getString("popularity");
                String vote_count = jsonObject1.getString("vote_count");
                String vote_average = jsonObject1.getString("vote_average");
                String poster_path = jsonObject1.getString("poster_path");
                String id = jsonObject1.getString("id");
                Model model = new Model();
                //setting name, votes and id
                model.setTitle(title);
                model.setRelease_date(release_date);
                model.setPopularity(popularity);
                model.setVote_count(vote_count);
                model.setVote_average(vote_average);
                model.setPoster_path(poster_path);
                model.setId(id);
                //adding model to mmodelArrayList
                modelArrayList.add(model);
            }
            customAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
        //inflates Options menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mostpop:
                Mostpop();
                break;
            case R.id.UpcomingMovies:
                upcoming();
                break;
            case R.id.toprated:
                topRated();
                break;
            case R.id.NowPlaying:
                nowPlaying();
                break;
            case R.id.Fav:
                Intent favintent = new Intent(MainActivity.this, FavoritesList.class);
                flag=1;
                favintent.putExtra("fav",flag);
                startActivity(favintent);
                //opens the favouritsList activity, which is also used to display the watchlist
                break;
            case R.id.watchlist:
                Intent watchintent = new Intent(MainActivity.this,FavoritesList.class);
                flag = 2;
                watchintent.putExtra("fav",flag);
                startActivity(watchintent);
                break;
            case R.id.refresh:
                String title = this.getTitle().toString();
                checkConnection();
                //refreshes the page
                switch (title) {
                    case "Most Popular":
                        Mostpop();
                        break;
                    case "Nowplaying":
                        nowPlaying();
                        break;
                    case "Upcoming":
                        upcoming();
                        break;
                    case "Top Rated":
                        topRated();
                        break;

                    default:

                }
        }
        return super.onOptionsItemSelected(item);
    }

    void topRated() {
        url = "http://api.themoviedb.org/3/movie/top_rated?api_key=f47dd4de64c6ef630c2b0d50a087cc33";
        modelArrayList.clear();
        this.setTitle("Top Rated");
        checkConnection();
        //the specific url is given and the title is changed in case of the following
    }

    void upcoming() {
        url = "http://api.themoviedb.org/3/movie/upcoming?api_key=8496be0b2149805afa458ab8ec27560c";
        modelArrayList.clear();
        this.setTitle("Upcoming");
        checkConnection();
    }

    void Mostpop() {
        url = "http://api.themoviedb.org/3/movie/popular?api_key=f47dd4de64c6ef630c2b0d50a087cc33";
        modelArrayList.clear();
        this.setTitle("Most Popular");
        checkConnection();
    }

    void nowPlaying() {
        url = "http://api.themoviedb.org/3/movie/now_playing?api_key=8496be0b2149805afa458ab8ec27560c";
        modelArrayList.clear();
        this.setTitle("Nowplaying");
        checkConnection();
    }

}
