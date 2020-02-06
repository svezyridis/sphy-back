package sphy.evaluation.models;

import sphy.auth.models.User;
import sphy.subject.models.Question;

public class Answer {
    Integer ID;
    Integer userID;
    Integer questionID;
    Integer choiceID;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public Integer getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(Integer choiceID) {
        this.choiceID = choiceID;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "ID=" + ID +
                ", userID=" + userID +
                ", questionID=" + questionID +
                ", choiceID=" + choiceID +
                '}';
    }
}
