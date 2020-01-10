package sphy.subject.models;

import java.util.List;

public class Question {
    private Integer ID;
    private String text;
    private String answerReference;
    private List<Option> optionList;
    private String Image;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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
}
