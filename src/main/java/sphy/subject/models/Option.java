package sphy.subject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Option {
    @JsonIgnore
    private Integer ID;
    private String text;
    private boolean correct;
    @JsonIgnore
    private Integer questionID;
    @JsonIgnore
    public Integer getID() {
        return ID;
    }
    @JsonProperty
    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
    @JsonIgnore
    public Integer getQuestionID() {
        return questionID;
    }
    @JsonProperty
    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }
}
