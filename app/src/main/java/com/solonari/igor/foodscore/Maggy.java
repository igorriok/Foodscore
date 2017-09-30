package com.solonari.igor.foodscore;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Maggy extends AppCompatActivity {

    private ProgressDialog pDialog;
    final String TAG = "Maggy";
    ArrayList<Food> foodList;
    HashMap<Integer, Integer> foodMap;
    GridView gridView;
    TextView score, nScore, pScore;
    int category, pScorePoints, scorePoints, nScorePoints = 0;
    String NEG_SCORE = "nScorePoints";
    String POS_SCORE = "pScorePoints";
    LinearLayout tableN, tableP;
    int viewID = 100;
    ConstraintLayout root;
    SharedPreferences.Editor pEditor, nEditor, scoreEditor;
    SharedPreferences nPref, pPref, scorePref, instPref;
    View scroll, newFood;
    Toolbar myToolbar;
    Random rnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maggy);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);

        instPref = Maggy.this.getSharedPreferences(getString(R.string.instPref), Context.MODE_PRIVATE);
        if (instPref.getBoolean(getString(R.string.showInst), true)) {
            showNoticeDialog();
        }
      
        foodList = new ArrayList<>();
        foodMap = new HashMap<>();
        gridView = (GridView) findViewById(R.id.gridview);
        //gridView.setColumnWidth(200);
        new GetFood().execute();
        score = findViewById(R.id.fScoreText);
        nScore = findViewById(R.id.nScoreText);
        pScore = findViewById(R.id.pScoreText);
        tableN = findViewById(R.id.tableN);
        tableP = findViewById(R.id.tableP);
        root = findViewById(R.id.root);
        scroll = findViewById(R.id.scroll);
        //nPanel = findViewById(R.id.nScore);
        //nPanel.setClipToOutline(true);

        rnd = new Random();

        scorePref = Maggy.this.getSharedPreferences(getString(R.string.score_pref), Context.MODE_PRIVATE);
        pPref = Maggy.this.getSharedPreferences(getString(R.string.p_pref), Context.MODE_PRIVATE);
        nPref = Maggy.this.getSharedPreferences(getString(R.string.n_pref), Context.MODE_PRIVATE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(Maggy.this, "Position: " + position + ", id: " + id, Toast.LENGTH_LONG).show();
                int points = (int) v.getTag(R.string.food_points);
                Log.d(TAG, "view points: " + points);
                //newFood = new ImageView(Maggy.this);
                newFood = View.inflate(Maggy.this, R.layout.list_item2, null);
                newFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                newFood.setPadding(4,4,4,4);

                //newFood.setId(viewID+1);
                //Find the TextView in the list_item.xml layout with the ID version_name
                //TextView nameTextView = (TextView) newFood.findViewById(R.id.version_name);
                //nameTextView.setText(foodList.get((int)id).getName());
                //nameTextView.setId(viewID++);

                //newFood.setImageResource(R.drawable.i1);
                // Find the ImageView in the list_item.xml layout with the ID list_item_icon
                ImageView iconView = newFood.findViewById(R.id.list_item_icon);
                int imgRes = Maggy.this.getResources().getIdentifier("f" + foodList.get((int)id).getID(), "drawable", Maggy.this.getPackageName());
                iconView.setImageResource(imgRes);
                iconView.setId(viewID++);

                newFood.setTag(R.string.food_points, points);
                newFood.setTag(R.string.food_id, v.getTag(R.string.food_id));
                
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
                /*
                Transition move = new ChangeTransform()
                        .addTarget(newFood)
                        .setDuration(1000);
                */

                TransitionSet move  = new TransitionSet()
                        .addTarget(newFood)
                        .setDuration(2000)
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeTransform());

                TransitionManager.beginDelayedTransition(root, move);

                root.removeView(newFood);

                newFood.setX(0);
                newFood.setY(0);

                //newFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                newFood.setPadding(4,4,4,4);
                newFood.setElevation(4);

                setPoints(points, newFood);

                addClickListener(newFood);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setPoints(int points, View newView) {

        if (points > 0) {
            tableP.addView(newView, 0);
            newFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            newFood.setPadding(4,4,4,4);
            newFood.setElevation(4);
            pScorePoints += points;
            pScore.setText("+" + String.valueOf(pScorePoints));
        } else {
            tableN.addView(newView, 0);
            newFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            newFood.setPadding(4,4,4,4);
            newFood.setElevation(4);
            nScorePoints += points;
            nScore.setText(String.valueOf(nScorePoints));
        }
        setScore();
        saveState();
    }
  
    public void setState() {
        nScorePoints = scorePref.getInt(NEG_SCORE, 0);
        pScorePoints = scorePref.getInt(POS_SCORE, 0);
        pScore.setText("+" + String.valueOf(pScorePoints));
        nScore.setText(String.valueOf(nScorePoints));
        setScore();
      
        Map<String, ?> pViews = pPref.getAll();
        for (Map.Entry<String,?> entry : pViews.entrySet()) {
            View oldFood = createView(Integer.valueOf(entry.getValue().toString()));
            tableP.addView(oldFood, 0);
            oldFood.setPadding(4,4,4,4);
            oldFood.setElevation(4);
            addClickListener(oldFood);
        }

        Map<String, ?> nViews = nPref.getAll();
        for (Map.Entry<String,?> entry : nViews.entrySet()) {
            View oldFood = createView(Integer.valueOf(entry.getValue().toString()));
            tableN.addView(oldFood, 0);
            oldFood.setPadding(4,4,4,4);
            oldFood.setElevation(4);
            addClickListener(oldFood);
        }
    }

    public void addClickListener(View foodView) {
        foodView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) v.getParent()).removeView(v);
                int viewPoints = (int) v.getTag(R.string.food_points);
                scorePoints -= viewPoints;
                if (viewPoints > 0) {
                    pScorePoints -= viewPoints;
                    pScore.setText("+" + String.valueOf(pScorePoints));
                } else {
                    nScorePoints -= viewPoints;
                    nScore.setText(String.valueOf(nScorePoints));
                }
                setScore();
            }
        });
    }

    public View createView (int id) {
        View oldFood = View.inflate(Maggy.this, R.layout.list_item, null);
        oldFood.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ImageView iconView = oldFood.findViewById(R.id.list_item_icon);
        int imgRes = this.getResources().getIdentifier("f" + id, "drawable", this.getPackageName());
        iconView.setImageResource(imgRes);
        iconView.setId(viewID++);
        oldFood.setPadding(2,2,2,2);
        oldFood.setElevation(4);
        oldFood.setTag(R.string.food_id, id);
        oldFood.setTag(R.string.food_points, foodMap.get(id));
        return oldFood;
    }
  
    public void saveState() {
        scoreEditor = scorePref.edit();
        scoreEditor.putInt(POS_SCORE, pScorePoints);
        scoreEditor.putInt(NEG_SCORE, nScorePoints);
        scoreEditor.apply();

        pEditor = pPref.edit();
        pEditor.clear();
        for (int i=0; i<tableP.getChildCount(); i++) {
            pEditor.putInt(Integer.toString(i), (int) tableP.getChildAt(i).getTag(R.string.food_id));
        }
        pEditor.apply();

        nEditor = nPref.edit();
        nEditor.clear();
        for (int i=0; i<tableN.getChildCount(); i++) {
            nEditor.putInt(Integer.toString(i), (int) tableN.getChildAt(i).getTag(R.string.food_id));
        }
        nEditor.apply();
    }

    public void setScore() {
        int points = nScorePoints + pScorePoints;
        setColor(points);
        if (points > 0) {
            score.setText("+" + String.valueOf(points));
        } else {
            score.setText(String.valueOf(points));
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
                tableN.removeAllViews();
                tableP.removeAllViews();
                pScorePoints = 0;
                nScorePoints = 0;
                pScore.setText("+" + String.valueOf(pScorePoints));
                nScore.setText(String.valueOf(nScorePoints));
                setScore();
                saveState();
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
  
    public void setColor(int points) {
        if (points > -5 && points < 5) {
            gridView.setBackgroundResource(R.color.PL0);
            scroll.setBackgroundResource(R.color.PL0);
            myToolbar.setBackgroundResource(R.color.P0);
        }
        for (int i = 5; i <= 80; i= i*2) {
            if (points >= i && points < i*2) {
                int PLResourse = this.getResources().getIdentifier("PL" + i, "color", this.getPackageName());
                gridView.setBackgroundResource(PLResourse);
                scroll.setBackgroundResource(PLResourse);
                int PResource = this.getResources().getIdentifier("P" + i, "color", this.getPackageName());
                myToolbar.setBackgroundResource(PResource);
                if (category != i) {
                    int rndNumber = rnd.nextInt(11);
                    int toastText = this.getResources().getIdentifier("pt" + rndNumber, "string", this.getPackageName());
                    Toast.makeText(getApplicationContext(), getString(toastText), Toast.LENGTH_SHORT).show();
                    category = i;
                }
            }
            if (points <= -i && points > -i*2) {
                int NLResource = this.getResources().getIdentifier("NL" + i, "color", this.getPackageName());
                gridView.setBackgroundResource(NLResource);
                scroll.setBackgroundResource(NLResource);
                int NResource = this.getResources().getIdentifier("N" + i, "color", this.getPackageName());
                myToolbar.setBackgroundResource(NResource);
                if (category != i) {
                    int rndNumber = rnd.nextInt(10);
                    int toastText = this.getResources().getIdentifier("nt" + rndNumber, "string", this.getPackageName());
                    Toast.makeText(getApplicationContext(), getString(toastText), Toast.LENGTH_SHORT).show();
                    category = i;
                }
            }
        }
    }
  
    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment instructionsDialog = new InstructionFragment();
        instructionsDialog.show(getFragmentManager(), "instFragment");
    }

    private class GetFood extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing progress dialog
            pDialog = new ProgressDialog(Maggy.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            */
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
                        int score = c.getInt("score");

                        int foodName = Maggy.this.getResources().getIdentifier("food" + id, "string", Maggy.this.getPackageName());
                        //Log.d(TAG, Integer.toString(foodName));
                        String name = getResources().getString(foodName);
                        //Log.d(TAG, id + ", " + name);

                        // tmp hash map for single contact
                        Food contact = new Food(id, name, score);
                        // adding contact to contact list
                        foodList.add(contact);
                      
                        foodMap.put(id, score);
                    }

                    Collections.sort(foodList);

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
            /* Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            */
            /**
             * Updating parsed JSON data into GridView
             * */
            FoodAdapter foodAdapter = new FoodAdapter(Maggy.this, foodList);
            gridView.setAdapter(foodAdapter);
            Log.d(TAG, "foodAdapter set");
            setState();
        }
    }
}
