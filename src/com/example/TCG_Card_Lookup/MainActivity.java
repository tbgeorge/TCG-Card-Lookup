package com.example.TCG_Card_Lookup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button magicBtn, ygoBtn, pkmBtn, wowBtn, cvBtn;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        magicBtn = (Button) findViewById(R.id.magicButton);
        ygoBtn = (Button) findViewById(R.id.yugiohButton);
        pkmBtn = (Button) findViewById(R.id.pokemonButton);
        wowBtn = (Button) findViewById(R.id.wowButton);
        cvBtn = (Button) findViewById(R.id.cvButton);

        magicBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MagicSearch.class);
                startActivity(intent);
            }
        });


    }
}
