package com.example.travewhere.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.CouponDetailActivity;
import com.example.travewhere.R;
import com.example.travewhere.models.Coupon;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private final Context context;
    private final List<Coupon> couponList;

    public CouponAdapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
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
        holder.tvTitle.setText("Discount: " + coupon.getDiscount() + " VND");
        holder.tvMinSpend.setText("Min spend: " + coupon.getMinSpend() + " VND");
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
