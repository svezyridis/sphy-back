package sphy.evaluation.models;

import sphy.auth.models.User;

import java.sql.Date;
import java.util.List;

public class Classroom {
    Integer ID;
    String name;
    Integer creatorID;
    Date creationDate;
    List<Student> students;
    List<Test> tests;
    Integer noOfTests;
    String unit;


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getNoOfTests() {
        return noOfTests;
    }

    public void setNoOfTests(Integer noOfTests) {
        this.noOfTests = noOfTests;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
