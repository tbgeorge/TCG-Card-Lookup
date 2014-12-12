package com.example.TCG_Card_Lookup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 12/11/2014.
 */
public class SearchResults extends Activity {

    final Context myApp = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchresults);

        String url = getIntent().getStringExtra("url");

        renderSearchResults(url);

    }

    /********************* THE PROPER WAY ****** Sometimes doesn't work  **********************/

    /***** Async pull source code from TCGPlayer *****/
//    private class JSOUPget extends AsyncTask<String, Void, Document> {
//
//        protected Document doInBackground(String... urls) {
//
//            Document resultHTML = null;
//            //Pull source code
//            try {
//                resultHTML = Jsoup.connect(urls[0]).get();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return resultHTML;
//        }
//
//        protected void onPostExecute(Document result) {
//            parseHTML(result);
//        }
//    }
//
//    public void parseHTML(Document doc) {
//        LinearLayout progress = (LinearLayout) findViewById(R.id.progress);
//        progress.setVisibility(View.GONE);
//
//        LinearLayout rootElem = (LinearLayout) findViewById(R.id.rootSearchElement);
//
//        Log.d("htmlResponse", doc.html());
//        //Seller Containers
//        Elements sellerContainers = doc.getElementsByClass("sellerContainer");
//
//        TextView resultCount = new TextView(myApp);
//        resultCount.setText("Found " + sellerContainers.size() + " results.");
//        rootElem.addView(resultCount);
//
//        //Getting Card Titles
//        List<Element> titles = new ArrayList<Element>();
//        for(Element div : sellerContainers) {
//            Elements title = div.select("h2 a");
//            for(Element t : title)
//                titles.add(t);
//        }
//
//        for(Element t : titles) {
//            TextView cardTitle = new TextView(myApp);
//            cardTitle.setText(t.html());
//            rootElem.addView(cardTitle);
//
//            final String link = t.attr("href");
//            cardTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(YugiohSearch.this, CardInfo.class);
//                    intent.putExtra("url", link);
//                    startActivity(intent);
//                }
//            });
//        }

