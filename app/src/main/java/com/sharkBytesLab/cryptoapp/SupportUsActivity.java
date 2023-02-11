package com.sharkBytesLab.cryptoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

import java.util.concurrent.TimeUnit;

public class SupportUsActivity extends AppCompatActivity {

    private MaxInterstitialAd interstitialAd;
    private int retry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_us);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        createInterstitialAd();

    }

    private void createInterstitialAd() {
        interstitialAd = new MaxInterstitialAd(getResources().getString(R.string.applovin_inter_adId), this);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.v("ads loaded", "true");
                try {
                    if (interstitialAd.isReady()) {
                        interstitialAd.showAd();
                    }
                } catch (Exception e) {
                    Log.v("Error", e.getMessage().toString());
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.e("Reset Error", "Displayed");
            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                retry++;
                long delay = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retry)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.loadAd();
                    }
                }, delay);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        };
        interstitialAd.setListener(adListener);
        interstitialAd.loadAd();

    }

}