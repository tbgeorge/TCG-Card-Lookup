package com.example.TCG_Card_Lookup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 12/2/2014.
 */
public class YugiohSearch extends Activity {

    String searchResultHTML = "";
    boolean loadCompleted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yugiohsearch);
        setupUI(findViewById(R.id.main));
        initSpinners();

        Button searchBtn = (Button) findViewById(R.id.searchBtn);
        final TextView testURL = (TextView) findViewById(R.id.testURL);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCompleted = false;
                String url = formURL();
                //testURL.setText(url);
                setContentView(R.layout.searchresults);
                renderSearchResults(url);
            }
        });


    }

    final Context myApp = this;

    public void renderSearchResults(String url) {
        final WebView browser = new WebView(myApp);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                browser.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
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
            final String finalHTML = html;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout progress = (LinearLayout) findViewById(R.id.progress);
                    progress.setVisibility(View.GONE);

                    LinearLayout rootElem = (LinearLayout) findViewById(R.id.rootSearchElement);
                    TextView resultHTML = new TextView(myApp);

                    resultHTML.setText(finalHTML);
                    rootElem.addView(resultHTML);
                }
            });

        }
    }

    private String formURL() {
        String baseURL = "http://shop.tcgplayer.com/yugioh";
        String url = baseURL;

        Spinner setSpinner = (Spinner) findViewById(R.id.setSpinner);
        String set = setSpinner.getSelectedItem().toString();

        EditText productName = (EditText) findViewById(R.id.productName);
        EditText description = (EditText) findViewById(R.id.description);

        Spinner cardTypeSpinner = (Spinner) findViewById(R.id.cardTypeSpinner);
        String cardType = cardTypeSpinner.getSelectedItem().toString();
        Spinner monsterTypeSpinner = (Spinner) findViewById(R.id.monsterTypeSpinner);
        String monsterType = monsterTypeSpinner.getSelectedItem().toString();

        List<CheckBox> rarities = new ArrayList<CheckBox>();
        CheckBox common = (CheckBox) findViewById(R.id.common);
        rarities.add(common);
        CheckBox rare = (CheckBox) findViewById(R.id.rare);
        rarities.add(rare);
        CheckBox superRare = (CheckBox) findViewById(R.id.superRare);
        rarities.add(superRare);
        CheckBox ultraRare = (CheckBox) findViewById(R.id.ultra);
        rarities.add(ultraRare);
        CheckBox secretRare = (CheckBox) findViewById(R.id.secret);
        rarities.add(secretRare);
        CheckBox ultimateRare = (CheckBox) findViewById(R.id.ultimate);
        rarities.add(ultimateRare);
        CheckBox ghostRare = (CheckBox) findViewById(R.id.ghost);
        rarities.add(ghostRare);

        List<CheckBox> attributes = new ArrayList<CheckBox>();
        CheckBox attrDark = (CheckBox) findViewById(R.id.dark);
        attributes.add(attrDark);
        CheckBox attrDivine = (CheckBox) findViewById(R.id.divine);
        attributes.add(attrDivine);
        CheckBox attrEarth = (CheckBox) findViewById(R.id.earth);
        attributes.add(attrEarth);
        CheckBox attrFire = (CheckBox) findViewById(R.id.fire);
        attributes.add(attrFire);
        CheckBox attrLight = (CheckBox) findViewById(R.id.light);
        attributes.add(attrLight);
        CheckBox attrWater = (CheckBox) findViewById(R.id.water);
        attributes.add(attrWater);
        CheckBox attrWind = (CheckBox) findViewById(R.id.wind);
        attributes.add(attrWind);

        Spinner levelFromSpinner = (Spinner) findViewById(R.id.levelFromSpinner);
        String levelFrom = levelFromSpinner.getSelectedItem().toString();
        Spinner levelToSpinner = (Spinner) findViewById(R.id.levelToSpinner);
        String levelTo = levelToSpinner.getSelectedItem().toString();
        Spinner attackFromSpinner = (Spinner) findViewById(R.id.attackFromSpinner);
        String attackFrom = attackFromSpinner.getSelectedItem().toString();
        Spinner attackToSpinner = (Spinner) findViewById(R.id.attackToSpinner);
        String attackTo = attackToSpinner.getSelectedItem().toString();
        Spinner defenseFromSpinner = (Spinner) findViewById(R.id.defenseFromSpinner);
        String defenseFrom = defenseFromSpinner.getSelectedItem().toString();
        Spinner defenseToSpinner = (Spinner) findViewById(R.id.defenseToSpinner);
        String defenseTo = defenseToSpinner.getSelectedItem().toString();
        Spinner priceConditionSpinner = (Spinner) findViewById(R.id.priceConditionSpinner);
        String priceCondition = priceConditionSpinner.getSelectedItem().toString();
        EditText price = (EditText) findViewById(R.id.priceEdit);

        if(set.equalsIgnoreCase("All Sets")) {
            url += "/product/show?";
        }
        else {
            set = convertSetToURLFriendly(set);
            url += "/" + set + "?";
        }

        if(!productName.getText().toString().equalsIgnoreCase(""))
            url += "ProductName=" + convertStrToURLFriendly(productName.getText().toString()) + "&";

        if(!description.getText().toString().equalsIgnoreCase(""))
            url += "Description=" + convertStrToURLFriendly(description.getText().toString()) + "&";

        if(!cardType.equalsIgnoreCase("All"))
            url += "CardType=" + convertTypeToURLFriendly(cardType) + "&";

        if(!monsterType.equalsIgnoreCase("All"))
            url += "MonsterType=" + convertTypeToURLFriendly(monsterType) + "&";


        List<String> checkedRarities = new ArrayList<String>();
        for(int i = 0; i < rarities.size(); i++) {
            if(rarities.get(i).isChecked()) {
                String boxText = rarities.get(i).getText().toString();
                if(boxText.equalsIgnoreCase("rare")) {
                    checkedRarities.add("Rare");
                }
                else if(boxText.contains("Rare")) {
                    boxText = boxText.substring(0, boxText.length() - 5);
                    checkedRarities.add(boxText);
                }
                else {
                    checkedRarities.add(boxText);
                }
            }
        }

        if(checkedRarities.size() > 0) {
            url += "Rarity=";
            for(int i = 0; i < checkedRarities.size(); i++) {
                url += checkedRarities.get(i);
                if(i != checkedRarities.size() - 1) {
                    url += "%2c";
                }
            }
            url += "&";
        }

        List<String> checkedAttributes = new ArrayList<String>();
        for(int i = 0; i < attributes.size(); i++) {
            if(attributes.get(i).isChecked()) {
                String boxText = attributes.get(i).getText().toString();
                checkedAttributes.add(boxText);
            }
        }

        if(checkedAttributes.size() > 0) {
            url += "Attribute=";
            for(int i = 0; i < checkedAttributes.size(); i++) {
                url += checkedAttributes.get(i);
                if(i != checkedAttributes.size() - 1) {
                    url += "%2c";
                }
            }
            url += "&";
        }


        if(!levelFrom.equalsIgnoreCase("any"))
            url += "Level_From=" + levelFrom + "&";

        if(!levelTo.equalsIgnoreCase("any"))
            url += "Level_To=" + levelTo + "&";

        if(!attackFrom.equalsIgnoreCase("any"))
            url += "Attack_From=" + attackFrom + "&";

        if(!attackTo.equalsIgnoreCase("any"))
            url += "Attack_To=" + attackTo + "&";

        if(!defenseFrom.equalsIgnoreCase("any"))
            url += "Defense_From=" + defenseFrom + "&";

        if(!defenseTo.equalsIgnoreCase("any"))
            url += "Defense_To=" + defenseTo + "&";

        if(!price.getText().toString().equalsIgnoreCase("")) {
            String priceStr = price.getText().toString();
            Double priceDbl;
            boolean notANumber = false;
            try {
                priceDbl = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                notANumber = true;
            }

            if(!notANumber)
                url += "Price_Condition=" + convertStrToURLFriendly(priceCondition) + "&" + "Price=" + priceStr + "&";
        }

        url = url.substring(0,url.length() - 1);


        return url;
    }

    private String convertTypeToURLFriendly(String type) {
        type = type.replace(" ", "");
        type = type.replace("-", "");

        return type;
    }

    private String convertStrToURLFriendly(String str) {
        try {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    private String convertSetToURLFriendly(String str) {
        str = str.replace("'", "");
        str = str.replace(" ", "-");
        str = str.toLowerCase();
        return str;
    }

    //Hide keyboard when tapped outside
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(YugiohSearch.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void initSpinners() {
        List<String> setArray = new ArrayList<String>();
        setArray.add("All Sets");
        setArray.add("2002 Collectors Tin");
        setArray.add("2003 Collectors Tin");
        setArray.add("2004 Collectors Tin");
        setArray.add("2005 Collectors Tin");
        setArray.add("2006 Collectors Tin");
        setArray.add("2007 Collectors Tin");
        setArray.add("2008 Collectors Tin");
        setArray.add("2009 Collectors Tin");
        setArray.add("2010 Collectors Tins");
        setArray.add("2010 Duelist Pack Collection Tin");
        setArray.add("2011 Collectors Tins");
        setArray.add("2011 Duelist Pack Tin");
        setArray.add("2012 Collectors Tin");
        setArray.add("2012 Premium Collection Tin");
        setArray.add("2013 Collectors Tins Wave 1");
        setArray.add("2013 Collectors Tins Wave 2");
        setArray.add("2013 Zexal Collection Tin");
        setArray.add("2014 Mega-Tin Mega-Pack");
        setArray.add("2014 Mega-Tins");
        setArray.add("5D's 2008 English Starter Deck");
        setArray.add("5D's 2008 Starter Deck");
        setArray.add("5D's Starter Deck 2009");
        setArray.add("Absolute Powerforce");
        setArray.add("Absolute Powerforce: Special Edition");
        setArray.add("Abyss Rising");
        setArray.add("Abyss Rising SE");
        setArray.add("American God Cards");
        setArray.add("Ancient Prophecy");
        setArray.add("Ancient Prophecy SE");
        setArray.add("Ancient Sanctuary");
        setArray.add("Anniversary Pack");
        setArray.add("Astral Pack 1");
        setArray.add("Astral Pack 2");
        setArray.add("Astral Pack 3");
        setArray.add("Astral Pack 4");
        setArray.add("Astral Pack 5");
        setArray.add("Battle Pack 2: War of the Giants");
        setArray.add("Battle Pack 2: War of the Giants – Round 2");
        setArray.add("Battle Pack 3: Monster League");
        setArray.add("Battle Pack Tournament Prize Cards");
        setArray.add("Battle Pack: Epic Dawn");
        setArray.add("Bonds Beyond Time Movie Pack");
        setArray.add("Capsule Monster Coliseum");
        setArray.add("Champion Pack 1");
        setArray.add("Champion Pack 2");
        setArray.add("Champion Pack 3");
        setArray.add("Champion Pack 4");
        setArray.add("Champion Pack 5");
        setArray.add("Champion Pack 6");
        setArray.add("Champion Pack 7");
        setArray.add("Champion Pack 8");
        setArray.add("Cosmo Blazer");
        setArray.add("Cosmo Blazer SE");
        setArray.add("Crimson Crisis");
        setArray.add("Crossroads of Chaos");
        setArray.add("Cyberdark Impact");
        setArray.add("Cybernetic Revolution");
        setArray.add("Dark Beginning 1");
        setArray.add("Dark Beginning 2");
        setArray.add("Dark Beginnings 1");
        setArray.add("Dark Beginnings 2");
        setArray.add("Dark Crisis");
        setArray.add("Dark Duel Stories");
        setArray.add("Dark Legends");
        setArray.add("Dark Legends Promo Card");
        setArray.add("Dark Revelation 1");
        setArray.add("Dark Revelation 2");
        setArray.add("Dark Revelation 3");
        setArray.add("Dark Revelation Volume 4");
        setArray.add("Dark Revelations 1");
        setArray.add("Dark Revelations 2");
        setArray.add("Dark Revelations 3");
        setArray.add("Dawn of Destiny Xbox");
        setArray.add("Demo Pack");
        setArray.add("Destiny Board Traveler Promo");
        setArray.add("Dragon's Roar Structure Deck");
        setArray.add("Dragons of Legend");
        setArray.add("Duel Disk - Yusei Version");
        setArray.add("Duel Master's Guide");
        setArray.add("Duel Terminal");
        setArray.add("Duel Terminal - Preview");
        setArray.add("Duel Terminal 1");
        setArray.add("Duel Terminal 2");
        setArray.add("Duel Terminal 3");
        setArray.add("Duel Terminal 4");
        setArray.add("Duel Terminal 5");
        setArray.add("Duel Terminal 6");
        setArray.add("Duel Terminal 7");
        setArray.add("Duel Transer Promo Cards");
        setArray.add("Duelist Alliance");
        setArray.add("Duelist Alliance: Deluxe Edition");
        setArray.add("Duelist Genesis");
        setArray.add("Duelist League Promo");
        setArray.add("Duelist of the Roses");
        setArray.add("Duelist Pack – Yugi");
        setArray.add("Duelist Pack 10 - Yusei 3");
        setArray.add("Duelist Pack 11 - Crow");
        setArray.add("Duelist Pack 7");
        setArray.add("Duelist Pack 8 - Yusei Fudo");
        setArray.add("Duelist Pack 9 – Yusei 2");
        setArray.add("Duelist Pack Aster Phoenix");
        setArray.add("Duelist Pack Chazz Princeton");
        setArray.add("Duelist Pack Collection Tin");
        setArray.add("Duelist Pack Jaden Yuki");
        setArray.add("Duelist Pack Jaden Yuki 2");
        setArray.add("Duelist Pack Special Edition");
        setArray.add("Duelist Pack Zane Truesdale");
        setArray.add("Duelist Pack: Kaiba");
        setArray.add("Duelist Revolution");
        setArray.add("Duelist Revolution SE");
        setArray.add("Elemental Energy");
        setArray.add("Elemental Hero Collection 1");
        setArray.add("Elemental Hero Collection 2");
        setArray.add("Enemy of Justice");
        setArray.add("Eternal Duelist Soul");
        setArray.add("Exclusive Tin 2009");
        setArray.add("Extreme Victory");
        setArray.add("Fire Fists Special Editon");
        setArray.add("Flaming Eternity");
        setArray.add("Forbidden Legacy");
        setArray.add("Forbidden Legacy 1");
        setArray.add("Forbidden Memories");
        setArray.add("Force of the Breaker");
        setArray.add("Galactic Overlord");
        setArray.add("Generation Force");
        setArray.add("Generation Force SE");
        setArray.add("Gladiator's Assault");
        setArray.add("Gladiator's Assault SE");
        setArray.add("Gold Series 1");
        setArray.add("Gold Series 2");
        setArray.add("Gold Series 2008");
        setArray.add("Gold Series 2009");
        setArray.add("Gold Series 3");
        setArray.add("Gold Series 4: Pyramids Edition");
        setArray.add("Gold Series: Haunted Mine");
        setArray.add("GX Duel Academy GBA Promo");
        setArray.add("GX Manga Promo");
        setArray.add("GX Next Generation Blister Pack Promo");
        setArray.add("GX Spirit Caller Promo");
        setArray.add("GX Tag Force 2 Promo");
        setArray.add("GX Tag Force Promo");
        setArray.add("GX Ultimate Beginner's Pack 1");
        setArray.add("Hidden Arsenal");
        setArray.add("Hidden Arsenal 2");
        setArray.add("Hidden Arsenal 3");
        setArray.add("Hidden Arsenal 4");
        setArray.add("Hidden Arsenal 5: Steelswarm Invasion");
        setArray.add("Hidden Arsenal 5: Steelswarm Invasion SE");
        setArray.add("Hidden Arsenal 6: Omega Xyz");
        setArray.add("Hidden Arsenal 7: Knight of Stars");
        setArray.add("Hidden Arsenal: Special Edition");
        setArray.add("Hobby League 1");
        setArray.add("Hobby League 2");
        setArray.add("Hobby League 3");
        setArray.add("Hobby League 4");
        setArray.add("Hobby League 5");
        setArray.add("Hobby League 6");
        setArray.add("Hobby League 7");
        setArray.add("Invasion of Chaos");
        setArray.add("Jaden Yuki Vol 3 GX Duelist Pack");
        setArray.add("Jesse Anderson Vol 3 GX Duelist Pack");
        setArray.add("Judgment of the Light");
        setArray.add("Judgment of the Light: Deluxe Edition");
        setArray.add("Jump Award");
        setArray.add("Labryinth of Nightmare");
        setArray.add("Labyrinth of Nightmare");
        setArray.add("Legacy of Darkness");
        setArray.add("Legacy of the Valiant");
        setArray.add("Legend of Blue Eyes White Dragon");
        setArray.add("Legendary Collection 1");
        setArray.add("Legendary Collection 2");
        setArray.add("Legendary Collection 3: Yugi's World");
        setArray.add("Legendary Collection 4: Joey's World");
        setArray.add("Legendary Collection 5D's");
        setArray.add("Light and Darkness Power Pack");
        setArray.add("Light of Destruction");
        setArray.add("Lord of the Tachyon Galaxy");
        setArray.add("Lost Millenium");
        setArray.add("Lost Millennium SE");
        setArray.add("Magic Ruler");
        setArray.add("Magician's Force");
        setArray.add("Master Collection Volume 1");
        setArray.add("Master Collection Volume 2");
        setArray.add("Mattel Action Figure Promos: Series 1");
        setArray.add("Mattel Action Figure Promos: Series 2");
        setArray.add("McDonald's Promo");
        setArray.add("McDonald's Promo Series 2");
        setArray.add("Metal Raiders");
        setArray.add("Nintendo DS Nightmare of Troubadour");
        setArray.add("Noble Knights of the Round Table Box Set");
        setArray.add("Number Hunters");
        setArray.add("Order of Chaos");
        setArray.add("Order of Chaos SE");
        setArray.add("Phantom Darkness");
        setArray.add("Pharaoh's Servant");
        setArray.add("Pharaonic Guardian");
        setArray.add("Photon Shockwave");
        setArray.add("Poseidra Value Box");
        setArray.add("Power of Chaos: Joey the Passion");
        setArray.add("Power of Chaos: Kaiba the Revenge");
        setArray.add("Power of Chaos: Yugi the Destiny");
        setArray.add("Power of the Duelist");
        setArray.add("Premium Gold");
        setArray.add("Premium Pack 1");
        setArray.add("Premium Pack 2");
        setArray.add("Primal Origin");
        setArray.add("PS2 Yugioh GX The Beginning of Destiny");
        setArray.add("Ra Yellow Mega Pack");
        setArray.add("Ra Yellow Mega Pack SE");
        setArray.add("Raging Battle");
        setArray.add("Raging Battle SE");
        setArray.add("Reshef of Destruction");
        setArray.add("Retro Pack 1");
        setArray.add("Retro Pack 2");
        setArray.add("Return of the Duelist");
        setArray.add("Return of the Duelist SE");
        setArray.add("Reverse of Arcadia Promos");
        setArray.add("Rise of Destiny");
        setArray.add("Rise of Destiny Special Edition");
        setArray.add("Samurai Assault");
        setArray.add("Shadow of Infinity");
        setArray.add("Shadow Samurai Value Box");
        setArray.add("Shadow Specters");
        setArray.add("Shining Darknss");
        setArray.add("Shonen Jump Championship Series Prize Cards");
        setArray.add("Shonen Jump Championship Series Promos");
        setArray.add("Shonen Jump Magazine Promos");
        setArray.add("Silver Dragon Value Box");
        setArray.add("Sneak Preview Series 1");
        setArray.add("Sneak Preview Series 2");
        setArray.add("Sneak Preview Series 3");
        setArray.add("Sneak Preview Series 4");
        setArray.add("Sneak Preview Series 5");
        setArray.add("Soul of the Duelist");
        setArray.add("Spellcaster's Command Structure Deck");
        setArray.add("Stairway to the Destined Duel");
        setArray.add("Star Pack 2013");
        setArray.add("Star Pack 2014");
        setArray.add("Stardust Accelerator Promos");
        setArray.add("Stardust Overdrive");
        setArray.add("Starstrike Blast");
        setArray.add("Starter Deck 2006");
        setArray.add("Starter Deck Jaden Yuki");
        setArray.add("Starter Deck Joey");
        setArray.add("Starter Deck Kaiba");
        setArray.add("Starter Deck Kaiba Evolution");
        setArray.add("Starter Deck Pegasus");
        setArray.add("Starter Deck Syrus Truesdale");
        setArray.add("Starter Deck Yu-Gi-Oh");
        setArray.add("Starter Deck Yu-Gi-Oh Evolution");
        setArray.add("Starter Deck: Dawn of the Xyz");
        setArray.add("Starter Deck: Duelist Toolbox");
        setArray.add("Starter Deck: Kaiba Reloaded");
        setArray.add("Starter Deck: Xyz Symphony");
        setArray.add("Starter Deck: Yugi Reloaded");
        setArray.add("Storm of Ragnarok");
        setArray.add("Storm of Ragnarok SE");
        setArray.add("Strike of Neos");
        setArray.add("Structure Deck Blaze of Destruction");
        setArray.add("Structure Deck Dinosaur's Rage");
        setArray.add("Structure Deck Dragon's Roar");
        setArray.add("Structure Deck Fury from the Deep");
        setArray.add("Structure Deck Invincible Fortress");
        setArray.add("Structure Deck Lord of the Storm");
        setArray.add("Structure Deck Machine Re-Volt");
        setArray.add("Structure Deck Rise of the Dragon Lords");
        setArray.add("Structure Deck Spellcaster's Judgment");
        setArray.add("Structure Deck Warrior's Triumph");
        setArray.add("Structure Deck Zombie Madness");
        setArray.add("Structure Deck: Cyber Dragon Revolution");
        setArray.add("Structure Deck: Dragons Collide");
        setArray.add("Structure Deck: Dragunity Legion");
        setArray.add("Structure Deck: Gates of the Underworld");
        setArray.add("Structure Deck: Geargia Rampage");
        setArray.add("Structure Deck: HERO Strike");
        setArray.add("Structure Deck: Lost Sanctuary");
        setArray.add("Structure Deck: Machina Mayhem");
        setArray.add("Structure Deck: Marik");
        setArray.add("Structure Deck: Onslaught of the Fire Kings");
        setArray.add("Structure Deck: Realm of Light");
        setArray.add("Structure Deck: Realm of the Sea Emperor");
        setArray.add("Structure Deck: Saga of Blue-Eyes White Dragon");
        setArray.add("Structure Deck: Samurai Warlords");
        setArray.add("Structure Deck: The Dark Emperor");
        setArray.add("Structure Deck: Warrior's Strike");
        setArray.add("Structure Deck: Zombie World");
        setArray.add("Super Starter: Space-Time Showdown");
        setArray.add("Super Starter: Space-Time Showdown Power-Up Pack");
        setArray.add("Super Starter: V for Victory");
        setArray.add("Super Starter: V for Victory Power-Up Pack");
        setArray.add("Tactical Evolution");
        setArray.add("Tactical Evolution: Special Edition");
        setArray.add("The Dark Emperor Structure Deck");
        setArray.add("The Duelist Genesis");
        setArray.add("The Falsebound Kingdom");
        setArray.add("The Legend of Blue Eyes White Dragon");
        setArray.add("The New Challengers");
        setArray.add("The Sacred Cards");
        setArray.add("The Shining Darkness");
        setArray.add("The Valuable Book Volume 5");
        setArray.add("Tournament Pack 1");
        setArray.add("Tournament Pack 2");
        setArray.add("Tournament Pack 3");
        setArray.add("Tournament Pack 4");
        setArray.add("Tournament Pack 5");
        setArray.add("Tournament Pack 6");
        setArray.add("Tournament Pack 7");
        setArray.add("Tournament Pack 8");
        setArray.add("Toys &quot;R&quot; Us Throwdown Promo");
        setArray.add("Turbo Pack Booster One Pack");
        setArray.add("Turbo Pack: Booster Eight");
        setArray.add("Turbo Pack: Booster Five");
        setArray.add("Turbo Pack: Booster Four");
        setArray.add("Turbo Pack: Booster Seven");
        setArray.add("Turbo Pack: Booster Six");
        setArray.add("Turbo Pack: Booster Three");
        setArray.add("Turbo Pack: Booster Two");
        setArray.add("Twilight Edition");
        setArray.add("Ultimate Edition 2");
        setArray.add("Unknown");
        setArray.add("World Championship 2004: GBA Promo");
        setArray.add("World Championship 2005: 7 Trials to Glory");
        setArray.add("World Championship 2006: Ultimate Masters");
        setArray.add("World Championship 2007");
        setArray.add("World Championship 2008");
        setArray.add("World Championship 2008 DS Game");
        setArray.add("World Championship Series");
        setArray.add("X-Saber Power-Up");
        setArray.add("Yu-Gi-Oh! 5D's Manga");
        setArray.add("Yu-Gi-Oh! 5D's Over the Nexus promotional cards");
        setArray.add("Yu-Gi-Oh! 5D's Reverse of Arcadia Promo");
        setArray.add("Yu-Gi-Oh! 5D's Tag Force 4 Promo");
        setArray.add("Yu-Gi-Oh! 5D's Tag Force 5 Promotional Cards");
        setArray.add("Yu-Gi-Oh! 5D's Wheelie Breakers Promotional Cards");
        setArray.add("Yu-Gi-Oh! Championship Series Prize Cards");
        setArray.add("Yu-Gi-Oh! GX Manga Promo Cards: Series 4");
        setArray.add("Yu-Gi-Oh! GX Manga Promo Cards: Series 6");
        setArray.add("Yu-Gi-Oh! GX Manga Promotional Cards");
        setArray.add("Yu-Gi-Oh! GX Tag Force 3 Promotional Cards");
        setArray.add("Yu-Gi-Oh! GX Tag Force Evolution Promo");
        setArray.add("Yu-Gi-Oh! GX Volume 5 Promo");
        setArray.add("Yu-Gi-Oh! GX Volume 8 Promo");
        setArray.add("Yu-Gi-Oh! Movie Exclusive Pack");
        setArray.add("Yu-Gi-Oh! R");
        setArray.add("Yu-Gi-Oh! The Movie Promo Set");
        setArray.add("Yu-Gi-Oh! Tokens");
        setArray.add("Yu-Gi-Oh! ZEXAL Manga Promotional Cards");
        setArray.add("Yugioh 5D's 2008 Starter Deck");
        setArray.add("Yugioh GX Manga Vol.2");

        ArrayAdapter<String> setAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, setArray);

        setAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sets = (Spinner) findViewById(R.id.setSpinner);
        sets.setAdapter(setAdapter);

        List<String> cardTypeArray = new ArrayList<String>();
        cardTypeArray.add("All");
        cardTypeArray.add("Main Deck Monster");
        cardTypeArray.add("Extra Deck Monster");
        cardTypeArray.add("Spell");
        cardTypeArray.add("Trap");

        ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, cardTypeArray);

        cardTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner cardTypes = (Spinner) findViewById(R.id.cardTypeSpinner);
        cardTypes.setAdapter(cardTypeAdapter);

        List<String> monsterTypeArray = new ArrayList<String>();
        monsterTypeArray.add("All");
        monsterTypeArray.add("Aqua");
        monsterTypeArray.add("Beast");
        monsterTypeArray.add("Beast-Warrior");
        monsterTypeArray.add("Creator God");
        monsterTypeArray.add("Dinosaur");
        monsterTypeArray.add("Divine-Beast");
        monsterTypeArray.add("Dragon");
        monsterTypeArray.add("Fairy");
        monsterTypeArray.add("Fiend");
        monsterTypeArray.add("Fish");
        monsterTypeArray.add("Insect");
        monsterTypeArray.add("Machine");
        monsterTypeArray.add("Plant");
        monsterTypeArray.add("Psychic");
        monsterTypeArray.add("Pyro");
        monsterTypeArray.add("Reptile");
        monsterTypeArray.add("Rock");
        monsterTypeArray.add("Sea Serpent");
        monsterTypeArray.add("Spellcaster");
        monsterTypeArray.add("Thunder");
        monsterTypeArray.add("Warrior");
        monsterTypeArray.add("Winged Beast");
        monsterTypeArray.add("Zombie");

        ArrayAdapter<String> monsterTypeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, monsterTypeArray);

        monsterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner monsterTypes = (Spinner) findViewById(R.id.monsterTypeSpinner);
        monsterTypes.setAdapter(monsterTypeAdapter);

        List<String> levelRankArray = new ArrayList<String>();
        levelRankArray.add("Any");
        levelRankArray.add("1");
        levelRankArray.add("2");
        levelRankArray.add("3");
        levelRankArray.add("4");
        levelRankArray.add("6");
        levelRankArray.add("7");
        levelRankArray.add("8");
        levelRankArray.add("9");
        levelRankArray.add("10");
        levelRankArray.add("11");

        ArrayAdapter<String> levelRankAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, levelRankArray);

        levelRankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner levelRankFrom = (Spinner) findViewById(R.id.levelFromSpinner);
        Spinner levelRankTo = (Spinner) findViewById(R.id.levelToSpinner);
        levelRankFrom.setAdapter(levelRankAdapter);
        levelRankTo.setAdapter(levelRankAdapter);

        List<String> attackArray = new ArrayList<String>();
        attackArray.add("Any");
        attackArray.add("0");
        attackArray.add("100");
        attackArray.add("200");
        attackArray.add("300");
        attackArray.add("400");
        attackArray.add("500");
        attackArray.add("600");
        attackArray.add("700");
        attackArray.add("800");
        attackArray.add("900");
        attackArray.add("1000");
        attackArray.add("1100");
        attackArray.add("1200");
        attackArray.add("1300");
        attackArray.add("1400");
        attackArray.add("1500");
        attackArray.add("1600");
        attackArray.add("1700");
        attackArray.add("1800");
        attackArray.add("1900");
        attackArray.add("2000");
        attackArray.add("2100");
        attackArray.add("2200");
        attackArray.add("2300");
        attackArray.add("2400");
        attackArray.add("2500");
        attackArray.add("2600");
        attackArray.add("2700");
        attackArray.add("2800");
        attackArray.add("2900");
        attackArray.add("3000");
        attackArray.add("3100");
        attackArray.add("3200");
        attackArray.add("3300");
        attackArray.add("3400");
        attackArray.add("3500");
        attackArray.add("3600");
        attackArray.add("3700");
        attackArray.add("3800");
        attackArray.add("3900");
        attackArray.add("4000");
        attackArray.add("4100");
        attackArray.add("4200");
        attackArray.add("4300");
        attackArray.add("4400");
        attackArray.add("4500");
        attackArray.add("4600");
        attackArray.add("4700");
        attackArray.add("4800");
        attackArray.add("4900");
        attackArray.add("5000");

        ArrayAdapter<String> attackAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, attackArray);

        attackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner attackFrom = (Spinner) findViewById(R.id.attackFromSpinner);
        Spinner attackTo = (Spinner) findViewById(R.id.attackToSpinner);
        attackFrom.setAdapter(attackAdapter);
        attackTo.setAdapter(attackAdapter);

        List<String> defenseArray = new ArrayList<String>();
        defenseArray.add("Any");
        defenseArray.add("0");
        defenseArray.add("100");
        defenseArray.add("200");
        defenseArray.add("300");
        defenseArray.add("400");
        defenseArray.add("500");
        defenseArray.add("600");
        defenseArray.add("700");
        defenseArray.add("800");
        defenseArray.add("900");
        defenseArray.add("1000");
        defenseArray.add("1100");
        defenseArray.add("1200");
        defenseArray.add("1300");
        defenseArray.add("1400");
        defenseArray.add("1500");
        defenseArray.add("1600");
        defenseArray.add("1700");
        defenseArray.add("1800");
        defenseArray.add("1900");
        defenseArray.add("2000");
        defenseArray.add("2100");
        defenseArray.add("2200");
        defenseArray.add("2300");
        defenseArray.add("2400");
        defenseArray.add("2500");
        defenseArray.add("2600");
        defenseArray.add("2700");
        defenseArray.add("2800");
        defenseArray.add("2900");
        defenseArray.add("3000");
        defenseArray.add("3100");
        defenseArray.add("3200");
        defenseArray.add("3300");
        defenseArray.add("3400");
        defenseArray.add("3500");
        defenseArray.add("3600");
        defenseArray.add("3700");
        defenseArray.add("3800");
        defenseArray.add("3900");
        defenseArray.add("4000");


        ArrayAdapter<String> defenseAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, defenseArray);

        defenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner defenseFrom = (Spinner) findViewById(R.id.defenseFromSpinner);
        Spinner defenseTo = (Spinner) findViewById(R.id.defenseToSpinner);
        defenseFrom.setAdapter(defenseAdapter);
        defenseTo.setAdapter(defenseAdapter);

        List<String> priceConditionArray = new ArrayList<String>();
        priceConditionArray.add("Less Than");
        priceConditionArray.add("Equal To");
        priceConditionArray.add("Greater Than");

        ArrayAdapter<String> priceConditionAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, priceConditionArray);

        levelRankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner priceCondition = (Spinner) findViewById(R.id.priceConditionSpinner);
        priceCondition.setAdapter(priceConditionAdapter);
    }

}