package sphy.subject.models;

import java.util.List;

public class Subject {
    Integer categoryID;
    String category;
    String name;
    List<Image> images;
    String general;
    String units;
    Integer ID;
    String URI;
    Image defaultImage;
    Integer defaultImageID;

    public Image getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(Image defaultImage) {
        this.defaultImage = defaultImage;
    }

    public Integer getDefaultImageID() {
        return defaultImageID;
    }

    public void setDefaultImageID(Integer defaultImageID) {
        this.defaultImageID = defaultImageID;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getURI() {
        return URI;
    }

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


    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "categoryID=" + categoryID +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", images=" + images +
                ", general='" + general + '\'' +
                ", units='" + units + '\'' +
                ", ID=" + ID +
                ", URI='" + URI + '\'' +
                ", defaultImage=" + defaultImage +
                ", defaultImageID=" + defaultImageID +
                '}';
    }
}
