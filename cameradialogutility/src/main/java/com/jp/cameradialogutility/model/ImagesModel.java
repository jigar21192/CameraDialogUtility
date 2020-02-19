package com.jp.cameradialogutility.model;


import java.io.Serializable;

public class ImagesModel implements Serializable {
    private String image = "";
    private int is_selected = 0;
    private String thumb = "";
    private String name;


    public ImagesModel(String filename, int is_selected, String thumb, String name) {
        this.image = filename;
        this.is_selected = is_selected;
        this.thumb = thumb;
        this.name = name;
    }

    public ImagesModel() {
    }


    public String getImage() {
        return image;
    }

    public String getNormalFilename() {
        if (!image.isEmpty() && image.contains("file://")) {
            return image.replace("file://", "");
        }
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(int is_selected) {
        this.is_selected = is_selected;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
