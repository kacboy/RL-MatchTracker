package com.rlmatchtracker.interface2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> mStats;
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> stats){
        mContext = context;
        mStats = stats;
        String DEBUG_TAG = "MainActivity";
        Log.d(DEBUG_TAG,"RecyclerViewAdapter: called");
    }

    @Override
    //responsble for infalting view
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        String DEBUG_TAG = "MainActivity";
        Log.d(DEBUG_TAG,"onCreateViewHolder: called");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    //changed based on layout and what i want
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String DEBUG_TAG = "MainActivity";
        Log.d(DEBUG_TAG,"onBindViewHolder: called");
        //Log.d(TAG,"onBindViewHolder: called."); //called based on how many items are called
        //sensor lists are made HERE
        String[] results = (mStats.get(position)).split(" ");

        //image if scoreboard only
        decodeBase64AndSetImage(results[Constants.ID_IMAGE], holder.scoreboardView);
        //puts the text into the recyclerview
        String winTest = "1";
        String lossTest = "2";
        //test win or loss or image
        if (results[Constants.ID_WL].toLowerCase().contains(winTest.toLowerCase())) {
            holder.textViewWinLoss.setText("  Win");
            holder.textViewWinLoss.setTextColor(Color.parseColor("#42B200"));
        } else if (results[Constants.ID_WL].toLowerCase().contains(lossTest.toLowerCase())){
            holder.textViewWinLoss.setText("Loss");
            holder.textViewWinLoss.setTextColor(Color.parseColor("#FF5959"));
        } else {
            holder.textViewWinLoss.setText("Not inputted");
        }
        //test mode
        if (results[Constants.ID_MODE].equals("0")) {
            holder.textViewMode.setText("Standard");
        } else if (results[Constants.ID_MODE].equals("1")){
            holder.textViewMode.setText("Doubles");
        } else if (results[Constants.ID_MODE].equals("2")){
            holder.textViewMode.setText("Duel");
        } else {
            holder.textViewMode.setText("");
        }
        //test score - hides score and time
        if (results[Constants.ID_TEAM].equals(results[Constants.ID_OPP])) {
            holder.textViewScore.setText("");
            holder.textViewTime.setText("");
        } else {
            holder.textViewScore.setText(results[Constants.ID_TEAM]+"-"+results[Constants.ID_OPP]);
            holder.textViewTime.setText(results[Constants.ID_TIME].replace("_"," "));
        }
    }

    @Override
    public int getItemCount() {
        String DEBUG_TAG = "MatchListActivity";
        Log.d(DEBUG_TAG,"getItemCount: called");
        return mStats.size();
    }

    public void setStats(ArrayList<String> stats){
        mStats = stats;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView scoreboardView;
        public TextView textViewWinLoss;
        public TextView textViewMode;
        public TextView textViewScore;
        public TextView textViewTime;

        RelativeLayout parent_layout;
        public ViewHolder(View itemView){
            super(itemView);
            //get context of specific listed item
            itemView.setOnClickListener(this);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            scoreboardView = itemView.findViewById(R.id.scoreboardView);
            textViewWinLoss = itemView.findViewById(R.id.textViewWinLoss);
            textViewMode = itemView.findViewById(R.id.textViewMode);
            textViewScore = itemView.findViewById(R.id.textViewScore);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        @Override
        public void onClick(View v) {
            //get index position of clicked item
            int index = this.getAdapterPosition();
//            String id = db.get(index).getId();
//            String results = (mStats.get(index).toString());
            String[] results = (mStats.get(index).toString()).split(" ");

            for (String result: results) {
                Log.d("DEBUG_TAG","result: "+result+index);
            }
            //test score - tell whether clicked match is inputted or not by testing the row string length
            if (results[2].equals("3")) {
                Intent i = new Intent(mContext, ImageInputActivity.class);
                i.putExtra("currentMatchIndex", index);
                mContext.startActivity(i);
            } else {
                Intent i = new Intent(mContext, MatchViewActivity.class);
                i.putExtra("currentMatchIndex", index);
                mContext.startActivity(i);
            }
        }
    }

    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {
        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(stream),250, 250);
        imageView.setImageBitmap(bitmap);
    }
}
