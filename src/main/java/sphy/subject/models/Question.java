package sphy.subject.models;

import java.util.List;

public class Question {
    private Integer ID;
    private String text;
    private String answerReference;
    private List<Option> optionList;
    private Integer imageID;
    private  Image image;

    public Integer getImageID() {
        return imageID;
    }

    public void setImageID(Integer imageID) {
        this.imageID = imageID;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswerReference() {
        return answerReference;
    }

    public void setAnswerReference(String answerReference) {
        this.answerReference = answerReference;
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }

    @Override
    public String toString() {
        return "Question{" +
                "ID=" + ID +
                ", text='" + text + '\'' +
                ", answerReference='" + answerReference + '\'' +
                ", optionList=" + optionList +
                ", imageID=" + imageID +
                ", image=" + image +
                '}';
    }
}
