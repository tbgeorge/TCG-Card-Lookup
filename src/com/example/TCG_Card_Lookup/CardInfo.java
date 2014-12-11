package com.example.TCG_Card_Lookup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tyler on 12/11/2014.
 */
public class CardInfo extends Activity {

    final Context myApp = this;

    Bitmap image_bmp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardinfo);
        Intent intent = this.getIntent();

        String url = "http://shop.tcgplayer.com" + intent.getStringExtra("url");
        byte[] byteArray = intent.getByteArrayExtra("img");
        image_bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        renderCardDetails(url);




    }

    public void renderCardDetails(String url) {
        Log.d("htmlURL", url);
        final WebView browser = new WebView(myApp);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface2(), "HTMLOUT");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("htmlLoad", "Page finished loading");
                browser.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                Log.d("htmlGet", "Getting html...");
                view.addJavascriptInterface(this, "Android");

            }
        });

        browser.loadUrl(url);

    }

    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface2 {
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void showHTML(String html)
        {
            final String finalHTML = html;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("htmlSHOW", "Inside showHTML");

                    //Log.d("html", finalHTML);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar imgProgress = (ProgressBar) findViewById(R.id.img_progress);
                            imgProgress.setVisibility(View.GONE);
                            Document doc = Jsoup.parse(finalHTML);
                            //Log.d("htmlResponse", doc.html());

                            Element cardDetailsWrapper = doc.getElementsByClass("cardDetailsWrap").first();
//                            Element detailImage = cardDetailsWrapper.select("div img").first();
                            Element cardDetails = cardDetailsWrapper.getElementsByClass("cardDetails").first();
                            Elements details = cardDetails.select("table tr td");
                            Log.d("htmlDetailsLength", details.size() + "");

                            //String imgURL = detailImage.attr("src");

                            // show The Image
                            // DownloadImageTask((ImageView) findViewById(R.id.card_detail_image))
                                    //.execute(imgURL);
                            ((ImageView) findViewById(R.id.card_detail_image)).setImageBitmap(image_bmp);

                            TextView cardTitle = (TextView) findViewById(R.id.card_title);
                            TextView setName = (TextView) findViewById(R.id.set_name);
                            TextView number = (TextView) findViewById(R.id.number);
                            TextView rarity = (TextView) findViewById(R.id.rarity);
                            TextView cardType = (TextView) findViewById(R.id.card_type);
                            TextView level = (TextView) findViewById(R.id.level);
                            TextView atkDef = (TextView) findViewById(R.id.atk_def);
                            TextView price = (TextView) findViewById(R.id.price);
                            TextView description = (TextView) findViewById(R.id.card_description);

                            cardTitle.setSelected(true);
                            setName.setSelected(true);
                            number.setSelected(true);
                            rarity.setSelected(true);
                            cardType.setSelected(true);
                            level.setSelected(true);
                            atkDef.setSelected(true);

                            cardTitle.setText(cardDetails.select("h1").first().html());
                            price.setText(cardDetails.getElementById("lowestPriceValue").html());

                            for (Element el : details) {
                                if (el.html().equalsIgnoreCase("<b>Set Name:</b>")) {
                                    Log.d("htmlINNERHTML", el.nextElementSibling().html());
                                    setName.setText(el.nextElementSibling().select("a").first().html());
                                }
                                if (el.hasClass("descriptionCol")) {
                                    Log.d("htmlINNERHTML", el.html());
                                    if (el.html().equalsIgnoreCase("<b>Number:</b>")) {
                                        number.setText(el.nextElementSibling().html());
                                    }
                                    if (el.html().equalsIgnoreCase("<b>Rarity:</b>")) {
                                        rarity.setText(el.nextElementSibling().html());
                                    }
                                    if (el.html().equalsIgnoreCase("<b>Card Type:</b>")) {
                                        cardType.setText(el.nextElementSibling().html());
                                    }
                                    if (el.html().equalsIgnoreCase("<b>A / D:</b>")) {
                                        atkDef.setText(el.nextElementSibling().html());
                                    }
                                    if (el.html().equalsIgnoreCase("<b>Level:</b>")) {
                                        level.setText(el.nextElementSibling().html());
                                    }
                                    if (el.html().equalsIgnoreCase("<b>Description:</b>")) {
                                        String desc = el.nextElementSibling().html();
                                        desc = desc.replace("<br>", "\n");
                                        description.setText(desc);
                                    }
                                }
                            }

                            if (level.getText().toString().equalsIgnoreCase("")) {
                                ((TextView) findViewById(R.id.level_label)).setVisibility(View.GONE);
                                level.setVisibility(View.GONE);
                            }


                        }
                    });
                }
            }).start();
        }
    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }
}
