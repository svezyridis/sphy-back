package sphy.subject.models;

public class Image {
    String filename;
    String label;
    String subject;
    Integer ID;

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getID() {
        return ID;
    }

    public String getLabel() {
        return label;
    }

    public String getFilename() {
        return filename;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Image{" +
                "filename='" + filename + '\'' +
                ", label='" + label + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
