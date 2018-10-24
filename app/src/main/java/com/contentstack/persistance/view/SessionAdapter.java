package com.contentstack.persistance.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.contentstack.persistance.R;
import com.contentstack.persistance.table.Session;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import io.realm.RealmResults;


public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder>{

    private RealmResults<Session> sessionList;


    SessionAdapter(RealmResults<Session> listModels) {
        this.sessionList = listModels;
    }


    class SessionViewHolder extends RecyclerView.ViewHolder {

        TextView title, date;

        SessionViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
        }

    }


    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);

        return new SessionViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder( SessionViewHolder holder, int position) {

        Session model = sessionList.get(position);
        assert model != null;
        String sessionTitle = model.getTitle();
        String sessionStartDate = model.getStart_time();
        String sessionEndDate = model.getEnd_time();

        holder.title.setText(sessionTitle);
        holder.date.setText(getDateFormat(sessionStartDate, sessionEndDate));
    }



    @Override
    public int getItemCount() {
        return sessionList.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDateFormat(String startTime, String endTime){

        @SuppressLint("SimpleDateFormat") String finish = new SimpleDateFormat("hh:mm a")
                .format(Date.from(Instant.parse(endTime)));
        @SuppressLint("SimpleDateFormat") String start = new SimpleDateFormat("hh:mm a")
                .format(Date.from(Instant.parse(startTime)));
        LocalDate localDate = Date.from(Instant.parse(startTime))
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String dayMonth = localDate.getDayOfMonth()+" "+localDate.getMonth();
        return dayMonth+" ( "+start+" ~ "+finish+" )";
    }

}

