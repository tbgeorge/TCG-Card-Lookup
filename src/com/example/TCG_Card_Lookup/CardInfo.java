package com.example.TCG_Card_Lookup;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Tyler on 12/11/2014.
 */
public class CardInfo extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yugiohsearch);
        Intent intent = this.getIntent();

        String url = "http://shop.tcgplayer.com" + intent.getStringExtra("url");
        new JSOUPget().execute(url);




    }


    /***** Async pull source code from bing *****/
    private class JSOUPget extends AsyncTask<String, Void, Document> {

        protected Document doInBackground(String... urls) {

            Document resultHTML = null;
            //Pull source code
            try {
                resultHTML = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return resultHTML;
        }

        protected void onPostExecute(Document result) {
            parseHTML(result);
        }
    }

    private void parseHTML(Document doc) {
        
    }
}