//        TextView htmlView = new TextView(myApp);
//        htmlView.setText(html);
//        rootElem.addView(htmlView);
//    }

    /******************************************************************************/


    /*********************** THE JANK WAY ****************************************/
    public void renderSearchResults(String url) {
        final WebView browser = new WebView(myApp);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("htmlLoad", "Page finished loading...");
                browser.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                Log.d("htmlGet", "Getting html...");
            }
        });

        browser.loadUrl(url);

    }

    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface
    {
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void showHTML(String html)
        {
//            Log.d("htmlSHOW", "Inside showHTML");
            final String finalHTML = html;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
                            progress.setVisibility(View.GONE);

                            final LinearLayout rootElem = (LinearLayout) findViewById(R.id.rootSearchElement);
                            LinearLayout header = (LinearLayout) findViewById(R.id.header);

                            Document doc = Jsoup.parse(finalHTML);
                            //Log.d("htmlResponse", doc.html());

                            //Element cardCatalog = doc.getElementsByClass("cardCatalog").first();
                            Elements catalogDivs = doc.select("div.cardCatalog > div");
                            List<Element> cards = new ArrayList<Element>();
                            for (Element e : catalogDivs) {
                                if (!e.hasAttr("class"))
                                    cards.add(e);
                            }
                            //Log.d("NumberOfCards", "" + cards.size());
                            int counter = 0;
                            for (Element card : cards) {
                                LinearLayout cardLayout = new LinearLayout(myApp);
                                cardLayout.setOrientation(LinearLayout.HORIZONTAL);

                                ImageView cardImage = new ImageView(myApp);
                                String cardURL = card.getElementsByClass("imageContainer").first().select("img.productImage").first().attr("src");
                                final String link = card.getElementsByClass("sellerContainer").first().select("h2 a").first().attr("href");
                                new DownloadImageTask(cardImage, cardLayout, link).execute(cardURL);

                                TextView cardTitle = new TextView(myApp);
                                String title = card.getElementsByClass("sellerContainer").first().select("h2 a").first().html();
                                cardTitle.setText(title);
                                cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

                                cardLayout.addView(cardImage);
                                cardLayout.addView(cardTitle);

                                LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                cardLayoutParams.setMargins(dipToPixels(myApp, 10f), dipToPixels(myApp, 5f), dipToPixels(myApp, 10f), dipToPixels(myApp, 5f));

                                cardLayout.setLayoutParams(cardLayoutParams);

                                rootElem.addView(cardLayout);
                                counter++;

                                if(!(counter == cards.size())) {

                                    ImageView seperator = new ImageView(myApp);
                                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPixels(myApp, 3f));
                                    seperator.setBackgroundColor(Color.parseColor("#33cccccc"));
                                    seperator.setLayoutParams(params);

                                    rootElem.addView(seperator);
                                }


                            }

                            //Seller Containers
                            //                    Elements sellerContainers = doc.getElementsByClass("sellerContainer");

                            String results = doc.getElementsByClass("breadcrumb").first().select("b").first().html();
                            TextView resultCount = new TextView(myApp);
                            resultCount.setText(results);
                            header.addView(resultCount);

                            TextView searchHdr = (TextView) findViewById(R.id.search_header);
                            searchHdr.setText("Search Results");

                            //Getting Card Titles
                            //                    List<Element> titles = new ArrayList<Element>();
                            //                    for(Element div : sellerContainers) {
                            //                        Elements title = div.select("h2 a");
                            //                        for(Element t : title)
                            //                            titles.add(t);
                            //                    }
                            //                    List<String> imgURLs = new ArrayList<String>();
                            //
                            //
                            //                    for(Element t : titles) {
                            //                        TextView cardTitle = new TextView(myApp);
                            //                        cardTitle.setText(t.html());
                            //                        rootElem.addView(cardTitle);


                            //Pages
                            Button prevBtn = (Button) findViewById(R.id.prev_btn);
                            Button nextBtn = (Button) findViewById(R.id.next_btn);
                            TextView pageNum = (TextView) findViewById(R.id.page_num);

                            Element pageView = doc.getElementsByClass("pageView").first();
                            if (pageView.getElementsByClass("pageList").size() == 0) {
                                prevBtn.setVisibility(View.GONE);
                                nextBtn.setVisibility(View.GONE);
                                pageNum.setVisibility(View.GONE);
                            } else {
                                //Check if on first page
                                if (pageView.getElementsByClass("prevPage").first().hasAttr("disabled")) {
                                    prevBtn.setVisibility(View.GONE);
                                } else {
                                    final String prevURL = "http://shop.tcgplayer.com"
                                            + pageView.getElementsByClass("prevPage").first().attr("href");
                                    prevBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("PrevButton", "pressed...");
                                            Intent intent = new Intent(SearchResults.this, SearchResults.class);
                                            intent.putExtra("url", prevURL);
                                            startActivity(intent);
                                        }
                                    });
                                }

                                //check if on last page
                                if (pageView.getElementsByClass("nextPage").first().hasAttr("disabled")) {
                                    nextBtn.setVisibility(View.GONE);
                                } else {
                                    Log.d("NextButton", "Not on last page");
                                    final String nextURL = "http://shop.tcgplayer.com"
                                            + pageView.getElementsByClass("nextPage").first().attr("href");
                                    Log.d("NextButton", nextURL);
                                    nextBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("NextButton", "pressed...");
                                            Intent intent = new Intent(SearchResults.this, SearchResults.class);
                                            intent.putExtra("url", nextURL);
                                            startActivity(intent);
                                        }
                                    });
                                }

                                //get page number
                                pageNum.setText("Page " + pageView.getElementsByClass("currentPage").first().html());
                            }


                            //                    LinearLayout progress = (LinearLayout) findViewById(R.id.progress);
                            //                    progress.setVisibility(View.GONE);
                            //
                            //                    LinearLayout rootElem = (LinearLayout) findViewById(R.id.rootSearchElement);
                            //
                            //                    List<String> allMatches = new ArrayList<String>();
                            //                    Matcher m = Pattern.compile("(<div)(\\s+)(class=\"sellerContainer\">)(\\.*)(\\s*)(\\.*)(</h2>)").matcher(finalHTML);
                            //                    while(m.find()) {
                            //                        allMatches.add(m.group());
                            //                    }
                            //
                            //                    TextView resultSize = new TextView(myApp);
                            //                    resultSize.setText("Found " + allMatches.size() + " results");
                            //                    rootElem.addView(resultSize);
                            //
                            //                    for(String match : allMatches) {
                            //                        TextView matchView = new TextView(myApp);
                            //                        matchView.setText(match);
                            //                        rootElem.addView(matchView);
                            //                    }


                            //                    resultHTML.setText(finalHTML);
                            //                    rootElem.addView(resultHTML);
                        }
                    });
                }
            }).start();

        }
    }

    /******************************************************************************************/

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        LinearLayout card;
        String link;

        public DownloadImageTask(ImageView bmImage, LinearLayout card, String link) {
            this.bmImage = bmImage;
            this.card = card;
            this.link = link;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

            final Bitmap img = result;
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchResults.this, CardInfo.class);
                    intent.putExtra("url", link);

                    Bitmap myBitmap = img;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bytepicture = baos.toByteArray();

                    intent.putExtra("img", bytepicture);
                    startActivity(intent);
                }
            });
        }
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}



