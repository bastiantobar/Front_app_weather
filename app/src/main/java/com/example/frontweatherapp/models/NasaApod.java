package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class NasaApod implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("title")
    private String title;
    @SerializedName("explanation")
    private String explanation;
    @SerializedName("url")
    private String url;
    @SerializedName("copyright")
    private String copyright;
    @SerializedName("mediaType")
    private String mediaType;
    @SerializedName("date")
    private String date;

    // Getters
    public String getTitle() { return title; }
    public String getExplanation() { return explanation; }
    public String getUrl() { return url; }
    public String getCopyright() { return copyright; }
    public String getMediaType() { return mediaType; }
    public String getDate() { return date; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setUrl(String url) { this.url = url; }
    public void setCopyright(String copyright) { this.copyright = copyright; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "NasaApod{" +
                "title='" + title + '\'' +
                ", explanation='" + explanation + '\'' +
                ", url='" + url + '\'' +
                ", copyright='" + copyright + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
