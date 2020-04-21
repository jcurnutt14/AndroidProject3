package jon.curnutt.project3;
// api key b5f9b213

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Movie/Show";
    private EditText mValueEditText;
    private TextView mMovieTitleTextView;
    private TextView mMoviePlotTextView;
    private String KEY = "b5f9b213";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieTitleTextView = findViewById(R.id.MovieTitleTextView);
        mValueEditText = findViewById(R.id.valueEditText);
        mMoviePlotTextView = findViewById(R.id.MoviePlotTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // Add new item to recycler view
                return true;

            case R.id.action_settings:
                // Go to settings
                return true;

            case R.id.action_about:
                // App description in alert dialogue
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void getDetails(String input) {

        // Create a new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

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

                            mMovieTitleTextView.setText(titleYear);
                            mMoviePlotTextView.setText(plot);
                            mMovieTitleTextView.setTextColor(getResources().getColor(R.color.black));

                        } catch (JSONException e) {
                            //display error if movie cannot be found
                            e.printStackTrace();
                            mMovieTitleTextView.setText(getString(R.string.api_error));
                            mMovieTitleTextView.setTextColor(getResources().getColor(R.color.red));
                            mMoviePlotTextView.setText("");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.toString());
                        mMovieTitleTextView.setText(getString(R.string.api_error));
                        mMovieTitleTextView.setTextColor(getResources().getColor(R.color.red));
                        mMoviePlotTextView.setText("");
                    }
                });

// Add the request to the RequestQueue
        queue.add(requestObj);
    }

    public void getDetailsClick(View view) {

        String enteredText = mValueEditText.getText().toString();

        if(enteredText.length() == 0) {
            Toast.makeText(this, "Please enter a value.", Toast.LENGTH_SHORT).show();
            mMoviePlotTextView.setText("");
            mMovieTitleTextView.setText("");
        }
        else getDetails(enteredText);
    }
}