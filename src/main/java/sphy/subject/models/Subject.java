package sphy.subject.models;

import java.util.List;

public class Subject {
    Integer categoryID;
    String category;
    String name;
    List<Image> images;
    String text;
    Integer ID;

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getID() {
        return ID;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "categoryID=" + categoryID +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", images=" + images +
                ", text='" + text + '\'' +
                ", ID=" + ID +
                '}';
    }
}
