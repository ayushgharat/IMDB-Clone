package com.example.ayush.imdbdummyapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity2 extends AppCompatActivity implements Datalistener {
    ImageView imageView;
    TextView title, description, tagline, releasedate, budget, revenue, status, vote_average;
    ArrayList arrayList;
    LinearLayout linearLayout;
    ImageView favimage, watchimage;
    String url;
    DetailsModel model;
    Database database;
    String trailer_url;
    String poster_url;
    String id;
    RatingBar ratingBar, ratingBar2;
    String cast_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Fresco.initialize(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        database = new Database(MainActivity2.this);
        arrayList = new ArrayList<DetailsModel>();

        //recieves the id from the MainActivity
        id = bundle.getString("getid");
        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar3);
        imageView = (ImageView) findViewById(R.id.poster_detail);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        tagline = (TextView) findViewById(R.id.tag_line);
        releasedate = (TextView) findViewById(R.id.r_date);
        budget = (TextView) findViewById(R.id.budget);
        revenue = (TextView) findViewById(R.id.revenue);
        status = (TextView) findViewById(R.id.status);
        watchimage = (ImageView) findViewById(R.id.watch);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutdetails);
        vote_average = (TextView) findViewById(R.id.vote_average);
        favimage = (ImageView) findViewById(R.id.favorite);
        url = "http://api.themoviedb.org/3/movie/" + id + "?api_key=8496be0b2149805afa458ab8ec27560c";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //enables easy navigation to the home page
        checkConnection();
        showCast();
        showCrew();
        showPosters();
        showTrailers();
        //gets information from the api, including information about cast, crew as well as posters and trailers
        favimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = favimage.getTag();
                if (tag == "disable") {
                    favimage.setImageResource(R.drawable.favorite_enable_normal);
                    favimage.setTag("enable");
                    //ensures that movie is enabled as a favourite and changes the picture
                    model.setIsFav("1");
                    Database db = new Database(MainActivity2.this);
                    boolean check = db.checkMovie(id);

                    //checks if movie already exists in database
                    if (check)
                        db.updateMovieF(id,"1");
                    //if movie already exists in the database
                    else
                        db.addMovie(model);
                    //adds movie to the database
                } else {
                    //disables the movie as a favourite and changees the picture
                    favimage.setImageResource(R.drawable.favorite_disable_normal);
                    favimage.setTag("disable");
                    model.setIsFav("0");
                    Database db = new Database(MainActivity2.this);
                    boolean check = db.checkMovie(id);

                    //checks if movie already exists in database
                    if (check)
                        db.updateMovieF(id,"0");
                    //if movie already exists in the watchlist...
                    else
                        db.addMovie(model);
                    //adds movie to the database
                }


            }
        });
        watchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = watchimage.getTag();
                if (tag == "disable") {
                    //if movie is added to watchlist, then image is changed and movie is updated to the watchlist
                    watchimage.setImageResource(R.drawable.watchlist_enable_normal);
                    watchimage.setTag("enable");
                    model.setIsWatchlist("1");
                    Database db = new Database(MainActivity2.this);
                    boolean check = db.checkMovie(id);
                    //checks if movie already exists in database
                    if (check)
                        db.updateMovieW(id,"1");
                    else
                        db.addMovie(model);
                } else {
                    watchimage.setImageResource(R.drawable.watchlist_disable_normal);
                    watchimage.setTag("disable");
                    model.setIsWatchlist("0");
                    Database db = new Database(MainActivity2.this);
                    boolean check = db.checkMovie(id);
                    //checks if movie already exists in database
                    if (check)
                        db.updateMovieW(id,"0");
                    else
                        db.addMovie(model);
                }


            }
        });
    }

    private void checkMovie(String Id) {

        Database db = new Database(MainActivity2.this);
        // db.deleteNonFavWatchMovie();
        Boolean check = db.checkMovie(Id);

        if (!check) {
            //checks if movie does not existing in database
            favimage.setImageResource(R.drawable.favorite_disable_normal);
            favimage.setTag("disable");
            watchimage.setImageResource(R.drawable.watchlist_disable_normal);
            watchimage.setTag("disable");
        } else {
            //if movie does exist
            String fav = db.checkfav(Id);
            String watch = db.checkwatch(Id);
            if (fav.equals("0")) {
                //set image based on database value
                favimage.setImageResource(R.drawable.favorite_disable_normal);
                favimage.setTag("disable");
                model.setIsFav("0");

            } else {
                favimage.setImageResource(R.drawable.favorite_enable_normal);
                favimage.setTag("enable");
                model.setIsFav("1");
            }

            if (watch.equals("0")) {
                //set image based on database value
                watchimage.setImageResource(R.drawable.watchlist_disable_normal);
                watchimage.setTag("disable");
                model.setIsWatchlist("0");
            } else {
                watchimage.setImageResource(R.drawable.watchlist_enable_normal);
                watchimage.setTag("enable");
                model.setIsWatchlist("1");
            }
        }
    }




    public void checkConnection() {
        if (isConnectedToInternet()) {
            //calling Async Task
            DataProcess movieDetails = new DataProcess(MainActivity2.this, url, this,linearLayout);
            movieDetails.execute();
        } else {

        }
    }

    private boolean isConnectedToInternet() {
        boolean isconnected = false;
        //connectivity managee for checking the internet connection of the device
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            isconnected = true;
        }
        return isconnected;
    }
    private void showCast() {

        LinearLayout castsSection = (LinearLayout)findViewById(R.id.casts_section);

        cast_url = "http://api.themoviedb.org/3/movie/"+ id + "/credits?api_key=8496be0b2149805afa458ab8ec27560c";

        try {
            List<Casts> castList = new GetCastCrew(this, "cast",cast_url).execute().get();
            //gets cast using cast_url and returns it to the castList

            if (castList != null && !castList.isEmpty()) {
                castsSection.setVisibility(View.VISIBLE);
                setCasts(castList);
                //creates the linear layout if there is a cast
            } else {
                castsSection.setVisibility(View.GONE);
                //the linear layout will disappear if the movie has no cast
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private void showPosters() {

        LinearLayout castsSection = (LinearLayout)findViewById(R.id.posters_section);
        poster_url = "http://api.themoviedb.org/3/movie/"+ id + "/images?api_key=8496be0b2149805afa458ab8ec27560c";

        try {
            List<Casts> castList = new GetCastCrew(this, "posters",poster_url).execute().get();
// gets posters using the same AsyncTask used to get the cast as well as the crew
            if (castList != null && !castList.isEmpty()) {
                castsSection.setVisibility(View.VISIBLE);
                setPosters(castList);
            } else {
                castsSection.setVisibility(View.GONE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Add multiple casts in the casts container
    private void setPosters(List<Casts> casts) {

        LinearLayout poster_container = (LinearLayout)findViewById(R.id.posters_container);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int size = casts.size();
        //gets the number of cast members
        for (int i = 0; i < size; i++) {
            Casts cast = casts.get(i);

            if (cast != null) {

                LinearLayout clickableColumn = (LinearLayout) inflater.inflate(R.layout.column, null);
                ImageView thumbnailImage = (ImageView) clickableColumn.findViewById(R.id.thumbnail_image);
                TextView titleView = (TextView) clickableColumn.findViewById(R.id.title_view);
                TextView subTitleView = (TextView) clickableColumn.findViewById(R.id.subtitle_view);

                if (cast.getProfilePath().equals("null")) {
                    thumbnailImage.setImageResource(R.drawable.noimageinverted);
                    //sets the image to a default image if there is no poster path given
                } else {
                    Uri uri = Uri.parse("http://image.tmdb.org/t/p/w500" + cast.getProfilePath());
                    Picasso.with(MainActivity2.this).load(uri).into(thumbnailImage);
                    //loads image into the imageView using Picasso
                }

                titleView.setVisibility(View.GONE);
                subTitleView.setVisibility(View.GONE);
            // erases the title and subtitle as they are not needed for the posters

                poster_container.addView(clickableColumn);
                //adds the view to the LinearLayout
            }
        }
    }

    private void setCasts(List<Casts> cast) {

        //the same process is undergone with the cast

        LinearLayout castsContainer = (LinearLayout)findViewById(R.id.casts_container);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        int size = cast.size();
        for (int i = 0; i < size; i++) {
            Casts casts = cast.get(i);

            if (casts != null) {

                LinearLayout clickableColumn = (LinearLayout) inflater.inflate(R.layout.column, null);
                ImageView thumbnailImage = (ImageView) clickableColumn.findViewById(R.id.thumbnail_image);
                TextView titleView = (TextView) clickableColumn.findViewById(R.id.title_view);
                TextView subTitleView = (TextView) clickableColumn.findViewById(R.id.subtitle_view);

                if (casts.getProfilePath().equals("null")) {
                    thumbnailImage.setImageResource(R.drawable.noimageinverted);
                } else {
                    Uri uri = Uri.parse("http://image.tmdb.org/t/p/w500" + casts.getProfilePath());
                    Picasso.with(MainActivity2.this).load(uri).into(thumbnailImage);

                }

                titleView.setText(casts.getName());
                subTitleView.setText(casts.getCharacter());
                //the name and the character portrayed by the actor is set here


                castsContainer.addView(clickableColumn);

            }
        }
    }
    private void showCrew() {

        //same process is undergone

        LinearLayout crewSection = (LinearLayout)findViewById(R.id.casts_section);

        cast_url = "http://api.themoviedb.org/3/movie/"+ id + "/credits?api_key=8496be0b2149805afa458ab8ec27560c";

        try {
            List<Casts> crewList = new GetCastCrew(this, "crew",cast_url).execute().get();

            if (crewList != null && !crewList.isEmpty()) {
                crewSection.setVisibility(View.VISIBLE);
                setCrew(crewList);
            } else {
                crewSection.setVisibility(View.GONE);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private void showTrailers() {

        //recieves the urls for the trailers

        LinearLayout trailer_Section = (LinearLayout)findViewById(R.id.trailers_section);

        trailer_url = "http://api.themoviedb.org/3/movie/"+ id + "/videos?api_key=8496be0b2149805afa458ab8ec27560c";

        try {
            List<Casts> castList = new GetCastCrew(this,"results",trailer_url).execute().get();

            if (castList != null && !castList.isEmpty()) {
                trailer_Section.setVisibility(View.VISIBLE);
                setTrailers(castList);
            } else {
                trailer_Section.setVisibility(View.GONE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void setTrailers(List<Casts> trailers) {


        LinearLayout castsContainer = (LinearLayout)findViewById(R.id.trailers_container);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int size = trailers.size();
        for (int i = 0; i < size; i++) {
            Casts cast = trailers.get(i);

            if (cast != null) {

                LinearLayout clickableColumn = (LinearLayout) inflater.inflate(R.layout.column_trailer, null);
                TextView titleView = (TextView) clickableColumn.findViewById(R.id.trailer_link);

                titleView.setText(cast.getName());
                final String trailer = "https://www.youtube.com/watch?v=" + cast.getKey();
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer)));
                    }
                });
                //creates an intent for each trailer and takes the user to Youtube on click of the text
                castsContainer.addView(clickableColumn);
            }
        }
    }

    // Add multiple casts in the casts container
    private void setCrew(List<Casts> crew) {

        //the same process is undergone here too

        LinearLayout castsContainer = (LinearLayout)findViewById(R.id.crews_container);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        int size = crew.size();
        for (int i = 0; i < size; i++) {
            Casts casts = crew.get(i);

            if (casts != null) {

                LinearLayout clickableColumn = (LinearLayout) inflater.inflate(R.layout.column, null);
                ImageView thumbnailImage = (ImageView) clickableColumn.findViewById(R.id.thumbnail_image);
                TextView titleView = (TextView) clickableColumn.findViewById(R.id.title_view);
                TextView subTitleView = (TextView) clickableColumn.findViewById(R.id.subtitle_view);

                if (casts.getProfilePath().equals("null")) {
                    thumbnailImage.setImageResource(R.drawable.noimageinverted);
                } else {
                    Uri uri = Uri.parse("http://image.tmdb.org/t/p/w500" + casts.getProfilePath());
                    Picasso.with(MainActivity2.this).load(uri).into(thumbnailImage);
                }

                titleView.setText(casts.getName());
                subTitleView.setText(casts.getJob());

                castsContainer.addView(clickableColumn);

            }
        }
    }

    @Override
    public void updatelist(String data) {
        model = new DetailsModel();
        try {
            //gets the information and formats the information before adding them to their respective Textviews and imageViews
            JSONObject jsonObject = new JSONObject(data);
            model.setId(jsonObject.getString("id"));
            model.setTitle(jsonObject.getString("title"));
            model.setRelease_date(jsonObject.getString("release_date"));
            model.setPoster_path(jsonObject.getString("poster_path"));
            model.setVote_average(jsonObject.getString("vote_average"));
            model.setVote_count(jsonObject.getString("vote_count"));
            model.setBudget(jsonObject.getString("budget"));
            model.setRevenue(jsonObject.getString("revenue"));
            model.setTagline(jsonObject.getString("tagline"));
            model.setStatus(jsonObject.getString("status"));
            model.setOverview(jsonObject.getString("overview"));
            model.setPopularity(jsonObject.getString("popularity"));


            title.setText(model.getTitle());
            releasedate.setText("Release Date:" + model.getRelease_date());
            vote_average.setText("(" + model.getVote_average() + "/10)" + "\n" + model.getVote_count() + " users");
            budget.setText("Budget: $" + model.getBudget());
            revenue.setText("Revenue: $" + model.getRevenue());
            tagline.setText("Tagline: " + model.getTagline());
            status.setText("Status: " + model.getStatus());
            description.setText(model.getOverview());
            ratingBar.setRating(Float.parseFloat(model.getPopularity()) / 50);
            ratingBar2.setRating(Float.parseFloat(model.getVote_average())/10);
            if (model.getPoster_path().equals("null")) {
                imageView.setImageResource(R.drawable.noimageinverted);
            } else {
                Uri uri = Uri.parse("http://image.tmdb.org/t/p/w500" + model.getPoster_path());
                Picasso.with(MainActivity2.this).load(uri).into(imageView);
            }

            arrayList.add(model);
            checkMovie(id);
            //checks if movie is present in either the watchlist or as a favourite

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}