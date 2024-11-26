package com.example.customermanager.ranking_activities;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customermanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import controller.ApiClient;
import controller.RankingService;
import model.dto.requests.RequestRankingDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRankingActivity extends AppCompatActivity {
    private EditText etRankingName, etRankingDescription, etRankingPromotionScore, etReward;
    private ImageView btnBack;
    private Button btnSaveRanking, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ranking);

        etRankingName = findViewById(R.id.etRankingName);
        etRankingDescription = findViewById(R.id.etRankingDescription);
        etRankingPromotionScore = findViewById(R.id.etRankingPromotionScore);
        etReward = findViewById(R.id.etRankingReward);
        btnSaveRanking = findViewById(R.id.btnSaveRanking);
        btnBack = findViewById(R.id.btnBack);
        btnCancel = findViewById(R.id.btnCancel);

        btnSaveRanking.setOnClickListener(v -> submitRanking());

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void submitRanking() {
        // Lấy dữ liệu từ form
        String rankingName = etRankingName.getText().toString().trim();
        String rankingDescription = etRankingDescription.getText().toString().trim();
        String rankingPromotionScore = etRankingPromotionScore.getText().toString().trim();
        String rankingReward = etReward.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(rankingName) || TextUtils.isEmpty(rankingPromotionScore)
                || TextUtils.isEmpty(rankingReward)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi dữ liệu qua Retrofit
        RequestRankingDTO requestRankingDTO = new RequestRankingDTO();
        requestRankingDTO.setName(rankingName);
        requestRankingDTO.setDescription(rankingDescription);
        requestRankingDTO.setPromotionScore(Long.valueOf(rankingPromotionScore));
        requestRankingDTO.setReward(Float.valueOf(rankingReward));

        RankingService rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
        Call<Void> call = rankingService.createRanking(requestRankingDTO);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddRankingActivity.this, "Ranking added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(AddRankingActivity.this, "Failed to add ranking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        });
    }
}

