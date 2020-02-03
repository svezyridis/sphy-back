package sphy.evaluation.models;

import sphy.auth.models.User;

import java.sql.Date;
import java.sql.Timestamp;

public class Student extends User {
    Timestamp timeAdded;

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
