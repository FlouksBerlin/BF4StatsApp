package com.example.marius.bf4statsapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private Button mButton;
    private ListView mListView;


    // Variable for Material CardView
    CardView mCardView;

    // Variables for Soldier-Info (mTV = mTextView)
    private TextView mTV_Name;
    private TextView mTV_Tag;
    private TextView mTV_Score;
    private TextView mTV_TimePlayed;
    private TextView mTV_RankNr;
    private TextView mTV_RankName;
    private TextView mTV_Skill;
    private TextView mTV_Kills;
    private TextView mTV_Headshots;
    private TextView mTV_Deaths;
    private TextView mTV_KillStreak;
    private TextView mTV_KDR;
    private TextView mTV_WLR;
    private TextView mTV_SPM;
    private TextView mTV_KPM;

    // Variables for Images in Views
    private ImageView mIV_Rank;

    private ProgressBar spinner;

    private List<String> meals = new ArrayList<>();
    //    private final static String urlString = "http://api.bf4stats.com/api/playerInfo?plat=pc&name=chill3rman&output=json";
    private static String urlString = "http://api.bf4stats.com/api/playerInfo?plat=pc&name=chill3rman&opt=details,stats,extra&output=json";
    //        private final static String urlString = "http://api.bf4stats.com/api/playerRankings?plat=pc&name=chill3rman&opt=stats&output=json";
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mButton = (Button) findViewById(R.id.btn_Go);


        mTV_Name = (TextView) findViewById(R.id.tvName);
        mTV_Tag = (TextView) findViewById(R.id.tvTag);
        mTV_Score = (TextView) findViewById(R.id.tvScore);
        mTV_TimePlayed = (TextView) findViewById(R.id.tvTime);
