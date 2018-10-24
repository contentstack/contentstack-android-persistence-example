package com.contentstack.persistance.table;


import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.RealmField;

@RealmClass(name = "session")
public class Session extends RealmObject {

    @PrimaryKey
    @RealmField(name = "uid")
    private String uid;

    @RealmField(name = "title")
    private String title;
    @RealmField(name = "description")
    private String description;
    @RealmField(name = "is_popular")
    private String is_popular;
    @RealmField(name = "type")
    private String type;
    @RealmField(name = "session_id")
    private String sessionId;
    @RealmField(name = "tags")
    private String tags;
    @RealmField(name = "locale")
    private String locale;
    @RealmField(name = "start_time")
    private String start_time;
    @RealmField(name = "end_time")
    private String end_time;


    ///////////////////////////////////////////////////////////////////////
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIs_popular() {
        return is_popular;
    }
    public void setIs_popular(String is_popular) {
        this.is_popular = is_popular;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public String getStart_time() {
        return start_time;
    }
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
    ///////////////////////////////////////////////////////////////////////


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDateFormat(String startTime, String endTime){

        @SuppressLint("SimpleDateFormat")
        String finish = new SimpleDateFormat("hh:mm a").format(Date.from(Instant.parse(endTime)));
        @SuppressLint("SimpleDateFormat")
        String start = new SimpleDateFormat("hh:mm a").format(Date.from(Instant.parse(startTime)));
        LocalDate localDate = Date.from(Instant.parse(startTime))
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String dayMonth = localDate.getDayOfMonth()+" "+localDate.getMonth();
        return dayMonth+" ( "+start+" ~ "+finish+" )";
    }
}
