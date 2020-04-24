package jon.curnutt.project3;
// api key b5f9b213

//api call for specific episode https://www.omdbapi.com/?apikey=b5f9b213&t=[insert title]&season=[insert season]&episode=[insert episode]

//********PROBABLY SHOULDN'T ALLOW THE USER TO SPECIFY EPISODES BECAUSE IT GETS VERY COMPLICATED********\\

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static jon.curnutt.project3.MovieDatabase.MovieSortOrder.ALPHABETIC;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Movie/Show";
    //private TextView mMovieTitleTextView;
    //private TextView mMoviePlotTextView;
    private String KEY = "b5f9b213";
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int mSelectedMoviePosition = RecyclerView.NO_POSITION;
    //private boolean mDarkTheme;
    private SharedPreferences mSharedPrefs;
    private MovieDatabase mMovieDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

//        mMovieTitleTextView = findViewById(R.id.MovieTitleTextView);
//        mMoviePlotTextView = findViewById(R.id.MoviePlotTextView);

        mMovieDb = MovieDatabase.getInstance(getApplicationContext());

        mRecyclerView = findViewById(R.id.movieRecyclerView);

        // Create 2 grid layout columns
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Shows the available subject
        mMovieAdapter = new MovieAdapter(loadMovies());
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If theme changed, recreate the activity so theme is applied
//        boolean darkTheme = mSharedPrefs.getBoolean(SettingsFragment.PREFERENCE_THEME, false);
//        if (darkTheme != mDarkTheme) {
//            recreate();
//        }

        // Load subjects here in case settings changed
        mMovieAdapter = new MovieAdapter(loadMovies());
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // Add new item to recycler view
                showAddDialog(); //code for custom dialogue help at https://bhavyanshu.me/tutorials/create-custom-alert-dialog-in-android/08/20/2015/
                return true;

            case R.id.action_settings:
                // Go to settings
                return true;

            case R.id.action_about:
                // App description in alert dialogue
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(MainActivity.this.getText(R.string.app_description))
                        .setCancelable(false)
                        .setTitle(R.string.app_description_title)
                        .setPositiveButton(R.string.close_appdesc_dialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //close the dialog
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.AlertDialogueAddEditText);

        dialogBuilder.setTitle(R.string.add_title);
        dialogBuilder.setMessage(R.string.add_message);
        dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String input = edt.getText().toString();
                checkInput(input);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Close dialogue
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    void getDetails(final String input) {

        // Create a new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //this api call only works for movies and generic TV shows (not specific episodes yet)
        String url = "https://www.omdbapi.com/?apikey=" + KEY + "&t=" + input;

        // Create a new JsonObjectRequest that requests available subjects
        JsonObjectRequest requestObj = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //get title
                            String title = response.getString("Title");
                            Log.d(TAG, title);

                            String year = response.getString("Year");

                            String titleYear = title + ", released: " + year;

                            String plot = response.getString("Plot");

                            Movie movie = new Movie(title, year, titleYear, plot);

                            if (mMovieDb.addMovie(movie)) {
                                mMovieAdapter.addMovie(movie);
                                Toast.makeText(getApplicationContext(), "Added " + title, Toast.LENGTH_SHORT).show();
                            } else {
                                String message = getResources().getString(R.string.movie_exists, title);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }

                            //mMovieTitleTextView.setText(titleYear);
                            //mMoviePlotTextView.setText(plot);
                            //mMovieTitleTextView.setTextColor(getResources().getColor(R.color.black));

                        } catch (JSONException e) {
                            //display error if movie cannot be found
                            e.printStackTrace();
                            String message = getResources().getString(R.string.movie_not_found, input);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.toString());
                        //mMovieTitleTextView.setText(getString(R.string.api_error));
                        //mMovieTitleTextView.setTextColor(getResources().getColor(R.color.red));
                        //mMoviePlotTextView.setText("");
                    }
                });

// Add the request to the RequestQueue
        queue.add(requestObj);
    }

    public void checkInput(String input) {

        //String enteredText = mValueEditText.getText().toString();

        if(input.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_input_toast), Toast.LENGTH_SHORT).show();
            //mMoviePlotTextView.setText("");
           // mMovieTitleTextView.setText("");
        }
        else getDetails(input);
    }

    // if we do anything with long click/press, include this - implements View.OnClickListener, View.OnLongClickListener
    private class MovieHolder extends RecyclerView.ViewHolder {

        private Movie mMovie;
        private TextView mTextView;

        public MovieHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            //itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.movieTextView);
            //itemView.setOnLongClickListener(this);
        }

        public void bind(Movie movie, int position) {
            mMovie = movie;
            mTextView.setText(movie.getName());

//            if (mSelectedMoviePosition == position) {
//                // Make selected subject stand out
//                mTextView.setBackgroundColor(Color.RED);
//            } else {
//                // Make the background color dependent on the length of the subject string
//                int colorIndex = movie.getName().length() % mSubjectColors.length;
//                mTextView.setBackgroundColor(mSubjectColors[colorIndex]);
//            }
        }

//        @Override
//        public boolean onLongClick(View view) {
//            if (mActionMode != null) {
//                return false;
//            }
//            mSelectedSubject = mSubject;
//            mSelectedSubjectPosition = getAdapterPosition();
//
//            // Re-bind the selected item
//            mSubjectAdapter.notifyItemChanged(mSelectedSubjectPosition);
//
//            // Show the CAB
//            mActionMode = SubjectActivity.this.startActionMode(mActionModeCallback);
//
//            return true;
//        }

//        @Override
//        public void onClick(View view) {
//            // Start QuestionActivity, indicating what subject was clicked
//            Intent intent = new Intent(SubjectActivity.this, QuestionActivity.class);
//            intent.putExtra(QuestionActivity.EXTRA_SUBJECT, mSubject.getText());
//            startActivity(intent);
//        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private List<Movie> mMovieList;

        public MovieAdapter(List<Movie> movies) {
            mMovieList = movies;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new MovieHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MovieHolder holder, int position){
            holder.bind(mMovieList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mMovieList.size();
        }

        public void addMovie(Movie movie) {
            mMovieList.add(0, movie);

            notifyItemInserted(0);

            mRecyclerView.scrollToPosition(0);
        }

        public void removeMovie(Movie movie) {
            int index = mMovieList.indexOf(movie);
            if (index >= 0) {
                mMovieList.remove(index);

                notifyItemRemoved(index);
            }
        }
    }

    // Sorta broke for now so just use alphabetical list
    private List<Movie> loadMovies() {
//        String order = mSharedPrefs.getString(PREFERENCE_SUBJECT_ORDER, "1");
//        switch (Integer.parseInt(order)) {
//            case 0: return mMovieDb.getMovies(ALPHABETIC);
//            case 1: return mMovieDb.getMovies(UPDATE_DESC);
//            default: return mMovieDb.getMovies(UPDATE_ASC);
//        }
        return mMovieDb.getMovies(ALPHABETIC);
    }
}