//        mTV_RankNr = (TextView) findViewById(R.id.tvRankNr);
        mTV_RankName = (TextView) findViewById(R.id.tvRank);
        mTV_Skill = (TextView) findViewById(R.id.tvSkill);
        mTV_Kills = (TextView) findViewById(R.id.tvKills);
        mTV_Headshots = (TextView) findViewById(R.id.tvHeadshots);
        mTV_Deaths = (TextView) findViewById(R.id.tvDeaths);
        mTV_KillStreak = (TextView) findViewById(R.id.tvKillStreak);
        mTV_KDR = (TextView) findViewById(R.id.tvKDR);
        mTV_WLR = (TextView) findViewById(R.id.tvWLR);
        mTV_SPM = (TextView) findViewById(R.id.tvSPM);
        mTV_KPM = (TextView) findViewById(R.id.tvKPM);

        mIV_Rank = (ImageView) findViewById(R.id.rankImg);

       /* mCardView = (CardView) findViewById(R.id.cardview);
        mCardView.setElevation(50);*/


        spinner = (ProgressBar)findViewById(R.id.progressBarM);
        spinner.setVisibility(View.GONE);


        String FILENAME = "lastPlayer";
        try {
            FileInputStream fos = openFileInput(FILENAME);
            StringBuffer fileContent = new StringBuffer("");
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = fos.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
            fos.close();
            new JsonFetcher().onPostExecute(fileContent.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        handleIntent(getIntent());
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, ComparisonActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            notificationCaller();
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            urlString = "http://api.bf4stats.com/api/playerInfo?plat=pc&name=" + query + "&opt=details,stats,extra&output=json";
            spinner.setVisibility(View.VISIBLE);
            new JsonFetcher().execute();
        }

    }

    public void notificationCaller(){
        Notification.Builder mBuilder =
                new Notification.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("BF4 RangUp")
                        .setContentText("A Friend just got PROMOTED!")
                        .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/promoted"));
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
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
                in.close();
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

            if (json.equals("")) {
                Toast.makeText(getApplicationContext(), "Keine Daten gefunden!", Toast.LENGTH_LONG).show();
                return;
            }

            // Name and Clantag: player->name/tag  // Score and time played: player->score/timePlayed
            String sPlayerName, sPlayerTag, sPlayerScore;
            int sTimePlayed;
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

                sPlayerName = statObjOne.getString("name");
                sPlayerTag = statObjOne.getString("tag");
                //Check for existing Clantag
                if (!sPlayerTag.isEmpty()) {
                    sPlayerTag = "[" + sPlayerTag + "]";
                    Log.d("MainActivity", "Clan-Tag: Kein Tag vorhanden");
                }

                //TimePlayed to Hour (round)
                sPlayerScore = statObjOne.getString("score");
                double toHour = Integer.parseInt(statObjOne.getString("timePlayed"));
                toHour = Math.round(toHour/3600);
                sTimePlayed = (int) Math.round(toHour);

                statObjTwo = statObjOne.getJSONObject("rank");
                sRankNr = statObjTwo.getString("nr");
                sRankName = statObjTwo.getString("name");

                statObjOne = statObjMain.getJSONObject("stats");
                sStatsSkill = statObjOne.getString("skill");
                sStatsKills = statObjOne.getString("kills");
                sStatsHeadshots = statObjOne.getString("headshots");
                sStatsDeaths = statObjOne.getString("deaths");
                sStatsKillStreak = statObjOne.getString("killStreakBonus");

                statObjTwo = statObjOne.getJSONObject("extra");
                // Round for 2 digits after the dot
                sExtraKDR = statObjTwo.getString("kdr");
                String [] sArrayKDR = sExtraKDR.split("\\.");
                if (sArrayKDR.length == 2) {
                    sExtraKDR = sArrayKDR[0] + "." + sArrayKDR[1].substring(0,2);
                } else {
                    sExtraKDR = sArrayKDR[0] + ".00";
                }

                // Round for 2 digits after the dot
                sExtraWLR = statObjTwo.getString("wlr");
                String [] sArrayWLR = sExtraWLR.split("\\.");
                if (sArrayWLR.length == 2) {
                    sExtraWLR = sArrayWLR[0] + "." + sArrayWLR[1].substring(0,2);
                } else {
                    sExtraWLR = sArrayWLR[0] + ".00";
                }

                // Round for 2 digits after the dot
                sExtraSPM = statObjTwo.getString("spm");
                String [] sArraySPM = sExtraSPM.split("\\.");
                if (sArraySPM.length == 2) {
                    sExtraSPM = sArraySPM[0] + "." + sArraySPM[1].substring(0,2);
                } else {
                    sExtraSPM = sArraySPM[0] + ".00";
                }

                // Round for 2 digits after the dot
                sExtraKPM = statObjTwo.getString("kpm");
                 String [] sArrayKPM = sExtraKPM.split("\\.");
                if (sArrayKPM.length == 2) {
                    sExtraKPM = sArrayKPM[0] + "." + sArrayKPM[1].substring(0,2);
                } else {
                    sExtraKPM = sArrayKPM[0] + ".00";
                }



                /* Load values into the Layout */

                //Load RankImage from Assets based on Rank
                String imgString = "ranks/r" + sRankNr + ".png";
                try {
                    // get input stream
                    InputStream ims = getAssets().open(imgString);
                    // load image as Drawable
                    Drawable d = Drawable.createFromStream(ims, null);
                    // set image to ImageView
                    mIV_Rank.setImageDrawable(d);
                } catch (IOException ex) {
                    return;
                }

                mTV_Name.setText(sPlayerName);
                mTV_Tag.setText(sPlayerTag);
                mTV_Score.setText(sPlayerScore);
                mTV_TimePlayed.setText(""+sTimePlayed+"H");
//                mTV_RankNr.setText(sRankNr);
                mTV_RankName.setText(sRankName);
                mTV_Skill.setText(sStatsSkill);
                mTV_Kills.setText(sStatsKills);
                mTV_Headshots.setText(sStatsHeadshots);
                mTV_Deaths.setText(sStatsDeaths);
                mTV_KillStreak.setText(sStatsKillStreak);
                mTV_KDR.setText(sExtraKDR);
                mTV_WLR.setText(sExtraWLR);
                mTV_SPM.setText(sExtraSPM);
                mTV_KPM.setText(sExtraKPM);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String FILENAME = "lastPlayer";
            try {
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(json.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            spinner.setVisibility(View.GONE);

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

        if (id == R.id.news) {
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.comparison) {
            Intent intent = new Intent(this, ComparisonActivity.class);
            startActivity(intent);
        }

        if (id == R.id.deals) {
            Intent intent = new Intent(this, DealsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
