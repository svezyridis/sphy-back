package sphy.evaluation.models;

import sphy.subject.models.Question;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class Test {
    Integer ID;
    Integer classID;
    String name;
    //in minutes
    Integer duration;
    Date creationDate;
    LocalDateTime activationTime;
    List<Question> questions;
    List<Answer> answers;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getClassID() {
        return classID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(LocalDateTime activationTime) {
        this.activationTime = activationTime;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
