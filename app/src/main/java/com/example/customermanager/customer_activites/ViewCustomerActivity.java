package com.example.customermanager.customer_activites;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customermanager.R;

import java.util.Objects;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ViewCustomerActivity extends AppCompatActivity {
    private TextView tvId, tvName, tvDoB, tvEmail, tvPhone, tvGender, tvScore, tvRank, voucherQuantity;
    private ImageView btnBack;
    private LinearLayout voucherContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);

        tvId = findViewById(R.id.tvId);
        tvName = findViewById(R.id.tvName);
        tvDoB = findViewById(R.id.tvDoB);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvScore = findViewById(R.id.tvScore);
        tvRank = findViewById(R.id.tvRank);
        voucherContainer = findViewById(R.id.voucherContainer);
        voucherQuantity = findViewById(R.id.voucherQuantity);
        btnBack = findViewById(R.id.btnBack);

        tvId.setText(getIntent().getStringExtra("CUSTOMER_ID"));
        tvName.setText(getIntent().getStringExtra("CUSTOMER_NAME"));
        tvDoB.setText(getIntent().getStringExtra("CUSTOMER_DOB"));
        tvEmail.setText(getIntent().getStringExtra("CUSTOMER_EMAIL"));
        tvPhone.setText(getIntent().getStringExtra("CUSTOMER_PHONE"));
        tvGender.setText(getIntent().getByteExtra("CUSTOMER_GENDER", (byte) 0) == 0 ? "Male" : "Female");
        tvScore.setText(String.valueOf(getIntent().getLongExtra("CUSTOMER_SCORE", 0)));
        tvRank.setText(getIntent().getStringExtra("CUSTOMER_RANK"));

        String discountStr = getIntent().getStringExtra("CUSTOMER_DISCOUNTS");
        int totalVoucherQuantity = 0;
        SortedMap<Float, Integer> customerDiscounts = new TreeMap<>();
        if (!Objects.equals(discountStr, "")) {
            StringTokenizer stringTokenizer = new StringTokenizer(discountStr, ",");
            totalVoucherQuantity = stringTokenizer.countTokens();
            while (stringTokenizer.hasMoreTokens()) {
                Float discount = Float.parseFloat(stringTokenizer.nextToken());
                if (customerDiscounts.containsKey(discount)) {
                    customerDiscounts.put(discount, customerDiscounts.get(discount) + 1);
                } else customerDiscounts.put(discount, 1);
            }
        }
        voucherQuantity.setText(String.valueOf(totalVoucherQuantity));
        if (!customerDiscounts.isEmpty()) {
            customerDiscounts.forEach((discount, quantity) -> {
                TextView voucherField = new TextView(this);
                voucherField.setText(discount + "(" + quantity + ")");
                voucherField.setPadding(0,0,24,0);
                voucherField.setTextSize(24);
                voucherContainer.addView(voucherField);
            });
        }
        btnBack.setOnClickListener(v -> finish());
    }
}

