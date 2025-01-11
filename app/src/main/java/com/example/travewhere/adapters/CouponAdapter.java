package com.example.travewhere.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.CouponDetailActivity;
import com.example.travewhere.R;
import com.example.travewhere.models.Coupon;
import com.example.travewhere.viewmodels.CouponViewModel;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private final Context context;
    private final List<Coupon> couponList;
    private boolean isVertical = true;
    CouponViewModel couponViewModel = new CouponViewModel();

    public CouponAdapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
    }

    // Function to switch layout orientation
    public void setOrientation(boolean isVertical) {
        this.isVertical = isVertical;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return isVertical ? 1 : 0;
    }


    public void prefetch(Runnable onComplete) {
        // Fetch all coupons
        couponViewModel.getAllCoupons().observe((LifecycleOwner) context, coupons -> {
            if (coupons != null) {
                setCoupons(coupons);
                Log.d("CouponAdapter", "Prefetched " + coupons.size() + " coupons");
            } else {
                Log.d("CouponAdapter", "No coupons found");
            }
            onComplete.run();
        });
    }


    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.coupon_item_vertical, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.coupon_item_horizontal, parent, false);
        }
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);

        // Set coupon details
        holder.tvTitle.setText("Discount: " + coupon.getDiscount() + " %");
        holder.tvMinSpend.setText("Min spend: " + coupon.getMinSpend() + " $");
        holder.tvCode.setText(coupon.getCode());
        holder.ivCouponImage.setImageResource(R.drawable.ic_coupon);

        // Handle coupon code click (copy to clipboard)
        holder.tvCode.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip =
                    android.content.ClipData.newPlainText("Coupon Code", coupon.getCode());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Coupon code copied to clipboard!", Toast.LENGTH_SHORT).show();
        });

        // Handle coupon image click (navigate to detail activity)
        holder.ivCouponImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, CouponDetailActivity.class);
            intent.putExtra("COUPON_ID", coupon.getId());
            context.startActivity(intent);
        });

        // Handle coupon details click (navigate to detail activity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CouponDetailActivity.class);
            intent.putExtra("COUPON_ID", coupon.getId());
            context.startActivity(intent);
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

    public void updateCouponList(List<Coupon> newCouponList) {
        couponList.clear();
        couponList.addAll(newCouponList);
        notifyDataSetChanged(); // Notify the adapter to refresh the UI
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvMinSpend, tvCode;
        ImageView ivCouponImage;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCouponTitle);
            tvMinSpend = itemView.findViewById(R.id.tvMinSpend);
            tvCode = itemView.findViewById(R.id.tvCouponCode);
            ivCouponImage = itemView.findViewById(R.id.ivCouponImage);
        }
    }
}
