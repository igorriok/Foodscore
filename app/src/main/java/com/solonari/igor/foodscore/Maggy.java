package com.solonari.igor.foodscore;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Maggy extends AppCompatActivity {

    private ProgressDialog pDialog;
    final String TAG = "Maggy";
    ArrayList<Food> foodList;
    GridView gridview;
    TextView score;
    TextView nScore;
    TextView pScore;
    int scorePoints = 0;
    int nScorePoints = 0;
    int pScorePoints = 0;
    float animDur = 1;
    LinearLayout tableN;
    LinearLayout tableP;
    int viewID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maggy);
        foodList = new ArrayList<>();
        gridview = (GridView) findViewById(R.id.gridview);
        //gridview.setColumnWidth(200);
        new GetFood().execute();
        score = (TextView) findViewById(R.id.fScoreText);
        nScore = (TextView) findViewById(R.id.nScoreText);
        pScore = (TextView) findViewById(R.id.pScoreText);
        tableN = (LinearLayout) findViewById(R.id.tableN);
        tableP = (LinearLayout) findViewById(R.id.tableP);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(Maggy.this, "Position: " + position + ", id: " + id, Toast.LENGTH_LONG).show();
                int points = foodList.get((int)id).getScore();
                scorePoints += points;
                score.setText(String.valueOf(scorePoints));

                View newFood;
                if (points > 0) {
                    newFood = View.inflate(Maggy.this, R.layout.list_item, tableP);
                    pScorePoints += points;
                    pScore.setText(String.valueOf(pScorePoints));
                } else {
                    newFood = View.inflate(Maggy.this, R.layout.list_item, tableN);
                    nScorePoints += points;
                    nScore.setText(String.valueOf(nScorePoints));
                }
                //newFood.setId(viewID+1);
                /* Find the TextView in the list_item.xml layout with the ID version_name
                TextView nameTextView = (TextView) newFood.findViewById(R.id.version_name);
                nameTextView.setText(foodList.get((int)id).getName());
                nameTextView.setId(viewID+1);
                */

                // Find the ImageView in the list_item.xml layout with the ID list_item_icon
                ImageView iconView = (ImageView) newFood.findViewById(R.id.list_item_icon);
                iconView.setImageResource(R.drawable.i1);
                iconView.setId(viewID+1);
            }
        });
    }

    private class GetFood extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Maggy.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String json = null;

            try {
                InputStream is = Maggy.this.getAssets().open("FoodList.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                Log.d(TAG, "opened file");
            } catch (IOException e) {
                Log.e(TAG, "cant open file: ", e);
            }

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);

                    // Getting JSON Array node
                    JSONArray foodArray = jsonObj.getJSONArray("foodList");

                    // looping through All Contacts
                    for (int i = 0; i < foodArray.length(); i++) {
                        JSONObject c = foodArray.getJSONObject(i);

                        int id = c.getInt("ID");
                        String name = c.getString("Name");
                        int score = c.getInt("score");
                        int popularity = c.getInt("popularity");

                        // tmp hash map for single contact
                        Food contact = new Food(id, name, score, popularity);

                        // adding contact to contact list
                        foodList.add(contact);
                    }
                    Log.d(TAG, "foodList created: " + foodList.toString());
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json file.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into GridView
             * */
            FoodAdapter foodAdapter = new FoodAdapter(Maggy.this, foodList);
            gridview.setAdapter(foodAdapter);
            Log.d(TAG, "foodAdapter set");
        }
    }
}
