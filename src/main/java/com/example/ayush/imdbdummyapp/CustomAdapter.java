package com.example.ayush.imdbdummyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by admin on 12/7/2017.
 */

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Model> modelArrayList;
    LayoutInflater inflater;

    //parameterized constructor
    public  CustomAdapter(Context context, ArrayList<Model> modelArrayList ){
        this.context = context;
        this.modelArrayList = modelArrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflating item_row XML
        convertView = inflater.inflate(R.layout.row,null);
        // creating textViews and setting Text to it
        TextView title = (TextView)convertView.findViewById(R.id.title);
        title.setText(modelArrayList.get(position).getTitle());
        TextView release_date = (TextView)convertView.findViewById(R.id.ReleaseDate);
        release_date.setText("Release Date: "+modelArrayList.get(position).getRelease_date());

        TextView popularity = (TextView)convertView.findViewById(R.id.popularity);
        popularity.setText("Popularity: ");

        TextView Votecount =(TextView)convertView.findViewById(R.id.votes);
        Votecount.setText("("+modelArrayList.get(position).getVote_average()+"/10) voted by "+modelArrayList.get(position).getVote_count()+" users");

        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
        ratingBar.setRating(Float.parseFloat(modelArrayList.get(position).getPopularity()) / 50);
        RatingBar ratingBar1 = (RatingBar) convertView.findViewById(R.id.rating1);
        ratingBar1.setRating(Float.parseFloat(modelArrayList.get(position).getVote_average()) / 10);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);
        new DownloadImageTask(imageView) .execute("http://image.tmdb.org/t/p/w500"+modelArrayList.get(position).getPoster_path());
        //adds image to the imageView using AsyncTask
        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        //downloads image from the Net and adds it to the imageView given
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
