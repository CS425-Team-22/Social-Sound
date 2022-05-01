package com.jotreewater.socialsound;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.Viewholder> {
    private Context context;
    private ArrayList<Sound> courseModelArrayList;
    private OnItemClickListener mClickListener;
    private Boolean clickable;
    public static int selectedItem;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Constructor
    public SoundAdapter(Context context, ArrayList<Sound> courseModelArrayList, Boolean clickable) {
        this.context = context;
        this.courseModelArrayList = courseModelArrayList;
        this.clickable = clickable;
        selectedItem = -1;
    }

    @NonNull
    @Override
    public SoundAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_card, parent, false);
        return new Viewholder(view, mClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void getImage(ImageUri trackImage, ImageView iview, Viewholder holder) {
        MainActivity.mainSpotifyAppRemote.getImagesApi().getImage(trackImage).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
            @Override
            public void onResult(Bitmap data) {
                iview.setImageBitmap(data);
                holder.imageViewTrackImage2.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull SoundAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        Sound model = courseModelArrayList.get(position);
        holder.textViewTrackName2.setText("Title: " + model.getTrackName());
        holder.textViewTrackArtist2.setText("Artist: " + model.getTrackArtist());
        holder.textViewTrackAlbum2.setText("Album: " + model.getTrackAlbum());
        holder.textViewUsername2.setText("From: " + model.getUsername());
        holder.textViewTrackName2.setTextColor(context.getResources().getColor(R.color.blue));
        holder.textViewTrackArtist2.setTextColor(context.getResources().getColor(R.color.blue));
        holder.textViewTrackAlbum2.setTextColor(context.getResources().getColor(R.color.blue));
        holder.textViewUsername2.setTextColor(context.getResources().getColor(R.color.blue));
        getImage(model.getTrackImage(), holder.imageViewTrackImage2, holder);
        Picasso.get().load(model.getProfilePicture()).into(holder.imageViewSoundsProfilePicture);
        holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        if (selectedItem == position) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.orange));
            holder.textViewTrackName2.setTextColor(context.getResources().getColor(R.color.white));
            holder.textViewTrackArtist2.setTextColor(context.getResources().getColor(R.color.white));
            holder.textViewTrackAlbum2.setTextColor(context.getResources().getColor(R.color.white));
            holder.textViewUsername2.setTextColor(context.getResources().getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                MainActivity.mainSpotifyAppRemote.getPlayerApi().play(model.getTrackUri());
                notifyItemChanged(previousItem);
                notifyItemChanged(position);



            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return courseModelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private static final String TAG = "TAGSoundAdapter";
        private ImageView imageViewTrackImage2, imageViewSoundsProfilePicture;
        private TextView textViewTrackName2, textViewTrackArtist2, textViewUsername2, textViewTrackAlbum2;

        public Viewholder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewTrackName2 = itemView.findViewById(R.id.textViewTrackName2);
            textViewTrackArtist2 = itemView.findViewById(R.id.textViewTrackArtist2);
            textViewTrackAlbum2 = itemView.findViewById(R.id.textViewTrackAlbum2);
            textViewUsername2 = itemView.findViewById(R.id.textViewUsername2);
            imageViewTrackImage2 = itemView.findViewById(R.id.imageViewTrackImage2);
            imageViewTrackImage2.setVisibility(View.INVISIBLE);
            imageViewSoundsProfilePicture = itemView.findViewById(R.id.imageViewSoundsProfilePicture);

            if (clickable) {
                imageViewSoundsProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Profile Clicked");
                        Fragment fragment = FragmentManager.findFragment(view);
                        listener.onItemClick(getAdapterPosition());
                        Log.d(TAG, "Adapter Position: " + getAdapterPosition());

                        Sound model = courseModelArrayList.get(getAdapterPosition());
                        Log.d(TAG, "Model: " + model);
                        String username = model.getUsername();
                        String key = model.getKey();

                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("key", key);

                        Log.d(TAG, "username: " + username);
                        Log.d(TAG, "Key: " + key);


                        FragmentTransaction fragmentTransaction = fragment.getParentFragmentManager().beginTransaction();

                        Fragment userLikesFragment = new UserLikesFragment();
                        userLikesFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.fragmentContainerView, userLikesFragment).commitNow();
                    }
                });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

        }
    }
}
