package sphy.subject.models;

public class Category {
    String name;
    Integer weaponID;
    Image randomImage;
    Integer ID;
    String URI;

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
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

    public Image getRandomImage() {
        return randomImage;
    }

    public void setRandomImage(Image randomImage) {
        this.randomImage = randomImage;
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
                ", randomImage=" + randomImage +
                ", ID=" + ID +
                '}';
    }
}
