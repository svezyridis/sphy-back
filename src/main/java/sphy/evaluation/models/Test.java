package sphy.evaluation.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import sphy.subject.models.Question;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Test {
    Integer ID;
    Integer classID;
    String name;
    //in minutes
    Integer duration;
    Timestamp creationTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    Timestamp activationTime;
    List<Question> questions;
    List<Answer> answers;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    Timestamp completionTime;

    public Timestamp getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Timestamp completionTime) {
        this.completionTime = completionTime;
    }

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

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Timestamp getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Timestamp activationTime) {
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

    @Override
    public String toString() {
        return "Test{" +
                "ID=" + ID +
                ", classID=" + classID +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", creationTime=" + creationTime +
                ", activationTime=" + activationTime +
                ", questions=" + questions +
                ", answers=" + answers +
                ", completionTime=" + completionTime +
                '}';
    }
}
