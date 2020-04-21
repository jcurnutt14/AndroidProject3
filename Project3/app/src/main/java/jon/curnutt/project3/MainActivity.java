package jon.curnutt.project3;
// api key b5f9b213

//api call for specific episode https://www.omdbapi.com/?apikey=b5f9b213&t=[insert title]&season=[insert season]&episode=[insert episode]

//********PROBABLY SHOULDN'T ALLOW THE USER TO SPECIFY EPISODES BECAUSE IT GETS VERY COMPLICATED********\\

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    private TextView mMovieTitleTextView;
    private TextView mMoviePlotTextView;
    private String KEY = "b5f9b213";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieTitleTextView = findViewById(R.id.MovieTitleTextView);
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

    void getDetails(String input) {

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

    public void checkInput(String input) {

        //String enteredText = mValueEditText.getText().toString();

        if(input.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_input_toast), Toast.LENGTH_SHORT).show();
            mMoviePlotTextView.setText("");
            mMovieTitleTextView.setText("");
        }
        else getDetails(input);
    }
}