package com.example.travewhere;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.travewhere.models.Coupon;
import com.example.travewhere.viewmodels.CouponViewModel;

public class CouponDetailActivity extends AppCompatActivity {

    private CouponViewModel couponViewModel;
    private TextView tvTitle, tvCode, tvDiscount, tvMinSpend, tvDetails;
    private Button btnCopyCouponCode;
    private RelativeLayout btnBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);
        getSupportActionBar().hide();

        tvTitle = findViewById(R.id.tvCouponTitle);
        tvCode = findViewById(R.id.tvCouponCode);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvMinSpend = findViewById(R.id.tvMinSpend);
        tvDetails = findViewById(R.id.tvCouponDetails);
        btnCopyCouponCode = findViewById(R.id.btnCopyCouponCode);
        btnBackLayout = findViewById(R.id.btnBackLayout);

        couponViewModel = new ViewModelProvider(this).get(CouponViewModel.class);

        String couponId = getIntent().getStringExtra("COUPON_ID");
        if (couponId != null) {
            fetchCouponDetails(couponId);
        } else {
            Toast.makeText(this, "Coupon not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnCopyCouponCode.setOnClickListener(v -> copyCouponCodeToClipboard());
        btnBackLayout.setOnClickListener(v -> finish());
    }

    private void fetchCouponDetails(String couponId) {
        couponViewModel.getCouponById(couponId).observe(this, coupon -> {
            if (coupon != null) {
                displayCouponDetails(coupon);
            } else {
                Toast.makeText(this, "Unable to fetch coupon details!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayCouponDetails(Coupon coupon) {
        tvTitle.setText("Discount: " + coupon.getDiscount() + " %");
        tvCode.setText("Code: " + coupon.getCode());
        tvDiscount.setText("Discount: " + coupon.getDiscount() + " %");
        tvMinSpend.setText("Min Spend: " + coupon.getMinSpend() + " $");
        tvDetails.setText("Details:\n\n• Use this coupon for amazing discounts.\n• Apply it during checkout.");
    }

    private void copyCouponCodeToClipboard() {
        String couponCode = tvCode.getText().toString();
        if (couponCode.isEmpty()) {
            Toast.makeText(this, "Coupon code is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Coupon Code", couponCode);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, "Coupon code copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}
