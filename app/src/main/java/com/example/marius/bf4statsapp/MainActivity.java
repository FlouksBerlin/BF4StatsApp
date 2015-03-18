package com.example.marius.bf4statsapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private Button mButton;
    private ListView mListView;
    private TextView mTextView;
    CardView mCardView;
    private List<String> meals = new ArrayList<>();
    //    private final static String urlString = "http://api.bf4stats.com/api/playerInfo?plat=pc&name=chill3rman&output=json";
    private final static String urlString = "http://api.bf4stats.com/api/playerInfo?plat=pc&name=chill3rman&opt=details,stats,extra&output=json";
    //        private final static String urlString = "http://api.bf4stats.com/api/playerRankings?plat=pc&name=chill3rman&opt=stats&output=json";
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.btn_Go);
        mTextView = (TextView) findViewById(R.id.tvPlayerName);

       /* mCardView = (CardView) findViewById(R.id.cardview);
        mCardView.setElevation(50);*/

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new JsonFetcher().execute();
            }
        });
    }

    public class JsonFetcher extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String json = "";
            String inputLine = "";

            try {
                URL url = new URL(urlString);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((inputLine = in.readLine()) != null) {
                    json += inputLine;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            String sPlayerName, sPlayerTag; // Name and Clantag: player->name/tag
            String sPlayerScore, sTimePlayed; // Score and time played: player->score/timePlayed
            String sRankNr, sRankName; //Rank number and name: rank->nr/name

            // Skill, Kills, Deaths: stats->skill/kills/headshots/deaths/killStreakBonus
            String sStatsSkill, sStatsKills, sStatsHeadshots, sStatsDeaths, sStatsKillStreak;

            // KillDeath, WinLose, ScorePerMinute, KillPerMinute: extra->/kdr/wlr/spm/kpm
            String sExtraKDR, sExtraWLR, sExtraSPM, sExtraKPM;


            try {
                JSONObject statObjMain = new JSONObject(json); // gesamtes JSON-Objekt
                JSONObject statObjOne;      // JSON-Objekt 1. Ebene
                JSONObject statObjTwo;      // JSON-Objekt 2. Ebene
                JSONObject statObjThree;    // JSON-Objekt 3. Ebene
                statObjOne = statObjMain.getJSONObject("player");

                String text = statObjOne.getString("name");
                text += statObjOne.getString("tag");
                text += statObjOne.getString("score");
                text += statObjOne.getString("timePlayed");

                statObjTwo = statObjOne.getJSONObject("rank");
                text += statObjTwo.getString("nr");
                text += statObjTwo.getString("name");

                statObjOne = statObjMain.getJSONObject("stats");
                text += statObjOne.getString("skill");
                text += statObjOne.getString("kills");
                text += statObjOne.getString("headshots");
                text += statObjOne.getString("deaths");
                text += statObjOne.getString("killStreakBonus");

                mTextView.setText(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}