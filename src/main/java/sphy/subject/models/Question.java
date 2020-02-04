package sphy.subject.models;

import java.util.List;

public class Question {
    private Integer ID;
    private String text;
    private String answerReference;
    private List<Option> optionList;
    private Integer imageID;
    private  Image image;
    private String subject;
    private String branch;
    private String category;
    private Integer testQuestionID;

    public Integer getTestQuestionID() {
        return testQuestionID;
    }

    public void setTestQuestionID(Integer testQuestionID) {
        this.testQuestionID = testQuestionID;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

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
