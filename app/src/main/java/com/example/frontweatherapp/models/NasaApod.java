package com.example.frontweatherapp.models;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class NasaApod implements Serializable {

    @SerializedName("date")
    private String date;

    @SerializedName("explanation")
    private String explanation;

    @SerializedName("hdurl")
    private String hdurl; // ¡Campo añadido! URL de la imagen en alta definición

    @SerializedName("media_type")
    private String mediaType; // Puede ser "image" o "video"

    @SerializedName("service_version")
    private String serviceVersion;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url; // URL de la imagen o video

    @SerializedName("copyright")
    private String copyright; // Opcional, puede no estar presente

    // Getters y Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getHdurl() { // ¡Getter añadido!
        return hdurl;
    }

    public void setHdurl(String hdurl) { // ¡Setter añadido!
        this.hdurl = hdurl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
