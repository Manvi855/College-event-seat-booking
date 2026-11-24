package com.manvirai.collegeeventseatbooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Models.EventItem event);
    }

    private final List<Models.EventItem> events;
    private final OnEventClickListener listener;

    public EventListAdapter(List<Models.EventItem> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Models.EventItem ev = events.get(position);

        holder.tvRowTitle.setText(ev.title);

        String scheduleVenue = ev.eventDate + " • " + ev.eventTime + " • " + ev.venue;
        holder.tvRowScheduleVenue.setText(scheduleVenue);

        String feeText = (!ev.isPaid || ev.fee <= 0.0)
                ? "FREE"
                : String.format(Locale.getDefault(), "PAID • ₹%.0f", ev.fee);
        String hallAndFee = ev.hallName + " • " + feeText;
        holder.tvRowHallAndFee.setText(hallAndFee);

        holder.ivRowPoster.setImageResource(getPosterDrawable(ev.posterType));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(ev);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    private int getPosterDrawable(String posterType) {
        if (posterType == null) {
            return android.R.drawable.ic_menu_today;
        }
        switch (posterType.toUpperCase(Locale.ROOT)) {
            case "TECH":
                return android.R.drawable.ic_menu_manage;
            case "CULTURAL":
                return android.R.drawable.ic_menu_gallery;
            case "WORKSHOP":
                return android.R.drawable.ic_menu_edit;
            case "SPORTS":
                return android.R.drawable.ic_menu_compass;
            default:
                return android.R.drawable.ic_menu_today;
        }
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRowPoster;
        TextView tvRowTitle;
        TextView tvRowScheduleVenue;
        TextView tvRowHallAndFee;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRowPoster = itemView.findViewById(R.id.ivRowPoster);
            tvRowTitle = itemView.findViewById(R.id.tvRowTitle);
            tvRowScheduleVenue = itemView.findViewById(R.id.tvRowScheduleVenue);
            tvRowHallAndFee = itemView.findViewById(R.id.tvRowHallAndFee);
        }
    }
}