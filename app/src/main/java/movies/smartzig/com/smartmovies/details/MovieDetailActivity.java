package movies.smartzig.com.smartmovies.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import movies.smartzig.com.smartmovies.DiscoveryActivity;
import movies.smartzig.com.smartmovies.R;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_movie_detail);

        Toolbar mToolbar = findViewById(R.id.detail_toolbar);

        setSupportActionBar(mToolbar);
        checkBuildVersion();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("MOVIE",
                    getIntent().getParcelableExtra("MOVIE"));
            MovieDetailFragment fragment = new MovieDetailFragment();
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
            Context context = this;
            Class destinationClass = DiscoveryActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            startActivity(intentToStartDetailActivity);
           // onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
