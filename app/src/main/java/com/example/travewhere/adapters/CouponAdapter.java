package com.example.travewhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.R;
import com.example.travewhere.models.Coupon;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private final Context context;
    private final List<Coupon> couponList;
    private final OnCouponClickListener listener;

    public interface OnCouponClickListener {
        void onCouponClick(Coupon coupon);
    }

    public CouponAdapter(Context context, List<Coupon> couponList, OnCouponClickListener listener) {
        this.context = context;
        this.couponList = couponList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);

        // Set coupon details
        holder.tvTitle.setText(coupon.getTitle());
        holder.tvMinSpend.setText("Min spend: " + coupon.getMinSpend());
        holder.tvCouponCode.setText(coupon.getCode());

        // Handle coupon click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCouponClick(coupon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public void setCoupons(List<Coupon> coupons) {
        this.couponList.clear();
        this.couponList.addAll(coupons);
        notifyDataSetChanged();
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvMinSpend, tvCouponCode;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCouponTitle);
            tvMinSpend = itemView.findViewById(R.id.tvMinSpend);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
        }
    }
}
