package sphy.subject.models;

public class Category {
    String name;
    Integer weaponID;
    Image image;
    Integer ID;
    String URI;
    Integer imageID;
    String branch;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImageID(Integer imageID) {
        this.imageID = imageID;
    }

    public Integer getImageID() {
        return imageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeaponID() {
        return weaponID;
    }

    public void setWeaponID(Integer weaponID) {
        this.weaponID = weaponID;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", weaponID=" + weaponID +
                ", image=" + image +
                ", ID=" + ID +
                ", URI='" + URI + '\'' +
                ", imageID=" + imageID +
                '}';
    }
}
