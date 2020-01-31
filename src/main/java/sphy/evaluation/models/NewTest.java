package sphy.evaluation.models;

import java.util.List;

public class NewTest {
    Test test;
    List<Integer> categoryIDs;
    Integer noOfQuestions;

    public List<Integer> getCategoryIDs() {
        return categoryIDs;
    }

    public void setCategoryIDs(List<Integer> categoryIDs) {
        this.categoryIDs = categoryIDs;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Integer getNoOfQuestions() {
        return noOfQuestions;
    }

    public void setNoOfQuestions(Integer noOfQuestions) {
        this.noOfQuestions = noOfQuestions;
    }
}
