package corp.demo.sportapp.SportDetails;
import android.app.Activity;
import android.app.NotificationManager;
import android.arch.lifecycle.LiveData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import corp.demo.sportapp.Sport;
import corp.demo.sportapp.SportComponents.Reviews;
import corp.demo.sportapp.SportComponents.Trailers;
import corp.demo.sportapp.R;
import corp.demo.sportapp.UserInterface;
import corp.demo.sportapp.databaseSQLITE.SportContract;
import corp.demo.sportapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SportDetailFragment extends Fragment implements
        TrailerAdapter.OnItemClickListener, TrailersTask.Listener, ReviewsTask.Listener, ReviewAdapter.OnItemClickListener {

    public static final int NOTIFICATION_ID = 1;
    public static final String LOG_TAG = SportDetailFragment.class.getSimpleName();

    public static final String ARG_SPORT = "ARG_SPORT";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private Sport mSport;
    private TrailerAdapter mTrailerListAdapter;
    private ReviewAdapter mReviewAdapter;
    private ShareActionProvider mShareActionProvider;
    private LiveData<List<Sport>> sport;
    public static List<Sport> updated_list;   //This is updated favorite movie list

    @BindView(R.id.trailer_list)
    RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.review_list)
    RecyclerView mRecyclerViewReviews;

    @BindView(R.id.movie_title)
    TextView mMovieTitleView;
    @BindView(R.id.movie_overview)
    TextView mMovieOverviewView;
    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDateView;
    @BindView(R.id.movie_user_rating)
    TextView mMovieRatingView;
    @BindView(R.id.movie_poster)
    ImageView mMoviePosterView;

    @BindView(R.id.button_watch_trailer)
    Button mButtonWatchTrailer;
    @BindView(R.id.button_mark_as_favorite)
    Button mButtonMarkAsFavorite;
    @BindView(R.id.button_remove_from_favorites)
    Button mButtonRemoveFromFavorites;

    List<ImageView> ratingStarViews;

    public SportDetailFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_SPORT)) {
            mSport = getArguments().getParcelable(ARG_SPORT);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();


        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof SportDetailActivity) {
            appBarLayout.setTitle(mSport.getTeam_name());
        }


        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            Picasso.get()
                    .load(mSport.getTeam_stadium())
                    .config(Bitmap.Config.RGB_565)
                    .into(movieBackdrop);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sport_details, container, false);
        ButterKnife.bind(this, rootView);

        mMovieTitleView.setText(mSport.getTeam_name());
        mMovieOverviewView.setText(mSport.getTeam_description());
        mMovieReleaseDateView.setText(mSport.getTeam_formed());
        Picasso.get()
                .load(mSport.getTeam_badge())
                .config(Bitmap.Config.RGB_565)
                .into(mMoviePosterView);

        updateFavorites();
        load_trailers(savedInstanceState);
        load_reviews(savedInstanceState);
        Log.d(LOG_TAG,SportContract.SportEntry.CONTENT_URI.toString());
        /*IF savedInstanceState == null*/

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailers> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);
            mButtonWatchTrailer.setEnabled(true);
        } else {
            getTrailers();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Reviews> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            getReviews();
        }
        Log.d(LOG_TAG, "Current selected movie id is: " + String.valueOf(mSport.getId_team()));

        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailers> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Reviews> reviews = mReviewAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavorites();
    }

    private void load_reviews(Bundle savedInstanceState) {
        // List of reviews (Vertically Arranged)
        mReviewAdapter = new ReviewAdapter(new ArrayList<Reviews>(), this);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);

        // Request for the reviews if only savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Reviews> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            getReviews();
        }
    }

    private void load_trailers(Bundle savedInstanceState) {
        //List of Trailers (Horizontal Layout)
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewTrailers.setLayoutManager(layoutManager);
        mTrailerListAdapter = new TrailerAdapter(new ArrayList<Trailers>(), this);
        mRecyclerViewTrailers.setAdapter(mTrailerListAdapter);
        mRecyclerViewTrailers.setNestedScrollingEnabled(false);

        //  Request for the trailers if only savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailers> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);
            mButtonWatchTrailer.setEnabled(true);
        } else {
            getTrailers();
        }
    }

    private void getTrailers() {
        if (NetworkUtils.networkStatus(getContext())) {
            TrailersTask task = new TrailersTask((TrailersTask.Listener) SportDetailFragment.this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  mSport.getId_team());
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle(getString(R.string.title_network_alert));
            dialog.setMessage(getString(R.string.message_network_alert));
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void getReviews() {
        ReviewsTask task = new ReviewsTask((ReviewsTask.Listener) SportDetailFragment.this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSport.getId_team());
    }

    /*Implemented method from TrailerTask Class*/
    @Override
    public void onLoadFinished(List<Trailers> trailers) {
        mTrailerListAdapter.add(trailers);
        mButtonWatchTrailer.setEnabled(!trailers.isEmpty());
        if (mTrailerListAdapter.getItemCount() > 0) {
            Trailers trailer = mTrailerListAdapter.getTrailers().get(0);
            if (trailer != null) {
                refresh_share_action_provider(trailer);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sport_detail_fragment, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
    }


    /*User is able to share the trailer url with others*/
    private void refresh_share_action_provider(Trailers trailers) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mSport.getTeam_name());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, trailers.getName() + ": "
                + trailers.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }

    /*Implemented method from ReviewsTask Class*/
    @Override
    public void on_reviews_loaded(List<Reviews> reviews) {
        mReviewAdapter.add(reviews);
    }



    public void mark_as_favorite() {
        Log.d(LOG_TAG, "Calling check for favorite method");


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!check_for_favorite()) {

                   /*
                    //Store movie object to the database
                    Log.d(LOG_TAG, "So creating new movieEntry object and storing it!");
                    final Date date = new Date();
                    MovieEntry movieEntry = new MovieEntry(
                            mMovie.getId(),
                            mMovie.getVoteAverage(),
                            mMovie.getOriginalTitle(),
                            mMovie.getBackdropPath(),
                            mMovie.getOverview(),
                            mMovie.getReleaseDate(),
                            mMovie.getPosterPath(),
                            date);
                    mDb.movieDao().insertMovie(movieEntry);
                    Log.d(LOG_TAG, "Was not marked as Favorite, so Marked it!");
                  */
                    ContentValues movie_data = new ContentValues();
                    movie_data.put(SportContract.SportEntry.COLUMN_SPORT_ID_TEAM,
                            mSport.getId_team());
                    movie_data.put(SportContract.SportEntry.COLUMN_SPORT_STR_TEAM,mSport.getTeam_name());
                    movie_data.put(SportContract.SportEntry.COLUMN_SPORT_STR_STADIUM_THUMB, mSport.getTeam_stadium());
                    movie_data.put(SportContract.SportEntry.COLUMN_SPORT_STR_DESCRIPTION_EN, mSport.getTeam_description());
                    movie_data.put(SportContract.SportEntry.COLUMN_SPORT_INT_FORMED_YEAR, mSport.getTeam_formed());
                    movie_data.put(SportContract.SportEntry.COLUMN_SPORT_STR_TEAM_BADGE, mSport.getTeam_badge());
                    getContext().getContentResolver().insert(
                            SportContract.SportEntry.CONTENT_URI,
                            movie_data
                    );
                    Log.d(LOG_TAG, "Was not marked as Favorite, so Marked it!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d(LOG_TAG, "Calling uupdate Favorites, inside markasfavorite");
                updateFavorites();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void remove_from_favorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (check_for_favorite()) {
                    //mDb.movieDao().deleteMovieById(mMovie.getId());

                    getContext().getContentResolver().delete(SportContract.SportEntry.CONTENT_URI,
                            SportContract.SportEntry.COLUMN_SPORT_ID_TEAM + " = " + mSport.getId_team(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavorites();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void updateFavorites() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d(LOG_TAG, "Calling uupdate Favorites, inside updateFavorites");
                return check_for_favorite();
            }

            @Override
            protected void onPostExecute(Boolean favorite) {
                Log.d(LOG_TAG, "Inside update favorite: " + favorite);
                if (favorite) {
                    mButtonRemoveFromFavorites.setVisibility(View.VISIBLE);
                    mButtonMarkAsFavorite.setVisibility(View.GONE);
                } else {
                    mButtonMarkAsFavorite.setVisibility(View.VISIBLE);
                    mButtonRemoveFromFavorites.setVisibility(View.GONE);
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mButtonMarkAsFavorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mark_as_favorite();
                        Toast.makeText(getActivity(), "Sudah berhasil di fav!",
                                Toast.LENGTH_LONG).show();
                        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                                .setSmallIcon(R.drawable.baseline_favorite_white_18dp) //ikon notification
                                .setContentTitle(getResources().getString(R.string.notif_title)) //judul konten
                                .setAutoCancel(true)//untuk menswipe atau menghapus notification
                                .setContentText(getResources().getString(R.string.notif_desc)); //isi text

/*
Kemudian kita harus menambahkan Notification dengan menggunakan NotificationManager
 */

                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(NOTIFICATION_ID, builder.build()
                        );



                    }

                });

        mButtonRemoveFromFavorites.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove_from_favorites();
                        Toast.makeText(getActivity(), "Sudah berhasil dihaps  !",
                                Toast.LENGTH_LONG).show();


                    }

                });

        mButtonWatchTrailer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTrailerListAdapter.getItemCount() > 0) {
                            watch_trailer(mTrailerListAdapter.getTrailers().get(0), 0);
                        }
                    }
                });
    }

    /*Overidden Methods from Tariler Task and ReviewTask Classes*/
    @Override
    public void watch_trailer(Trailers trailers, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailers.getTrailerUrl())));
    }

    @Override
    public void read_reviews(Reviews review, int position) {

    }

    private boolean check_for_favorite() {
        //Check the database if this is already in the list
        //Using SQLite
        Cursor movieCursor = getContext().getContentResolver().query(
                SportContract.SportEntry.CONTENT_URI,
                new String[]{SportContract.SportEntry.COLUMN_SPORT_ID_TEAM},
                SportContract.SportEntry.COLUMN_SPORT_ID_TEAM + " = " + mSport.getId_team(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }


}
