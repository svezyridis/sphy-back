package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sphy.subject.models.Category;
import sphy.subject.models.Image;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcCategoryRepository implements CategoryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *
     * @param weaponID
     * @return all categories of the specified weapon
     */
    @Override
    public List<Category> getCategoriesOfWeapon(Integer weaponID) {
        String sql = "SELECT CATEGORY.ID AS ID, CATEGORY.name AS name, weaponID, CATEGORY.URI as URI, IMAGE.ID as imageID, filename, label, SUBJECT.URI as subject " +
                "FROM CATEGORY LEFT  JOIN IMAGE ON IMAGE.ID = CATEGORY.imageID " +
                "LEFT JOIN SUBJECT ON SUBJECT.ID = IMAGE.subjectID "+
                "WHERE weaponID=?";
        try {
            return jdbcTemplate.query(sql,
                    new Object[]{weaponID},
                    new RowMappers.CategoryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * @param weapon the role to look for
     * @return the id of the weapon
     */

    @Override
    public Integer getWeaponID(String weapon) {
        String sql = "select ID from WEAPON where NAME = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{weapon},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public Integer getCategoryID(String category, Integer weaponID) {
        System.out.println(category+" "+weaponID);
        String sql = "select ID from CATEGORY where URI = ? AND weaponID= ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{category,weaponID},
                    (rs, rowNum) ->
                            rs.getInt("ID"));
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public Category getCategoryByURI(String URI) {
        String sql = "SELECT CATEGORY.ID AS ID, CATEGORY.name AS name, weaponID, CATEGORY.URI as URI, IMAGE.ID as imageID, filename, label, SUBJECT.URI as subject " +
                "FROM CATEGORY LEFT  JOIN IMAGE ON IMAGE.ID = CATEGORY.imageID " +
                "LEFT JOIN SUBJECT ON SUBJECT.ID = IMAGE.subjectID "+
                "WHERE CATEGORY.URI=?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{URI},
                    new RowMappers.CategoryRowMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }


    @Override
    public Image getImage(Integer imageID){
        String sql = "select filename,label,SUBJECT.URI as subject, IMAGE.ID as ID from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID where IMAGE.ID=?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{imageID},
                    new RowMappers.ImageRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer createCategory(Category category, Integer weaponID) {
        String sql = "INSERT INTO CATEGORY (NAME, WEAPONID, URI) VALUES (?,?,?)";
        int res=0;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, category.getName());
                ps.setInt(2,weaponID);
                ps.setString(3,category.getURI());
                return ps;
            }, keyHolder);
            System.out.println(keyHolder.getKey().intValue());
            return  keyHolder.getKey().intValue();
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Integer deleteCategory(Integer categoryID) {
        String sql = "DELETE FROM CATEGORY WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, categoryID);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Integer updateCategory(Category category) {
        String sql = "UPDATE CATEGORY SET name= IFNULL(?,name), uri=IFNULL(?,uri), imageID=(IFNULL(?,imageID)) WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, category.getName(),category.getURI(),category.getImageID(),category.getID());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }
}
