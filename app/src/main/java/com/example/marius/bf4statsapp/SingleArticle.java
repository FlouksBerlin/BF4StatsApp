package com.example.marius.bf4statsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;


public class SingleArticle extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);

        // GET
        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        String body = b.getString("txt");
        String date = b.getString("date");
        String gameTitle = b.getString("gameTitle");
        String imgURL = b.getString("imgUrl");

        TextView tV = (TextView) findViewById(R.id.singleArticleTitle);
        tV.setText(title);
        TextView tV2 = (TextView) findViewById(R.id.singleArticleText);
        tV2.setText(body);
        TextView tV3 = (TextView) findViewById(R.id.singlePubDate);
        tV3.setText(date + " - " + gameTitle);

        Log.e("Failed to Load Image", "ImageURL: " + imgURL);
        ImageView iV = (ImageView) findViewById(R.id.singleArticleImage);
        ImageLoader.getInstance().displayImage(imgURL, iV);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.articles) {
            Intent intent = new Intent(this, ArticlesActivity.class);
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
