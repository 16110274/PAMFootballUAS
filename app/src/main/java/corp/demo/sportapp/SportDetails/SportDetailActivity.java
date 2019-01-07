package corp.demo.sportapp.SportDetails;
import android.arch.lifecycle.LiveData;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.WindowManager;

import corp.demo.sportapp.Sport;
import corp.demo.sportapp.R;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SportDetailActivity extends AppCompatActivity {

    public static final String LOG_TAG = SportDetailActivity.class.getSimpleName();

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;

    public static LiveData<List<Sport>> sport;
    public static List<Sport> updated_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        checkBuildVersion();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(SportDetailFragment.ARG_SPORT,
                    getIntent().getParcelableExtra(SportDetailFragment.ARG_SPORT));
            SportDetailFragment fragment = new SportDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }

    protected void checkBuildVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
