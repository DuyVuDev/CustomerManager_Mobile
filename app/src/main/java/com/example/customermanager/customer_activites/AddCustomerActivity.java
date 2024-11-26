package com.example.customermanager.customer_activites;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customermanager.R;
import com.example.customermanager.fragment.CustomerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import controller.ApiClient;
import controller.CustomerService;
import controller.RankingService;
import model.dto.requests.RequestCustomerDTO;
import model.dto.responses.ResponseRankingDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCustomerActivity extends AppCompatActivity {
    private EditText etName, etDoB, etEmail, etPhone, etScore;
    private ImageView btnDecrease, btnIncrease, btnBack;
    private TextView voucherQuantity;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSaveCustomer, btnCancel;
    private LinearLayout voucherContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        etName = findViewById(R.id.etName);
        etDoB = findViewById(R.id.etDoB);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etScore = findViewById(R.id.etScore);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        voucherContainer = findViewById(R.id.voucherContainer);
        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnDecrease = findViewById(R.id.btnDecrease);
        voucherQuantity = findViewById(R.id.voucherQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnBack = findViewById(R.id.btnBack);
        btnCancel = findViewById(R.id.btnCancel);

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt((String) voucherQuantity.getText());
                quantity++;
                voucherQuantity.setText(String.valueOf(quantity));
                updateVoucherContainer(quantity);
            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt((String) voucherQuantity.getText());
                if (quantity > 0) {
                    quantity--;
                    voucherQuantity.setText(String.valueOf(quantity));
                    updateVoucherContainer(quantity);
                }
            }
        });

        btnSaveCustomer.setOnClickListener(v -> submitCustomer());
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void submitCustomer() {
        // Lấy dữ liệu từ form
        String name = etName.getText().toString().trim();
        String dob = etDoB.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        Byte gender = (byte) (selectedGenderId == R.id.rbMale ? 0 : 1);
        String scoreStr = etScore.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(scoreStr)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false); // Bật kiểm tra ngày hợp lệ
        try {
            dateFormat.parse(dob); // Kiểm tra xem có thể parse không
        } catch (ParseException e) {
            Toast.makeText(this, "Incorrect date format", Toast.LENGTH_SHORT).show();
            return; // Không đúng định dạng hoặc không hợp lệ
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Incorrect email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Incorrect phone number format (10 digits)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse dữ liệu
        Long score = Long.parseLong(scoreStr);
        StringBuilder discountStr = new StringBuilder();
        for (int i = 0; i < voucherContainer.getChildCount(); i++) {
            EditText voucherField = (EditText) voucherContainer.getChildAt(i);
            String voucherValue = voucherField.getText().toString().trim();
            if (!TextUtils.isEmpty(voucherValue) && Float.parseFloat(voucherValue) > 0F) {
                discountStr.append(voucherValue).append(",");
            }
        }
        if (discountStr.length() > 0)
            discountStr.deleteCharAt(discountStr.length() - 1);
        // Gửi dữ liệu qua Retrofit
        RequestCustomerDTO requestCustomerDTO = new RequestCustomerDTO();
        requestCustomerDTO.setName(name);
        requestCustomerDTO.setDob(dob);
        requestCustomerDTO.setEmail(email);
        requestCustomerDTO.setPhone(phone);
        requestCustomerDTO.setGender(gender);
        requestCustomerDTO.setScore(score);
        requestCustomerDTO.setDiscountStr(discountStr.toString());

        System.out.println(requestCustomerDTO.toString());

        CustomerService customerService = ApiClient.getRetrofitInstance().create(CustomerService.class);
        Call<Void> call = customerService.createCustomer(requestCustomerDTO);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddCustomerActivity.this, "Customer added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(AddCustomerActivity.this, "Failed to add customer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        });
    }

    private void updateVoucherContainer(int quantity) {
        voucherContainer.removeAllViews();

        for (int i = 0; i < quantity; i++) {
            EditText voucherField = new EditText(this);
            voucherField.setHint("Voucher " + (i + 1));
            voucherField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            voucherContainer.addView(voucherField);
        }
    }
}

