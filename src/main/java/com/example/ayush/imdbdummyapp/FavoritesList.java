package com.example.ayush.imdbdummyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by admin on 12/8/2017.
 */

public class FavoritesList extends AppCompatActivity{
    Database database;
    CustomAdapter customAdapter;
    ArrayList<Model> completelist;
    ListView listView;
    Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(this);
        intent = getIntent();
        listView = (ListView) findViewById(R.id.listview);
        int i= intent.getIntExtra("fav",3);
        //checks if activity required is favourites or watchlist and sets the title and data accordingly
        if(i==1){
            completelist = database.getFav();
            customAdapter=new CustomAdapter(this,completelist);
            this.setTitle("Favorites");
            listView.setAdapter(customAdapter);
        }
        if(i==2){
            completelist = database.getWatchList();
            this.setTitle("WatchList");
            customAdapter=new CustomAdapter(this,completelist);
            listView.setAdapter(customAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("getid", completelist.get(position).getId());
                Intent intent = new Intent(FavoritesList.this, MainActivity2.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //redirects the user to the activity that contains info about the movie
            }
        });


    }
}