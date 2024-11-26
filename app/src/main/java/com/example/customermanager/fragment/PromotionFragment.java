package com.example.customermanager.fragment;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customermanager.R;

import java.util.ArrayList;
import java.util.List;

import controller.ApiClient;
import controller.CustomerService;
import controller.RankingService;
import controller.VoucherService;
import model.dto.requests.RequestVoucherDTO;
import model.dto.responses.ResponseRankingDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionFragment extends Fragment {
    private CustomerService customerService;
    private RankingService rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
    private VoucherService voucherService = ApiClient.getRetrofitInstance().create(VoucherService.class);
    private List<ResponseRankingDTO> rankingsList;
    private LinearLayout rankNameContainer, voucherInputContainer;
    private Button btnPublishVouchers, btnGenerateScore;

    public PromotionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_promotion, container, false);

        customerService = ApiClient.getRetrofitInstance().create(CustomerService.class);
        rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
        voucherService = ApiClient.getRetrofitInstance().create(VoucherService.class);

        rankingsList = new ArrayList<>();

        rankNameContainer = view.findViewById(R.id.rankNameContainer);
        voucherInputContainer = view.findViewById(R.id.voucherInputContainer);
        btnPublishVouchers = view.findViewById(R.id.btnPublishVouchers);
        btnGenerateScore = view.findViewById(R.id.btnGenerateScore);

        btnPublishVouchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishVouchers();
            }
        });

        btnGenerateScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateScore();
            }
        });

        getAllRankings(responseRankingCallback);

        return view;
    }

    private void generateScore() {
        Call<Void> call = voucherService.generateScore();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Score generated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to generate score", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publishVouchers() {
        List<RequestVoucherDTO> requestVoucherDTOs = new ArrayList<>();
        for (int i = 0; i < voucherInputContainer.getChildCount(); i++) {
            EditText voucherField = (EditText) voucherInputContainer.getChildAt(i);
            String voucherValue = voucherField.getText().toString().trim();
            if (!TextUtils.isEmpty(voucherValue) && Float.parseFloat(voucherValue) > 0F) {
                RequestVoucherDTO voucherDTO = new RequestVoucherDTO();
                voucherDTO.setRank_id(rankingsList.get(i).getId());
                voucherDTO.setDiscount(Float.parseFloat(voucherValue));
                requestVoucherDTOs.add(voucherDTO);
            }
        }
        Call<Void> call = voucherService.publishVoucher(requestVoucherDTOs);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Vouchers published successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to publish vouchers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    Callback<List<ResponseRankingDTO>> responseRankingCallback = new Callback<List<ResponseRankingDTO>>() {
        @Override
        public void onResponse(Call<List<ResponseRankingDTO>> call, Response<List<ResponseRankingDTO>> response) {
            rankingsList = response.body();
            for (ResponseRankingDTO rankingDTO : rankingsList) {
                TextView rankName = new TextView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Chiều rộng
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.weight = 1;
                params.gravity = Gravity.CENTER_VERTICAL;
                params.setMargins(0, 16, 0, 16); // Trái, trên, phải, dưới (đơn vị: pixel)
                rankName.setLayoutParams(params);
                rankName.setText(rankingDTO.getName());
                rankName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                rankName.setTypeface(null, Typeface.BOLD);
                rankNameContainer.addView(rankName);

                EditText discountInput = new EditText(getContext());
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, // Chiều rộng
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params2.weight = 1;
                params2.setMargins(0, 16, 0, 16);
                discountInput.setLayoutParams(params2);
                discountInput.setHint(rankingDTO.getName());
                discountInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                voucherInputContainer.addView(discountInput);
            }
        }

        @Override
        public void onFailure(Call<List<ResponseRankingDTO>> call, Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
    };

    private void getAllRankings(Callback<List<ResponseRankingDTO>> callback) {
        Call<List<ResponseRankingDTO>> call = rankingService.getAllRankings();
        call.enqueue(callback);
    }
}