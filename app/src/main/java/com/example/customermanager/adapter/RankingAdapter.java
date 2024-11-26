package com.example.customermanager.adapter;

import android.annotation.SuppressLint;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customermanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.dto.responses.ResponseRankingDTO;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private final List<ResponseRankingDTO> rankingsList;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();
    private OnItemActionListener onItemActionListener;
    private List<Long> selectedRankingIds = new ArrayList<>();

    public RankingAdapter(List<ResponseRankingDTO> rankingsList, OnItemActionListener onItemActionListener) {
        this.rankingsList = rankingsList;
        this.onItemActionListener = onItemActionListener;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        ResponseRankingDTO rankingDTO = rankingsList.get(position);
        if (rankingDTO == null) {
            return;
        }
        holder.tvRankingId.setText(String.valueOf(rankingDTO.getId()));
        holder.tvRankingName.setText(rankingDTO.getName());
        holder.tvRankingDescription.setText(rankingDTO.getDescription());
        holder.tvRankingPromotionScore.setText(String.valueOf(rankingDTO.getPromotionScore()));
        holder.tvRankingReward.setText(String.valueOf(rankingDTO.getReward()));

        holder.btnEditRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemActionListener != null) {
                    onItemActionListener.onEdit(rankingDTO);
                }
            }
        });

        holder.btnDeleteRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemActionListener != null) {
                    onItemActionListener.onDelete(rankingDTO.getId(), position);
                }
            }
        });

        // Lấy trạng thái từ SparseBooleanArray
        holder.cbSelectRanking.setChecked(itemStateArray.get(position, false));

        // Xử lý sự kiện thay đổi trạng thái checkbox
        holder.cbSelectRanking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemStateArray.put(position, isChecked); // Cập nhật trạng thái
            if (isChecked) {
                selectedRankingIds.add(rankingDTO.getId());
            } else {
                selectedRankingIds.remove(rankingDTO.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (!rankingsList.isEmpty())
            return rankingsList.size();
        return 0;
    }
    public void removeItem(int position) {
        rankingsList.remove(position);
        notifyItemRemoved(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItemById(Long id) {
        rankingsList.removeIf(rankingDTO -> Objects.equals(rankingDTO.getId(), id));
        notifyDataSetChanged();
        for (int i = 0; i < rankingsList.size(); i++) {
            itemStateArray.put(i, false);
        }
    }
    // Chọn tất cả hoặc bỏ chọn tất cả
    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(boolean isSelected) {
        for (int i = 0; i < rankingsList.size(); i++) {
            itemStateArray.put(i, isSelected);
        }
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    public SparseBooleanArray getItemStateArray() {
        return itemStateArray;
    }

    public List<Long> getSelectedRankingIds() {
        return selectedRankingIds;
    }

    public interface OnItemActionListener {
        void onEdit(ResponseRankingDTO rankingDTO);
        void onDelete(Long rankingId, int position);
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRankingId,
                tvRankingName,
                tvRankingDescription,
                tvRankingPromotionScore,
                tvRankingReward;
        private ImageView btnEditRanking, btnDeleteRanking;
        private CheckBox cbSelectRanking;


        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankingId = itemView.findViewById(R.id.tvRankingId);
            tvRankingName = itemView.findViewById(R.id.tvRankingName);
            tvRankingDescription = itemView.findViewById(R.id.tvRankingDescription);
            tvRankingReward = itemView.findViewById(R.id.tvRankingReward);
            tvRankingPromotionScore = itemView.findViewById(R.id.tvRankingPromotionScore);
            btnEditRanking = itemView.findViewById(R.id.btnEditRanking);
            btnDeleteRanking = itemView.findViewById(R.id.btnDeleteRanking);
            cbSelectRanking = itemView.findViewById(R.id.cbSelectRanking);
        }
    }
}
