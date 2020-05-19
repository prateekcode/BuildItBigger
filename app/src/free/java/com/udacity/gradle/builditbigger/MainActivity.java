package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.androidmonk.androiddisplayjokelib.JokeDetailsActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity implements JokeListener {

    private String joke;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupInterstitialAd();
    }

    private void setupInterstitialAd() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        // Create Interstitial Ad and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitialAd();

        // Show Interstitial Ad when user click button
        Button button = findViewById(R.id.button_tell_joke);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EndpointsAsyncTask(MainActivity.this).execute();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("MainActivity", "The interstitial wasn't loaded yet.");
                    if (!TextUtils.isEmpty(joke)) {
                        launchJokeActivity();
                    }
                }
            }
        });

        // launch joke activity when interstitial ad closed
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialAd();
                if (TextUtils.isEmpty(joke)) {
                    joke = "No Joke Found";
                    launchJokeActivity();
                } else {
                    launchJokeActivity();
                }
            }
        });
    }


    private void launchJokeActivity() {
        Intent intent = new Intent(this, JokeDetailsActivity.class);
        intent.putExtra(JokeDetailsActivity.EXTRA_JOKE, joke);
        startActivity(intent);
    }

    private void requestNewInterstitialAd() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            mInterstitialAd.loadAd(adRequest);
        }
    }

    @Override
    public void onJokeLoaded(String joke) {
        Log.d("onJokeLoaded", "Joke is: " + joke);
        this.joke = joke;
    }



}
