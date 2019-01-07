package corp.demo.sportapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import corp.demo.sportapp.Activity.LoginActivity;
import corp.demo.sportapp.SportDetails.SportDetailActivity;
import corp.demo.sportapp.SportDetails.SportDetailFragment;
import corp.demo.sportapp.databaseSQLITE.SportContract;
import corp.demo.sportapp.util.ATabPager;
import corp.demo.sportapp.utilities.NetworkUtils;

public class UserInterface extends AppCompatActivity implements SportAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
, TabLayout.OnTabSelectedListener{

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private boolean tabletView;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    String myApiKey = BuildConfig.API_KEY;
    private static final int FAVORITE_MOVIES_LOADER = 0;
    @BindView(R.id.recycled_movie_grid)
    RecyclerView movie_grid_recyclerView;

    @BindView(R.id.indeterminateBar)
    ProgressBar mProgressBar;

    String premiereTeamURL;
    String laligaTeamURL;
    String serieaTeamURL;
    String bundesligaTeamURL;
    String ligueoneTeamURL;


    ArrayList<Sport> mPremiereList;
    ArrayList<Sport> mLaligaList;
    ArrayList<Sport> mSerieaList;
    ArrayList<Sport> mBundesligaList;
    ArrayList<Sport> mLigueone;

    SharedPrefManager sharedPrefManager;

    private SportAdapter mAdapter;
    private SportAdapter mAdapterFavorite;
    private String mSortBy = UserInterface.FetchSport.PREMIERELEAGUE;

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        ButterKnife.bind(this);
        sharedPrefManager = new SharedPrefManager(this);

        mProgressBar.setVisibility(View.INVISIBLE); //Hide Progressbar by Default
        //Dealing with View Model
        setupTab();

        //Define recyclerView Layout
        movie_grid_recyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.number_of_grid_columns)));
        mAdapter = new SportAdapter(new ArrayList<Sport>(), this);
        movie_grid_recyclerView.setAdapter(mAdapter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        // Menginisiasi  NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Mengatur Navigasi View Item yang akan dipanggil untuk menangani item klik menu navigasi
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.tvHeaderNama);
        navUsername.setText(sharedPrefManager.getSPNama());

        TextView navEmail = (TextView) headerView.findViewById(R.id.tvHeaderEmail);
        navEmail.setText(sharedPrefManager.getSPEmail());


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                //if (id == R.id.nav_now_playing) selectTab(0);
                //if (id == R.id.nav_favorite) selectTab(1);


                //Memeriksa apakah item tersebut dalam keadaan dicek  atau tidak,
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                //Menutup  drawer item klik
                drawerLayout.closeDrawers();
                //Memeriksa untuk melihat item yang akan dilklik dan melalukan aksi
                switch (menuItem.getItemId()){
                    // pilihan menu item navigasi akan menampilkan pesan toast klik kalian bisa menggantinya
                    //dengan intent activity

                    case R.id.nav_exit:
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                        startActivity(new Intent(UserInterface.this, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        return true;

                    default:
                        moveTaskToBack(true);
                        Toast.makeText(getApplicationContext(),"Anda Telah Keluar",Toast.LENGTH_SHORT).show();
                        return false;
                }

            }
        });


        // Menginisasi Drawer Layout dan ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Kode di sini akan merespons setelah drawer menutup disini kita biarkan kosong
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                //  Kode di sini akan merespons setelah drawer terbuka disini kita biarkan kosong
                super.onDrawerOpened(drawerView);
            }
        };
        //Mensetting actionbarToggle untuk drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //memanggil synstate
        actionBarDrawerToggle.syncState();







        // Large-screen
        tabletView = findViewById(R.id.movie_detail_container) != null;

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Sport> sports = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                mAdapter.add(sports);
                findViewById(R.id.indeterminateBar).setVisibility(View.GONE);

                // For listening content updates for tow pane mode
                if (mSortBy.equals(UserInterface.FetchSport.FAVORITES)) {
                    getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null,this);
                }
            }
            update_empty_state();
        } else {
            // Fetch Movies only if savedInstanceState == null
            if(NetworkUtils.networkStatus(UserInterface.this)){
                new UserInterface.FetchSport().execute();
            }else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserInterface.this);
                dialog.setTitle(getString(R.string.title_network_alert));
                dialog.setMessage(getString(R.string.message_network_alert));
                dialog.setCancelable(false);
                dialog.show();
            }
        }
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
        if (mSortBy.equals(UserInterface.FetchSport.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
        mSortBy = UserInterface.FetchSport.PREMIERELEAGUE;
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

        if (!mSortBy.equals(UserInterface.FetchSport.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main, menu);
        inflater.inflate(R.menu.dashboard, menu);


        switch (mSortBy) {
            case UserInterface.FetchSport.PREMIERELEAGUE:
                menu.findItem(R.id.sort_premiere).setChecked(true);
                break;
            case UserInterface.FetchSport.LALIGA:
                menu.findItem(R.id.sort_laliga).setChecked(true);
                break;
            case UserInterface.FetchSport.FAVORITES:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
            case UserInterface.FetchSport.SERIEA:
                menu.findItem(R.id.sort_seriea).setChecked(true);
                break;
            case FetchSport.BUNDESLIGA:
                menu.findItem(R.id.sort_bundesliga).setChecked(true);
                break;
            case FetchSport.LIGUEONE:
                menu.findItem(R.id.sort_ligueone).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_premiere:
                if (mSortBy.equals(UserInterface.FetchSport.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = FetchSport.PREMIERELEAGUE;
                refreshList(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_laliga:
                if (mSortBy.equals(UserInterface.FetchSport.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = FetchSport.LALIGA;
                refreshList(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_favorites:
                mSortBy = UserInterface.FetchSport.FAVORITES;
                item.setChecked(true);
                refreshList(mSortBy);
                break;
            case R.id.sort_seriea:
                mSortBy = UserInterface.FetchSport.SERIEA;
                item.setChecked(true);
                refreshList(mSortBy);
                break;
            case R.id.sort_bundesliga:
                mSortBy = FetchSport.BUNDESLIGA;
                item.setChecked(true);
                refreshList(mSortBy);
                break;
            case R.id.sort_ligueone:
                mSortBy = FetchSport.LIGUEONE;
                item.setChecked(true);
                refreshList(mSortBy);
                break;


            default: break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList(String sort_by) {

        switch (sort_by){
            case UserInterface.FetchSport.PREMIERELEAGUE:
                mAdapter = new SportAdapter(new ArrayList<Sport>(),this);
                mAdapter.add(mPremiereList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case UserInterface.FetchSport.LALIGA:
                mAdapter = new SportAdapter(new ArrayList<Sport>(),this);
                mAdapter.add(mLaligaList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case UserInterface.FetchSport.FAVORITES:
                getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
                break;
            case UserInterface.FetchSport.SERIEA:
                mAdapter = new SportAdapter(new ArrayList<Sport>(), this);
                mAdapter.add(mSerieaList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case FetchSport.BUNDESLIGA:
                mAdapter = new SportAdapter(new ArrayList<Sport>(), this);
                mAdapter.add(mBundesligaList);
                movie_grid_recyclerView.setAdapter(mAdapter);
                break;
            case FetchSport.LIGUEONE:
                mAdapter = new SportAdapter(new ArrayList<Sport>(), this);
                mAdapter.add(mLigueone);
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
            toolbar.setBackgroundColor(ContextCompat.getColor(UserInterface.this,
                    R.color.colorPrimary));
            tabLayout.setBackgroundColor(ContextCompat.getColor(UserInterface.this,
                    R.color.colorPrimary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(UserInterface.this,
                        R.color.colorPrimary));

                    if (mSortBy.equals(Dashboard.FetchSport.FAVORITES)) {
                        getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                    }
                    mSortBy = FetchSport.PREMIERELEAGUE;
                    refreshList(mSortBy);
            }
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(UserInterface.this,
                    R.color.colorAccent));
            tabLayout.setBackgroundColor(ContextCompat.getColor(UserInterface.this,
                    R.color.colorAccent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(UserInterface.this,
                        R.color.colorAccent));
                mSortBy = FetchSport.FAVORITES;
                refreshList(mSortBy);


            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private void setupTab() {
        viewPager.setAdapter(new ATabPager(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(R.string.label_now_playing);
        tabLayout.getTabAt(1).setText(R.string.label_favorite);
        tabLayout.addOnTabSelectedListener(this);
    }

    //AsyncTask
    public class FetchSport extends AsyncTask<Void,Void,Void> {

        public final static String PREMIERELEAGUE = "premiere";
        public final static String LALIGA = "laliga";
        public final static String FAVORITES = "favorites";
        public final static String SERIEA = "seria";
        public final static String BUNDESLIGA = "bundesliga";
        public final static String LIGUEONE = "ligueone";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids) {

            premiereTeamURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=English%20Premier%20League";
            laligaTeamURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=Spanish%20La%20Liga";
            serieaTeamURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=Italian%20Serie%20A";
            bundesligaTeamURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=German%20Bundesliga";
            ligueoneTeamURL = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=French%20Ligue%201";


            mPremiereList = new ArrayList<>();
            mLaligaList = new ArrayList<>();
            mSerieaList = new ArrayList<>();
            mBundesligaList = new ArrayList<>();
            mLigueone = new ArrayList<>();
            try {
                if(NetworkUtils.networkStatus(UserInterface.this)){
                    mPremiereList = NetworkUtils.fetchData(premiereTeamURL); //Get premiere movies
                    mLaligaList = NetworkUtils.fetchData(laligaTeamURL); //Get top rated movies
                    mSerieaList = NetworkUtils.fetchData(serieaTeamURL); //Get now playing movies
                    mBundesligaList = NetworkUtils.fetchData(bundesligaTeamURL); //Get now playing movies
                    mLigueone = NetworkUtils.fetchData(ligueoneTeamURL); //Get now playing movies

                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UserInterface.this);
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
            //Load premiere movies by default
            mAdapter = new SportAdapter(new ArrayList<Sport>(),UserInterface.this);
            mAdapter.add(mPremiereList);
            movie_grid_recyclerView.setAdapter(mAdapter);
        }
    }

    private void update_empty_state() {
        if (mAdapter.getItemCount() == 0) {
            if (mSortBy.equals(UserInterface.FetchSport.FAVORITES)) {
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

    private void selectTab(int tabNumber) {
        tabLayout.getTabAt(tabNumber).select();
    }
}
