package com.example.customermanager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customermanager.fragment.CustomerFragment;
import com.example.customermanager.fragment.PromotionFragment;
import com.example.customermanager.fragment.RankingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);
        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.person);
    }

    CustomerFragment personFragment = new CustomerFragment();
    RankingFragment rankingFragment = new RankingFragment();
    PromotionFragment promotionFragment = new PromotionFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.person) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, personFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.ranking) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, rankingFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.promotion) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, promotionFragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }
}