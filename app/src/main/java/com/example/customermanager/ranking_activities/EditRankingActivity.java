package com.example.customermanager.ranking_activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customermanager.R;

import controller.ApiClient;
import controller.RankingService;
import model.dto.requests.RequestRankingDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRankingActivity extends AppCompatActivity {
    private EditText etRankingName, etRankingDescription, etRankingPromotionScore, etReward;
    private ImageView btnBack;
    private Button btnSaveRanking, btnCancel;
    private TextView lblRankingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ranking);

        lblRankingId= findViewById(R.id.lblRankingId);
        etRankingName = findViewById(R.id.etRankingName);
        etRankingDescription = findViewById(R.id.etRankingDescription);
        etRankingPromotionScore = findViewById(R.id.etRankingPromotionScore);
        etReward = findViewById(R.id.etRankingReward);
        btnSaveRanking = findViewById(R.id.btnSaveRanking);
        btnBack = findViewById(R.id.btnBack);
        btnCancel = findViewById(R.id.btnCancel);

        lblRankingId.setText(String.valueOf(getIntent().getLongExtra("RANKING_ID",0)));
        etRankingName.setText(getIntent().getStringExtra("RANKING_NAME"));
        etRankingDescription.setText(getIntent().getStringExtra("RANKING_DESCRIPTION"));
        etRankingPromotionScore.setText(String.valueOf(getIntent().getLongExtra("RANKING_PROMOTION_SCORE",0)));
        etReward.setText(String.valueOf(getIntent().getFloatExtra("RANKING_REWARD",0F)));

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
        Call<Void> call = rankingService.updateRanking(Long.valueOf(getIntent().getLongExtra("RANKING_ID",0)), requestRankingDTO);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditRankingActivity.this, "Ranking edited successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(EditRankingActivity.this, "Failed to add ranking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        });
    }
}

