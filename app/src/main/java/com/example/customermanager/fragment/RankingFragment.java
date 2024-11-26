package com.example.customermanager.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customermanager.R;
import com.example.customermanager.adapter.CustomerAdapter;
import com.example.customermanager.adapter.RankingAdapter;
import com.example.customermanager.customer_activites.AddCustomerActivity;
import com.example.customermanager.customer_activites.EditCustomerActivity;
import com.example.customermanager.customer_activites.ViewCustomerActivity;
import com.example.customermanager.ranking_activities.AddRankingActivity;
import com.example.customermanager.ranking_activities.EditRankingActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import controller.ApiClient;
import controller.CustomerService;
import controller.RankingService;
import model.dto.responses.ResponseCustomerDTO;
import model.dto.responses.ResponseRankingDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingFragment extends Fragment {
    private RankingService rankingService;
    private List<ResponseRankingDTO> rankingsList;
    private RankingAdapter rankingAdapter;
    private FloatingActionButton btnAddRanking;
    private RecyclerView rcvRanking;
    private CheckBox cbSelectAllRankings;
    private ImageView btnRefreshRanking, btnDeleteSelectedRanking;

    public RankingFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
        rcvRanking = view.findViewById(R.id.rcvRanking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rcvRanking.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcvRanking.addItemDecoration(itemDecoration);

        btnAddRanking = view.findViewById(R.id.btnAddRanking);
        btnDeleteSelectedRanking = view.findViewById(R.id.btnDeleteSelectedRanking);
        btnRefreshRanking = view.findViewById(R.id.btnRefreshRanking);
        cbSelectAllRankings = view.findViewById(R.id.cbSelectAllRankings);

        btnAddRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRankingActivity.class);
                startActivity(intent);
            }
        });

        btnRefreshRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllRankings(responseRankingCallback);
            }
        });

        btnDeleteSelectedRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedRankings();
            }
        });

        getAllRankings(responseRankingCallback);
        return view;
    }
    Callback<List<ResponseRankingDTO>> responseRankingCallback = new Callback<List<ResponseRankingDTO>>() {
        @Override
        public void onResponse(Call<List<ResponseRankingDTO>> call, Response<List<ResponseRankingDTO>> response) {
            rankingsList = response.body();
            rankingAdapter = new RankingAdapter(rankingsList, new RankingAdapter.OnItemActionListener() {

                @Override
                public void onEdit(ResponseRankingDTO rankingDTO) {
                    Intent intent = new Intent(getContext(), EditRankingActivity.class);
                    intent.putExtra("RANKING_ID", rankingDTO.getId());
                    intent.putExtra("RANKING_NAME", rankingDTO.getName());
                    intent.putExtra("RANKING_DESCRIPTION", rankingDTO.getDescription());
                    intent.putExtra("RANKING_PROMOTION_SCORE", rankingDTO.getPromotionScore());
                    intent.putExtra("RANKING_REWARD", rankingDTO.getReward());

                    startActivity(intent);
                }

                @Override
                public void onDelete(Long rankingId, int position) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete this ranking?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Gọi API xóa khách hàng
                                performDeleteRanking(rankingId, position);
                            })
                            .setNegativeButton("No", null) // Đóng hộp thoại nếu nhấn "No"
                            .show();
                }
            });
            rcvRanking.setAdapter(rankingAdapter);
            cbSelectAllRankings.setOnCheckedChangeListener((buttonView, isChecked) -> {
                rankingAdapter.selectAll(isChecked); // Cập nhật trạng thái tất cả checkbox
            });
        }

        @Override
        public void onFailure(Call<List<ResponseRankingDTO>> call, Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
    };
    private void getAllRankings(Callback<List<ResponseRankingDTO>> callback) {
        // Gọi API để lấy danh sách khách hàng
        Call<List<ResponseRankingDTO>> call = rankingService.getAllRankings();
        call.enqueue(callback);
    }

    private void performDeleteRanking(Long rankingId, int position) {
        rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
        Call<Void> call = rankingService.deleteRankingById(rankingId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ranking deleted successfully", Toast.LENGTH_SHORT).show();
                    rankingAdapter.removeItem(position); // Xóa khách hàng khỏi danh sách RecyclerView
                } else {
                    Toast.makeText(getContext(), "Failed to delete ranking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteSelectedRankings() {
        List<Long> selectedRankingIds = rankingAdapter.getSelectedRankingIds();

        if (selectedRankingIds.isEmpty()) {
            Toast.makeText(getContext(), "No rankings selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete the selected rankings?")
                .setPositiveButton("Yes", (dialog, which) -> performDeleteMultipleRankings(selectedRankingIds))
                .setNegativeButton("No", null)
                .show();
    }

    private void performDeleteMultipleRankings(List<Long> selectedRankingIds) {
        rankingService = ApiClient.getRetrofitInstance().create(RankingService.class);
        Call<Void> call = rankingService.deleteAllRankingsByIds(selectedRankingIds);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Rankings deleted successfully", Toast.LENGTH_SHORT).show();
                    for (Long rankingId : selectedRankingIds)
                        rankingAdapter.removeItemById(rankingId);
                } else {
                    Toast.makeText(getContext(), "Failed to delete ranking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}