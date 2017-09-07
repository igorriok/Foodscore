package com.solonari.igor.foodscore;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
    ConstraintLayout root;
    View newFood;
    int FOOD_POINTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maggy);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        foodList = new ArrayList<>();
        gridview = (GridView) findViewById(R.id.gridview);
        //gridview.setColumnWidth(200);
        new GetFood().execute();
        score = (TextView) findViewById(R.id.fScoreText);
        nScore = (TextView) findViewById(R.id.nScoreText);
        pScore = (TextView) findViewById(R.id.pScoreText);
        tableN = (LinearLayout) findViewById(R.id.tableN);
        tableP = (LinearLayout) findViewById(R.id.tableP);
        root = (ConstraintLayout) findViewById(R.id.root);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(Maggy.this, "Position: " + position + ", id: " + id, Toast.LENGTH_LONG).show();
                int points = foodList.get((int)id).getScore();
                scorePoints += points;
                setScore(scorePoints);
                //newFood = new ImageView(Maggy.this);
                newFood = View.inflate(Maggy.this, R.layout.list_item, null);
                newFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                newFood.setPadding(4,4,4,4);

                //newFood.setId(viewID+1);
                //Find the TextView in the list_item.xml layout with the ID version_name
                //TextView nameTextView = (TextView) newFood.findViewById(R.id.version_name);
                //nameTextView.setText(foodList.get((int)id).getName());
                //nameTextView.setId(viewID++);

                //newFood.setImageResource(R.drawable.i1);
                // Find the ImageView in the list_item.xml layout with the ID list_item_icon
                ImageView iconView = (ImageView) newFood.findViewById(R.id.list_item_icon);
                iconView.setImageResource(R.drawable.i1);
                iconView.setId(viewID++);

                newFood.setTag(R.string.food_points, points);
                
                //newFood.setLayoutParams(v.getLayoutParams());
                Rect rectf = new Rect();
                v.getGlobalVisibleRect(rectf);
                Rect parentRect = new Rect();
                root.getGlobalVisibleRect(parentRect);

                newFood.setLayoutParams(new LinearLayout.LayoutParams(v.getWidth(), v.getHeight()));
                newFood.setX(rectf.left);
                newFood.setY(rectf.top - parentRect.top);
                Log.d(TAG, "position: "+ rectf.left + " " + rectf.top);
                root.addView(newFood);
                newFood.setElevation(8);
                
                Transition move = new ChangeTransform()
                        .addTarget(newFood)
                        .setDuration(2000)
                        .setInterpolator(new DecelerateInterpolator());
              
                TransitionManager.beginDelayedTransition(root, move);

                root.removeView(newFood);

                newFood.setX(0);
                newFood.setY(0);

                newFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                newFood.setPadding(2,2,2,2);
                newFood.setElevation(4);


                setPoints(points);
                
                newFood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) v.getParent()).removeView(v);
                        int viewPoints = (int) v.getTag(R.string.food_points);
                        scorePoints -= viewPoints;
                        setScore(scorePoints);
                        if (viewPoints > 0) {
                            pScorePoints -= viewPoints;
                            pScore.setText("+" + String.valueOf(pScorePoints));
                        } else {
                            nScorePoints -= viewPoints;
                            nScore.setText(String.valueOf(nScorePoints));
                        }
                    }
                });
            }
        });
    }

    public void setPoints(int points) {
        if (points > 0) {
            tableP.addView(newFood, 0);
            pScorePoints += points;
            pScore.setText("+" + String.valueOf(pScorePoints));
        } else {
            tableN.addView(newFood, 0);
            nScorePoints += points;
            nScore.setText(String.valueOf(nScorePoints));
        }
    }

    public void setScore(int points) {
        if (scorePoints > 0) {
            score.setText("+" + String.valueOf(scorePoints));
        } else {
            score.setText(String.valueOf(scorePoints));
        }
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar_menu, menu);
        return true;
    }
  
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.reset:
                Maggy.this.recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
  
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
  
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
       super.onRestoreInstanceState(savedInstanceState);
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
