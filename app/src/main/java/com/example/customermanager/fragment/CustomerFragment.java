package com.example.customermanager.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.customermanager.customer_activites.AddCustomerActivity;
import com.example.customermanager.customer_activites.EditCustomerActivity;
import com.example.customermanager.R;
import com.example.customermanager.customer_activites.ViewCustomerActivity;
import com.example.customermanager.adapter.CustomerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import controller.ApiClient;
import controller.CustomerService;
import controller.RankingService;
import controller.VoucherService;
import model.dto.responses.ResponseCustomerDTO;
import model.dto.responses.ResponseRankingDTO;
import model.dto.responses.ResponseVoucherDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerFragment extends Fragment {
    private static CustomerService customerService;
    private RankingService rankingService;
    private VoucherService voucherService;
    private List<ResponseCustomerDTO> customersList;
    private List<ResponseRankingDTO> rankingsList;
    private List<ResponseVoucherDTO> vouchersList;
    private CustomerAdapter customerAdapter;
    private FloatingActionButton btnAddCustomer;
    private Spinner sortByOptionsSpinner, sortOrderSpinner, filterByRankSpinner, filterByDiscountValueSpinner;
    private RecyclerView rcvCustomer;
    private CheckBox cbSelectAllCustomers;
    private ImageView btnRefreshCustomer, btnDeleteSelectedCustomer, btnFilterCustomer;
    private String[] sortByOptions, sortOrders;
    private List<String> rankNames;
    private List<String> discountValues;
    private String rankName = "null", discount = "null", sortBy = "null", sortOrder = "asc";

    public CustomerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        customerService = ApiClient.getRetrofitInstance().create(CustomerService.class);
        rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
        voucherService = ApiClient.getRetrofitInstance().create(VoucherService.class);
        customersList = new ArrayList<>();

        rcvCustomer = view.findViewById(R.id.rcvCustomer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rcvCustomer.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcvCustomer.addItemDecoration(itemDecoration);

        btnAddCustomer = view.findViewById(R.id.btnAddCustomer);
        sortByOptionsSpinner = view.findViewById(R.id.sortByScoreSpinner);
        sortOrderSpinner = view.findViewById(R.id.sortByTotalVoucherQuantitySpinner);
        filterByRankSpinner = view.findViewById(R.id.filterByRankSpinner);
        filterByDiscountValueSpinner = view.findViewById(R.id.filterByDiscountValueSpinner);
        cbSelectAllCustomers = view.findViewById(R.id.cbSelectAllCustomers);
        btnFilterCustomer = view.findViewById(R.id.btnFilterCustomer);
        btnRefreshCustomer = view.findViewById(R.id.btnRefreshCustomer);
        btnDeleteSelectedCustomer = view.findViewById(R.id.btnDeleteSelectedCustomer);

        sortByOptions = new String[]{"select", "score", "quantity"};
        sortOrders = new String[]{"asc", "desc"};
        rankNames = new ArrayList<>(List.of("rank"));
        discountValues = new ArrayList<>(List.of("discount"));
        // Tạo ArrayAdapter để kết nối dữ liệu với Spinner
        ArrayAdapter<String> sortByOptionsAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, sortByOptions);
        sortByOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> sortOrderAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, sortOrders);
        sortOrderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> filterByRankAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, rankNames);
        filterByRankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> filterByDiscountValueAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, discountValues);
        filterByDiscountValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán Adapter cho Spinner
        sortByOptionsSpinner.setAdapter(sortByOptionsAdapter);
        sortByOptionsSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sortByOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Kiểm tra giá trị được chọn
                        if (position != 0) {
                            sortBy = sortByOptions[position];
                        } else sortBy = "null";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        sortBy = "null";
                    }
                });
                return false;
            }
        });

        sortOrderSpinner.setAdapter(sortOrderAdapter);
        sortOrderSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        sortOrder = sortOrders[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        sortOrder = "asc";
                    }
                });

                return false;
            }
        });

        filterByRankSpinner.setAdapter(filterByRankAdapter);
        filterByRankSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                filterByRankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0)
                            rankName = rankNames.get(position);
                        else rankName = "null";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        rankName = "null";
                    }
                });
                return false;
            }
        });

        filterByDiscountValueSpinner.setAdapter(filterByDiscountValueAdapter);
        filterByDiscountValueSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                filterByDiscountValueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0)
                            discount = discountValues.get(position);
                        else discount = "null";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        discount = "null";
                    }
                });
                return false;
            }
        });

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCustomerActivity.class);
                startActivity(intent);
            }
        });

        btnFilterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAndSortCustomers(responseCustomerCallback);
                Toast.makeText(view.getContext(), rankName + " " + discount + " " + sortBy + " " + sortOrder, Toast.LENGTH_SHORT).show();
            }
        });

        btnRefreshCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllCustomers(responseCustomerCallback);
            }
        });

        btnDeleteSelectedCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedCustomers();
            }
        });

        getAllCustomers(responseCustomerCallback);
        getAllRankings(responseRankingCallback);
        getAllVouchers(responseVoucherCallback);

        return view;
    }

    Callback<List<ResponseCustomerDTO>> responseCustomerCallback = new Callback<List<ResponseCustomerDTO>>() {
        @Override
        public void onResponse(Call<List<ResponseCustomerDTO>> call, Response<List<ResponseCustomerDTO>> response) {
            customersList = response.body();
            customerAdapter = new CustomerAdapter(customersList, new CustomerAdapter.OnItemActionListener() {
                @Override
                public void onView(ResponseCustomerDTO customerDTO) {
                    Intent intent = new Intent(getContext(), ViewCustomerActivity.class);
                    intent.putExtra("CUSTOMER_ID", customerDTO.getId());
                    intent.putExtra("CUSTOMER_NAME", customerDTO.getName());
                    intent.putExtra("CUSTOMER_DOB", customerDTO.getDob());
                    intent.putExtra("CUSTOMER_EMAIL", customerDTO.getEmail());
                    intent.putExtra("CUSTOMER_PHONE", customerDTO.getPhone());
                    intent.putExtra("CUSTOMER_GENDER", customerDTO.getGender());
                    intent.putExtra("CUSTOMER_SCORE", customerDTO.getScore());
                    intent.putExtra("CUSTOMER_RANK", customerDTO.getRankName());
                    intent.putExtra("CUSTOMER_DISCOUNTS", customerDTO.getDiscountStr());

                    startActivity(intent);
                }

                @Override
                public void onEdit(ResponseCustomerDTO customerDTO) {
                    Intent intent = new Intent(getContext(), EditCustomerActivity.class);
                    intent.putExtra("CUSTOMER_ID", customerDTO.getId());
                    intent.putExtra("CUSTOMER_NAME", customerDTO.getName());
                    intent.putExtra("CUSTOMER_DOB", customerDTO.getDob());
                    intent.putExtra("CUSTOMER_EMAIL", customerDTO.getEmail());
                    intent.putExtra("CUSTOMER_PHONE", customerDTO.getPhone());
                    intent.putExtra("CUSTOMER_GENDER", customerDTO.getGender());
                    intent.putExtra("CUSTOMER_SCORE", customerDTO.getScore());
                    intent.putExtra("CUSTOMER_RANK", customerDTO.getRankName());
                    intent.putExtra("CUSTOMER_DISCOUNTS", customerDTO.getDiscountStr());

                    startActivity(intent);
                }

                @Override
                public void onDelete(String customerId, int position) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete this customer?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Gọi API xóa khách hàng
                                performDeleteCustomer(customerId, position);
                            })
                            .setNegativeButton("No", null) // Đóng hộp thoại nếu nhấn "No"
                            .show();
                }
            });
            rcvCustomer.setAdapter(customerAdapter);
            cbSelectAllCustomers.setOnCheckedChangeListener((buttonView, isChecked) -> {
                customerAdapter.selectAll(isChecked); // Cập nhật trạng thái tất cả checkbox
            });
        }

        @Override
        public void onFailure(Call<List<ResponseCustomerDTO>> call, Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
    };
    Callback<List<ResponseRankingDTO>> responseRankingCallback = new Callback<List<ResponseRankingDTO>>() {
        @Override
        public void onResponse(Call<List<ResponseRankingDTO>> call, Response<List<ResponseRankingDTO>> response) {
            rankingsList = response.body();
            if (rankingsList != null && !rankingsList.isEmpty()) {
                for (ResponseRankingDTO rankingDTO : rankingsList)
                    rankNames.add(rankingDTO.getName());
            }
        }

        @Override
        public void onFailure(Call<List<ResponseRankingDTO>> call, Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
    };
    Callback<List<ResponseVoucherDTO>> responseVoucherCallback = new Callback<List<ResponseVoucherDTO>>() {
        @Override
        public void onResponse(Call<List<ResponseVoucherDTO>> call, Response<List<ResponseVoucherDTO>> response) {
            vouchersList = response.body();
            if (vouchersList != null && !vouchersList.isEmpty()) {
                for (ResponseVoucherDTO voucherDTO : vouchersList)
                    discountValues.add(String.valueOf(voucherDTO.getDiscount()));
            }
        }

        @Override
        public void onFailure(Call<List<ResponseVoucherDTO>> call, Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
    };

    private void deleteSelectedCustomers() {
        List<String> selectedCustomerIds = customerAdapter.getSelectedCustomerIds();

        if (selectedCustomerIds.isEmpty()) {
            Toast.makeText(getContext(), "No customers selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete the selected customers?")
                .setPositiveButton("Yes", (dialog, which) -> performDeleteMultipleCustomers(selectedCustomerIds))
                .setNegativeButton("No", null)
                .show();
    }

    private void performDeleteMultipleCustomers(List<String> selectedCustomerIds) {
        customerService = ApiClient.getRetrofitInstance().create(CustomerService.class);
        Call<Void> call = customerService.deleteCustomersByIds(selectedCustomerIds);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Customers deleted successfully", Toast.LENGTH_SHORT).show();
                    for (String customerId : selectedCustomerIds)
                        customerAdapter.removeItemById(customerId);
                } else {
                    Toast.makeText(getContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performDeleteCustomer(String customerId, int position) {
        customerService = ApiClient.getRetrofitInstance().create(CustomerService.class);
        Call<Void> call = customerService.deleteCustomer(customerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                    customerAdapter.removeItem(position); // Xóa khách hàng khỏi danh sách RecyclerView
                } else {
                    Toast.makeText(getContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getAllCustomers(Callback<List<ResponseCustomerDTO>> callback) {
        // Gọi API để lấy danh sách khách hàng
        Call<List<ResponseCustomerDTO>> call = customerService.getAllCustomers();
        call.enqueue(callback);
    }

    private void filterAndSortCustomers(Callback<List<ResponseCustomerDTO>> callback) {
        Call<List<ResponseCustomerDTO>> call = customerService.filterAndSortCustomers(rankName, discount, sortBy, sortOrder);
        call.enqueue(callback);
    }

    private void getAllRankings(Callback<List<ResponseRankingDTO>> callback) {
        Call<List<ResponseRankingDTO>> call = rankingService.getAllRankings();
        call.enqueue(callback);
    }

    private void getAllVouchers(Callback<List<ResponseVoucherDTO>> callback) {
        Call<List<ResponseVoucherDTO>> call = voucherService.getAllVouchers();
        call.enqueue(callback);
    }
}

