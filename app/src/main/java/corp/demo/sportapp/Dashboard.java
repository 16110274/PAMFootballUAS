package corp.demo.sportapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import corp.demo.sportapp.SportDetails.SportDetailActivity;

import corp.demo.sportapp.SportDetails.SportDetailFragment;
import corp.demo.sportapp.databaseSQLITE.SportContract;
import corp.demo.sportapp.utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("Registered")
public class Dashboard extends AppCompatActivity implements SportAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = Dashboard.class.getSimpleName();

    private boolean tabletView;

    String myApiKey = BuildConfig.API_KEY;
    private static final int FAVORITE_MOVIES_LOADER = 0;
    @BindView(R.id.recycled_movie_grid)
    RecyclerView movie_grid_recyclerView;

    @BindView(R.id.indeterminateBar)
    ProgressBar mProgressBar;

    String popularMoviesURL;
    String topRatedMoviesURL;
    String nowPlayingMoviesURL;

    ArrayList<Sport> mPopularList;
    ArrayList<Sport> mTopTopRatedList;
    ArrayList<Sport> mNowPlayingList;


    private SportAdapter mAdapter;
    private SportAdapter mAdapterFavorite;
    private String mSortBy = FetchSport.POPULAR;

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE); //Hide Progressbar by Default
        //Dealing with View Model

        //Define recyclerView Layout
        movie_grid_recyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.number_of_grid_columns)));
        mAdapter = new SportAdapter(new ArrayList<Sport>(), this);
        movie_grid_recyclerView.setAdapter(mAdapter);
        // Large-screen
        tabletView = findViewById(R.id.movie_detail_container) != null;

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Sport> sports = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                mAdapter.add(sports);
                findViewById(R.id.indeterminateBar).setVisibility(View.GONE);

                // For listening content updates for tow pane mode
                if (mSortBy.equals(FetchSport.FAVORITES)) {
                    getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null,this);
                }
            }
            update_empty_state();
        } else {
            // Fetch Movies only if savedInstanceState == null
            if(NetworkUtils.networkStatus(Dashboard.this)){
                new FetchSport().execute();
            }else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
                dialog.setTitle(getString(R.string.title_network_alert));
                dialog.setMessage(getString(R.string.message_network_alert));
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mSortBy.equals(FetchSport.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
        mSortBy = FetchSport.POPULAR;
        refreshList(mSortBy);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Sport> sports = mAdapter.getMovies();
        if (sports != null && !sports.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, sports);
        }
        outState.putString(EXTRA_SORT_BY, mSortBy);

        if (!mSortBy.equals(FetchSport.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);

        switch (mSortBy) {
            case FetchSport.POPULAR:
                menu.findItem(R.id.sort_by_popular).setChecked(true);
                break;
            case FetchSport.TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case FetchSport.FAVORITES:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
            case FetchSport.NOW_PLAYING:
                menu.findItem(R.id.sort_by_now_playing).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                if (mSortBy.equals(FetchSport.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = FetchSport.TOP_RATED;
                refreshList(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_popular:
                if (mSortBy.equals(FetchSport.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = FetchSport.POPULAR;
                refreshList(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_favorites:
                mSortBy = FetchSport.FAVORITES;
                item.setChecked(true);
                refreshList(mSortBy);
            default:
                break;
            case R.id.sort_by_now_playing:
                mSortBy = FetchSport.NOW_PLAYING;
                item.setChecked(true);
                refreshList(mSortBy);
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList(String sort_by) {

        switch (sort_by){
            case FetchSport.POPULAR:
                mAdapter = new SportAdapter(new ArrayList<Sport>(),this);
                mAdapter.add(mPopularList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case FetchSport.TOP_RATED:
                mAdapter = new SportAdapter(new ArrayList<Sport>(),this);
                mAdapter.add(mTopTopRatedList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case FetchSport.FAVORITES:
                getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
                break;
            case FetchSport.NOW_PLAYING:
                mAdapter = new SportAdapter(new ArrayList<Sport>(), this);
                mAdapter.add(mNowPlayingList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
        }


    }

    public void send_details(Sport sport, int position) {
        if (tabletView) {

        } else {
            Intent intent = new Intent(this, SportDetailActivity.class);
            intent.putExtra(SportDetailFragment.ARG_SPORT, sport);
            startActivity(intent);
        }
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        findViewById(R.id.indeterminateBar).setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                SportContract.SportEntry.CONTENT_URI,
                SportContract.SportEntry.SPORT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mAdapter.add(cursor);
        update_empty_state();
        findViewById(R.id.indeterminateBar).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }



    //AsyncTask
    public class FetchSport extends AsyncTask<Void,Void,Void> {

        public final static String POPULAR = "popular";
        public final static String TOP_RATED = "top_rated";
        public final static String FAVORITES = "favorites";
        public final static String NOW_PLAYING = "now_playing";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids) {

            popularMoviesURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=English%20Premier%20League";
            topRatedMoviesURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=Spanish%20La%20Liga";
            nowPlayingMoviesURL = "https://api.themoviedb.org/3/movie/now_playing?api_key="+myApiKey+"&language=en-US";



            mPopularList = new ArrayList<>();
            mTopTopRatedList = new ArrayList<>();
            mNowPlayingList = new ArrayList<>();
            try {
                if(NetworkUtils.networkStatus(Dashboard.this)){
                    mPopularList = NetworkUtils.fetchData(popularMoviesURL); //Get popular movies
                    mTopTopRatedList = NetworkUtils.fetchData(topRatedMoviesURL); //Get top rated movies
                    mNowPlayingList = NetworkUtils.fetchData(nowPlayingMoviesURL); //Get now playing movies

                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
                    dialog.setTitle(getString(R.string.title_network_alert));
                    dialog.setMessage(getString(R.string.message_network_alert));
                    dialog.setCancelable(false);
                    dialog.show();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void  s) {
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.INVISIBLE);
            //Load popular movies by default
            mAdapter = new SportAdapter(new ArrayList<Sport>(),Dashboard.this);
            mAdapter.add(mPopularList);
            movie_grid_recyclerView.setAdapter(mAdapter);
        }
    }

    private void update_empty_state() {
        if (mAdapter.getItemCount() == 0) {
            if (mSortBy.equals(FetchSport.FAVORITES)) {
                findViewById(R.id.empty_state).setVisibility(View.GONE);
                findViewById(R.id.empty_state_favorites_container).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.empty_state).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.empty_state).setVisibility(View.GONE);
            findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
        }
    }
}
