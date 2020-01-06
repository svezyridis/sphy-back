package sphy.subject.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sphy.subject.models.Category;
import sphy.subject.models.Image;
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
        String sql = "SELECT * FROM CATEGORY WHERE weaponID=?";
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
        String sql = "select ID from CATEGORY where NAME = ? AND weaponID= ?";
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
    public Image getRandomImageOfCategory(Integer categoryID){
        String sql = "select filename,label,SUBJECT.name as subject, IMAGE.ID as ID from IMAGE INNER JOIN SUBJECT on IMAGE.subjectID=SUBJECT.ID " +
                "INNER JOIN CATEGORY ON CATEGORY.ID = SUBJECT.categoryID WHERE CATEGORY.ID = ? " +
                "ORDER BY RAND() LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{categoryID},
                    new RowMappers.ImageRowMapper());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer createCategory(String category, Integer weaponID) {
        String sql = "INSERT INTO CATEGORY (NAME, WEAPONID) VALUES (?,?)";
        int res=0;
        try {
            res =jdbcTemplate.update(sql, category,weaponID);
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
    public Integer updateCategory(Integer categoryID, String newName) {
        String sql = "UPDATE CATEGORY SET name=? WHERE ID=?";
        Integer res = -1;
        try {
            res = jdbcTemplate.update(sql, newName);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return res;
    }
}
