package com.example.expirynotifier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.expirynotifier.databinding.SingleItemBinding;


import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


public class NotifierAdapter extends ListAdapter<ReminderClass, NotifierAdapter.VH> {
    private Context context;
    private OnReminderClicked onReminderClicked;

    TimeZone tz1 = TimeZone.getTimeZone("Asia/Kolkata");

    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    int currentDate = calendar.get(Calendar.DAY_OF_YEAR);

    Calendar temp = Calendar.getInstance(Locale.ENGLISH);

    public NotifierAdapter(Context context, OnReminderClicked onReminderClicked, TaskDiff taskDiff) {
        super(taskDiff);
        this.context = context;
        this.onReminderClicked = onReminderClicked;
    }

    @NonNull
    @Override
    public NotifierAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(SingleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    class VH extends RecyclerView.ViewHolder {
        SingleItemBinding binding;

        VH(SingleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NotifierAdapter.VH holder, @SuppressLint("RecyclerView") int position) {
        SingleItemBinding binding = holder.binding;
        binding.itemName.setText(getItem(position).getProductName());
        binding.itemCategory.setText(getItem(position).getCategory());



        temp.setTimeInMillis(getItem(position).expiryDate);
        int expiryDate = temp.get(Calendar.DAY_OF_YEAR);

        int bal = expiryDate - currentDate;

        if(bal>10) {
            binding.cardView.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
        } else if(bal > 4) {
            binding.cardView.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow));
        } else {
            binding.cardView.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
        }


        StringBuilder sb = new StringBuilder();
        sb.append("Notify");
        if(getItem(position).notifyBefore == 0){
            sb.append(" on Expiry Day");
        } else if(getItem(position).notifyBefore == 1) {
            sb.append(" before : ").append(getItem(position).notifyBefore).append(" Day");
        } else {
            sb.append(" before : ").append(getItem(position).notifyBefore).append(" Days");
        }
        binding.notifyBefore.setText(sb);
        String imageUrl = getItem(position).getImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().fitCenter())
                    .error(R.drawable.app_icon)
                    .into(binding.image);
        } else {
            binding.image.setVisibility(View.GONE);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

        String expiry = "Expiry Date : " +  simpleDateFormat.format(getItem(position).expiryDate);
        binding.expiryDate.setText(expiry);

        binding.getRoot().setOnClickListener( view ->
                        onReminderClicked.onReminderClicked(getItem(position))
        );


    }

    static class TaskDiff extends DiffUtil.ItemCallback<ReminderClass> {

        @Override
        public boolean areItemsTheSame(@NonNull ReminderClass oldItem, @NonNull ReminderClass newItem) {
            return Objects.equals(oldItem.getUuid(), newItem.getUuid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReminderClass oldItem, @NonNull ReminderClass newItem) {
            return Objects.equals(oldItem.getUuid(), newItem.getUuid()) &&
                    Objects.equals(oldItem.expiryDate,newItem.expiryDate)&&
                    Objects.equals(oldItem.productName,newItem.productName)&&
                    Objects.equals(oldItem.notifyBefore,newItem.notifyBefore)&&
                    Objects.equals(oldItem.imageUrl,newItem.imageUrl)&&
                    Objects.equals(oldItem.category,newItem.category);
        }
    }
}
