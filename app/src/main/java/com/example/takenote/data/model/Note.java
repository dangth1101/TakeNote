package com.example.takenote.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;


@Entity
public class Note {
    @PrimaryKey @NonNull
    private String id;
    private String title;
    private String description;
    private Boolean priority;



    @ColumnInfo(name = "updated_at")
    private long updatedDate;

    @ColumnInfo(name = "created_at")
    private long createdDate;

    public Note(String id, String title, String description, Boolean priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.createdDate = System.currentTimeMillis();
        this.updatedDate = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Boolean getPriority() {
        return priority;
    }
    public void setTitle(String title) {
        setUpdatedDate();
        this.title = title;
    }
    public void setDescription(String description) {
        setUpdatedDate();
        this.description = description;
    }
    public void setPriority(Boolean priority) {
        this.priority = priority;
    }
    public void setId(@NonNull String id) {
        this.id = id;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUpdatedDate() {
        this.updatedDate = System.currentTimeMillis();
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static Note fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Note.class);
    }
    public static Map<String, Object> toMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
