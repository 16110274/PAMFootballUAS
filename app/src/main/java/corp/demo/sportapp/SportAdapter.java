package corp.demo.sportapp;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import corp.demo.sportapp.SportDetails.SportDetailActivity;
import corp.demo.sportapp.databaseSQLITE.SportContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.MovieViewHolder> {

    private final static String LOG_TAG = SportAdapter.class.getSimpleName();
    public static final float POSTER_ASPECT_RATIO = 1.5f;

    private final ArrayList<Sport> mSport;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void send_details(Sport sport, int position);

    }


    public SportAdapter(ArrayList<Sport> sports, OnItemClickListener mItemClickListener) {
        mSport = sports;
        this.mOnItemClickListener = mItemClickListener;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        int layoutForMovieItem = R.layout.sport_item;
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.sport_item, parent, shouldAttachToParentImmediately);
        final Context context = view.getContext();

        int gridColsNumber = context.getResources()
                .getInteger(R.integer.number_of_grid_columns);

        view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                POSTER_ASPECT_RATIO);


        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        final Sport sport = mSport.get(position);
        final Context context = holder.mView.getContext();

        holder.mMovie = sport;
        holder.mMovietitle.setText(sport.getTeam_name());

        String posterUrl = sport.getTeam_badge();

        // Warning: onError() will not be called, if url is null.
        // Empty url leads to app crash.
        if (posterUrl == null) {
            holder.mMovietitle.setVisibility(View.VISIBLE);
        }

        Picasso.get()
                .load(sport.getTeam_stadium())
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.mMovieThumbnail,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                if (holder.mMovie.getId_team() != sport.getId_team()) {
                                    holder.cleanUp();
                                } else {
                                    holder.mMovieThumbnail.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                holder.mMovietitle.setVisibility(View.VISIBLE);
                            }
                        }
                );
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.send_details(sport,holder.getAdapterPosition());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mSport.size();
    }

    @Override
    public void onViewRecycled(MovieViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUp();
    }

    //Inner Class
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public Sport mMovie;

        @BindView(R.id.movie_thumbnail)
        ImageView mMovieThumbnail;

        @BindView(R.id.movie_title)
        TextView mMovietitle;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;

        }
        //Other methods
        public void cleanUp() {
            final Context context = mView.getContext();
            Picasso.get().cancelRequest(mMovieThumbnail);
            mMovieThumbnail.setImageBitmap(null);
            mMovieThumbnail.setVisibility(View.INVISIBLE);
            mMovietitle.setVisibility(View.GONE);
        }

    }
    public void add(List<Sport> sports) {
        mSport.clear();
        mSport.addAll(sports);
        notifyDataSetChanged();
    }

    public void add(Cursor cursor) {

        mSport.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id_team = cursor.getLong(SportContract.SportEntry.COL_SPORT_ID_TEAM);
                String team_name = cursor.getString(SportContract.SportEntry.COL_SPORT_STR_TEAM);
                String team_stadium = cursor.getString(SportContract.SportEntry.COL_SPORT_STR_STADIUM_THUMB);
                String team_description = cursor.getString(SportContract.SportEntry.COL_SPORT_STR_DESCRIPTION_EN);
                String team_formed = cursor.getString(SportContract.SportEntry.COL_SPORT_INT_FORMED_YEAR);
                String team_badge = cursor.getString(SportContract.SportEntry.COL_SPORT_STR_TEAM_BADGE);
                Sport sport = new Sport(id_team,team_name,team_stadium,team_description,team_formed,team_badge);
                mSport.add(sport);
            } while (cursor.moveToNext());

        }
        notifyDataSetChanged();
    }

    public ArrayList<Sport> getMovies() {
        return mSport;
    }


}
