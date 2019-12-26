package sphy.subject.models;

public class Image {
    String URL;
    String label;

    public String getLabel() {
        return label;
    }

    public String getURL() {
        return URL;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Image{" +
                "URL='" + URL + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
