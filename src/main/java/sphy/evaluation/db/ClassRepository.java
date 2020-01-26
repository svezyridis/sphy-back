package sphy.evaluation.db;

import sphy.evaluation.models.Class;

public interface ClassRepository {
    Integer createClass(String className,Integer creatorID);
}
