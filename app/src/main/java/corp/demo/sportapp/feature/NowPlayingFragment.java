package corp.demo.sportapp.feature;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import corp.demo.sportapp.Sport;
import corp.demo.sportapp.SportAdapter;
import corp.demo.sportapp.SportDetails.SportDetailActivity;

import corp.demo.sportapp.SportDetails.SportDetailFragment;
import corp.demo.sportapp.databaseSQLITE.SportContract;
import corp.demo.sportapp.utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import corp.demo.sportapp.R;
/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements SportAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = NowPlayingFragment.class.getSimpleName();

    @BindView(R.id.recycled_movie_grid)
    RecyclerView movie_grid_recyclerView;

    @BindView(R.id.indeterminateBar)
    ProgressBar mProgressBar;

    private RecyclerView recyclerView;
    private List<Sport> itemsList;
    private List<SportAdapter> mAdapter;


    public NowPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);


        return view;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        getView().findViewById(R.id.indeterminateBar).setVisibility(View.VISIBLE);
        return new CursorLoader(getActivity(),
                SportContract.SportEntry.CONTENT_URI,
                SportContract.SportEntry.SPORT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void send_details(Sport sport, int position) {

    }
}