package sphy.evaluation.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sphy.evaluation.models.Answer;
import sphy.evaluation.models.Classroom;
import sphy.evaluation.models.Test;
import sphy.subject.db.RowMappers;
import sphy.subject.models.Option;
import sphy.subject.models.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTestRepository implements TestRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static class TestRowMapper implements RowMapper<Test> {
        @Override
        public Test mapRow(ResultSet rs, int rowNum) throws SQLException {
            Test test = new Test();
            test.setID(rs.getInt("ID"));
            test.setClassID(rs.getInt("classID"));
            test.setName(rs.getString("name"));
            test.setDuration(rs.getInt("duration"));
            test.setCreationTime(rs.getTimestamp("creationTime"));
            test.setActivationTime(rs.getTimestamp("activationTime"));
            test.setCompletionTime(rs.getTimestamp("completionTime"));
            return test;
        }
    }

    public static class AnswerRowMapper implements RowMapper<Answer> {
        @Override
        public Answer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Answer answer = new Answer();
            answer.setChoiceID(rs.getInt("choiceID"));
            answer.setQuestionID(rs.getInt("questionID"));
            answer.setUserID(rs.getInt("userID"));
            answer.setID(rs.getInt("ID"));
            return answer;
        }
    }

    @Override
    public Integer createTest(Test test) {
        String sql = "INSERT INTO TEST (classID, name, duration) VALUES (?,?,?)";
        Integer result = -1;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, test.getClassID());
                ps.setString(2, test.getName());
                ps.setInt(3, test.getDuration());
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            e.printStackTrace();
            return result;
        }
    }

    @Override
    public Integer submitAnswers( Integer studentID, List<Answer> answers) {
        int res = -1;
        String sql = "INSERT INTO TEST_ANSWER (userID, questionID, choiceID) VALUES (?,?,?)";
        try {
            int[] rows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setInt(1, studentID);
                    preparedStatement.setInt(2, answers.get(i).getQuestionID());
                    if(answers.get(i).getChoiceID()==null)
                        preparedStatement.setNull(3, Types.INTEGER);
                    else
                        preparedStatement.setInt(3, answers.get(i).getChoiceID());
                }

                @Override
                public int getBatchSize() {
                    return answers.size();
                }
            });
            res = 0;
            for (int row : rows)
                res += row;
        } catch (DataAccessException e) {
            return res;
        }
        return res;
    }

    @Override
    public List<Test> getAllTestsOfClass(Integer classID) {
        String sql = "SELECT * FROM  TOTAL_TEST WHERE  classid= ? order by ID,questionID,optionID";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{classID},
                    resultSet -> {
                        List<Test> result = new ArrayList<>();
                        Test currentTest = null;
                        List<Question> questions = new ArrayList<>();
                        List<Answer> answers = new ArrayList<>();
                        List<Option> options = new ArrayList<>();
                        Question currentQuestion = null;
                        Answer currentAnswer = null;
                        boolean newSubject = false;
                        while (resultSet.next()) {
                            Integer testID = resultSet.getInt("ID");
                            if (currentTest == null) { //initial object
                                currentTest = mapTest(resultSet);
                            } else if (currentTest.getID() != testID) {// break
                                questions.add(currentQuestion);
                                if (currentAnswer != null)
                                    answers.add(currentAnswer);
                                currentTest.setQuestions(questions);
                                currentTest.setAnswers(answers);
                                result.add(currentTest);
                                currentTest = mapTest(resultSet);
                                questions = new ArrayList<>();
                                answers = new ArrayList<>();
                                newSubject = true;
                            }
                            Integer questionID = resultSet.getInt("questionID");
                            if (currentQuestion == null) {//new or empty
                                currentQuestion = mapQuestion(resultSet);
                            } else if (currentQuestion.getID() != questionID) { //break
                                currentQuestion.setOptionList(options);
                                if (!newSubject)
                                    questions.add(currentQuestion);
                                currentQuestion = mapQuestion(resultSet);
                                options = new ArrayList<>();
                            }
                            options.add(mapOption(resultSet));
                            Integer answerID = resultSet.getInt("TAID");
                            if (currentAnswer == null) {//new or empty
                                currentAnswer = mapAnswer(resultSet);
                            } else if (currentAnswer.getID() != answerID) {
                                if (!newSubject)
                                    answers.add(currentAnswer);
                                currentAnswer = mapAnswer(resultSet);
                            }
                            newSubject = false;
                        }
                        if (currentTest != null) {
                            if (currentAnswer != null)
                                answers.add(currentAnswer);
                            if (currentQuestion != null)
                                currentQuestion.setOptionList(options);
                                questions.add(currentQuestion);
                            currentTest.setAnswers(answers);
                            currentTest.setQuestions(questions);
                            result.add(currentTest);
                        } //last test
                        return result;
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    private Test mapTest(ResultSet rs) {
        Test test = new Test();
        try {
            test.setID(rs.getInt("ID"));
            test.setClassID(rs.getInt("classID"));
            test.setName(rs.getString("name"));
            test.setDuration(rs.getInt("duration"));
            test.setCreationTime(rs.getTimestamp("creationTime"));
            test.setActivationTime(rs.getTimestamp("activationTime"));
            test.setCompletionTime(rs.getTimestamp("completionTime"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return test;
    }

    private Question mapQuestion(ResultSet resultSet) {
        Question question = new Question();
        try {
            question.setTestQuestionID(resultSet.getInt("testQuestionID"));
            question.setID(resultSet.getInt("questionID"));
            question.setAnswerReference(resultSet.getString("answerReference"));
            question.setText(resultSet.getString("questionText"));
            question.setImageID(resultSet.getInt("imageID"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return question.getID() != 0 ? question : null;
    }

    private Answer mapAnswer(ResultSet resultSet) {
        Answer answer = new Answer();
        try {
            answer.setID(resultSet.getInt("TAID"));
            answer.setChoiceID(resultSet.getInt("choiceID"));
            answer.setUserID(resultSet.getInt("userID"));
            answer.setQuestionID(resultSet.getInt("questionID"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return answer.getID() != 0 ? answer : null;
    }

    private Option mapOption(ResultSet resultSet) {
        Option option = new Option();
        try {
            option.setCorrect(resultSet.getBoolean("correct"));
            option.setID(resultSet.getInt("optionID"));
            option.setText(resultSet.getString("optionText"));
            option.setQuestionID(resultSet.getInt("questionID"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return option;
    }

    @Override
    public Integer updateTest(Test test) {
        String sql = "UPDATE  TEST SET activationTime=ifnull(?,activationTime),completionTime=ifnull(?,completionTime) WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, test.getActivationTime(), test.getCompletionTime(), test.getID());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Test getTestByID(Integer testID) {
        String sql = "SELECT * FROM  TOTAL_TEST WHERE  ID= ? order by ID,questionID,optionID";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{testID},
                    resultSet -> {
                        Test test = null;
                        List<Question> questions = new ArrayList<>();
                        List<Answer> answers = new ArrayList<>();
                        List<Option> options = new ArrayList<>();
                        Question currentQuestion = null;
                        Answer currentAnswer = null;
                        while (resultSet.next()) {
                            if (test == null) { //initial object
                                test = mapTest(resultSet);
                            }
                            Integer questionID = resultSet.getInt("questionID");
                            if (currentQuestion == null) {//new or empty
                                currentQuestion = mapQuestion(resultSet);
                            } else if (currentQuestion.getID() != questionID) { //break
                                currentQuestion.setOptionList(options);
                                questions.add(currentQuestion);
                                currentQuestion = mapQuestion(resultSet);
                                options = new ArrayList<>();
                            }
                            options.add(mapOption(resultSet));
                            Integer answerID = resultSet.getInt("TAID");
                            if (currentAnswer == null) {//new or empty
                                currentAnswer = mapAnswer(resultSet);
                            } else if (currentAnswer.getID() != answerID) {
                                answers.add(currentAnswer);
                                currentAnswer = mapAnswer(resultSet);
                            }
                        }
                        if (test != null) {
                            if (currentAnswer != null)
                                answers.add(currentAnswer);
                            if (currentQuestion != null)
                                currentQuestion.setOptionList(options);
                            questions.add(currentQuestion);
                            test.setAnswers(answers);
                            test.setQuestions(questions);
                        }
                        return test;
                    });
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer addQuestionsToTest(Integer testID, List<Integer> categoryIDs, Integer noOfQuestions) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("IDs", categoryIDs);
        parameters.addValue("testID", testID);
        parameters.addValue("noOfQuestions", noOfQuestions);
        Integer res = -1;
        String sql = "INSERT INTO TEST_QUESTION (questionID, testID)  " +
                "SELECT QUESTION.ID,:testID AS ID FROM QUESTION INNER JOIN SUBJECT S on QUESTION.subjectID = S.ID " +
                "INNER JOIN CATEGORY C on S.categoryID = C.ID WHERE categoryID IN (:IDs) ORDER BY RAND() LIMIT :noOfQuestions";
        try {
            res = namedParameterJdbcTemplate.update(sql, parameters);
            System.out.println(res);
            return res;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Integer deleteTest(Integer testID) {
        String sql = "DELETE FROM TEST WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, testID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean hasSubmitted(Integer userID, Integer testID) {
        String sql = "SELECT TA.ID FROM TEST_ANSWER TA INNER JOIN TEST_QUESTION TQ on TA.questionID = TQ.ID INNER JOIN TEST T on TQ.testID = T.ID WHERE T.ID=? AND  TA.userID=?";
        try {
            Integer result=jdbcTemplate.queryForObject(sql,
                    new Object[]{testID,userID},(resultSet, i) ->{
                        int size =0;
                        if (resultSet != null)
                        {
                            resultSet.last();    // moves cursor to the last row
                            size = resultSet.getRow(); // get row id
                        }
                        return size;
                    });
            return result>0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
