package com.sharkBytesLab.cryptoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

import java.util.concurrent.TimeUnit;

public class RewardActivity extends AppCompatActivity implements MaxRewardedAdListener {

    private MaxRewardedAd rewardedAd;
    private int retryAttempt;
    private final String TAG = "Rewarded Ads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        rewardedAd = MaxRewardedAd.getInstance("5b921723f96ba5ac", this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();

        if (rewardedAd.isReady()) {
            rewardedAd.showAd();
            Log.i(TAG, "Show");
        } else {
            Toast.makeText(this, "Getting Ads!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        Log.i(TAG, "Loaded");
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
        Log.i(TAG, "Failed");
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rewardedAd.loadAd();
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error) {
        Log.i(TAG, "Failed");
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
        Log.i(TAG, "Displayed");
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
    }

    @Override
    public void onAdHidden(final MaxAd maxAd) {
        Log.i(TAG, "Hidden");
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {
    }

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {
        Log.i(TAG, "Completed");
        Toast.makeText(this, "Reward Achieved!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward) {
        // Rewarded ad was displayed and user should receive the reward
    }


}