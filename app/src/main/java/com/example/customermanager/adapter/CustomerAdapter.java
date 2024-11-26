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

import model.dto.responses.ResponseCustomerDTO;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final List<ResponseCustomerDTO> customersList;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();
    private OnItemActionListener onItemActionListener;
    private List<String> selectedCustomerIds = new ArrayList<>();

    public CustomerAdapter(List<ResponseCustomerDTO> customersList, OnItemActionListener onItemActionListener) {
        this.customersList = customersList;
        this.onItemActionListener = onItemActionListener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        ResponseCustomerDTO customerDTO = customersList.get(position);
        if (customerDTO == null) {
            return;
        }
        holder.tvCustomerId.setText(customerDTO.getId());
        holder.tvCustomerName.setText(customerDTO.getName());
        holder.tvCustomerDoB.setText(customerDTO.getDob());
        holder.tvCustomerGender.setText(customerDTO.getGender() == 0 ? "Male" : "Female");
        holder.tvCustomerRank.setText(customerDTO.getRankName());
        holder.tvCustomerScore.setText(String.valueOf(customerDTO.getScore()));

        holder.btnDetailCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemActionListener != null) {
                    onItemActionListener.onView(customerDTO);
                }
            }
        });

        holder.btnEditCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemActionListener != null) {
                    onItemActionListener.onEdit(customerDTO);
                }
            }
        });

        holder.btnDeleteCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemActionListener != null) {
                    onItemActionListener.onDelete(customerDTO.getId(), position);
                }
            }
        });

        // Lấy trạng thái từ SparseBooleanArray
        holder.cbSelectCustomer.setChecked(itemStateArray.get(position, false));

        // Xử lý sự kiện thay đổi trạng thái checkbox
        holder.cbSelectCustomer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemStateArray.put(position, isChecked); // Cập nhật trạng thái
            if (isChecked) {
                selectedCustomerIds.add(customerDTO.getId());
            } else {
                selectedCustomerIds.remove(customerDTO.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }

    public void removeItem(int position) {
        customersList.remove(position);
        notifyItemRemoved(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItemById(String id) {
        customersList.removeIf(customerDTO -> Objects.equals(customerDTO.getId(), id));
        notifyDataSetChanged();
        for (int i = 0; i < customersList.size(); i++) {
            itemStateArray.put(i, false);
        }
    }

    // Chọn tất cả hoặc bỏ chọn tất cả
    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(boolean isSelected) {
        for (int i = 0; i < customersList.size(); i++) {
            itemStateArray.put(i, isSelected);
        }
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    public SparseBooleanArray getItemStateArray() {
        return itemStateArray;
    }

    public List<String> getSelectedCustomerIds() {
        return selectedCustomerIds;
    }

    public interface OnItemActionListener {
        void onView(ResponseCustomerDTO customerDTO);

        void onEdit(ResponseCustomerDTO customerDTO);

        void onDelete(String customerId, int position);
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCustomerId,
                tvCustomerName,
                tvCustomerDoB,
                tvCustomerGender,
                tvCustomerRank,
                tvCustomerScore;
        private ImageView btnEditCustomer, btnDeleteCustomer, btnDetailCustomer;
        private CheckBox cbSelectCustomer;


        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerScore = itemView.findViewById(R.id.tvCustomerScore);
            tvCustomerRank = itemView.findViewById(R.id.tvCustomerRank);
            tvCustomerDoB = itemView.findViewById(R.id.tvCustomerDoB);
            tvCustomerGender = itemView.findViewById(R.id.tvCustomerGender);
            btnEditCustomer = itemView.findViewById(R.id.btnEditCustomer);
            btnDeleteCustomer = itemView.findViewById(R.id.btnDeleteCustomer);
            btnDetailCustomer = itemView.findViewById(R.id.btnDetailCustomer);
            cbSelectCustomer = itemView.findViewById(R.id.cbSelectCustomer);
        }
    }
}
