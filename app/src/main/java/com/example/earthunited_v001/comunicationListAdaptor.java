package com.example.earthunited_v001;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class comunicationListAdaptor extends RecyclerView.Adapter<comunicationListAdaptor.HolderView> {
    private List<comMessages> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public comunicationListAdaptor(Context context, List<comMessages> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public HolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.communicationlayout, parent, false);
        return new HolderView(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final HolderView holder, int position) {

        for (comMessages OneMessage: mData) {
            holder.mytitle.setText(OneMessage.sTitle);
            holder.myDescription.setText(OneMessage.sMessage);

            holder.myvideoView.setVideoURI(Uri.parse(OneMessage.sLink));
            holder.myvideoView.seekTo(1000);
            //holder.myvideoView.seekTo(50000);
            // create an object of media controller
            //MediaController mediaController = new MediaController(mInflater.getContext());
            holder.myvideoView.setId(position);

            //holder.myPause.setId(Integer.parseInt("Pause" + String.valueOf(position)));
            //holder.myPlay.setId(Integer.parseInt("Play" + String.valueOf(position)));

            holder.myPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.myvideoView.pause();
                }
            });


            holder.myPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.myvideoView.start();
                }
            });



// initiate a video view
            //VideoView simpleVideoView = (VideoView) root.findViewById(R.id.videoView);
// set media controller object for a video view
            //mediaController.setVisibility(View.GONE);
            //holder.myvideoView.setMediaController(mediaController);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class HolderView extends RecyclerView.ViewHolder implements View.OnClickListener {
        VideoView myvideoView;
        TextView mytitle;
        TextView myDescription;

        ImageButton myPause;
        ImageButton myPlay;

        HolderView(View itemView) {
            super(itemView);
            myvideoView = itemView.findViewById(R.id.videoView);

            mytitle = itemView.findViewById(R.id.comtitle);

            myDescription = itemView.findViewById(R.id.description);

            myPause = itemView.findViewById(R.id.pause);
            myPlay = itemView.findViewById(R.id.play);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        //return mData.get(id);
        return null;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
