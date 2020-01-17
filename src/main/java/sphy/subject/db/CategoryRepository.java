package sphy.subject.db;

import sphy.subject.models.Category;
import sphy.subject.models.Image;

import java.util.List;

public interface CategoryRepository {
    List<Category> getCategoriesOfWeapon(Integer weaponID);
    Integer getCategoryID(String category,Integer weaponID);
    Integer getWeaponID(String weapon);
    Image getImage(Integer imageID);
    Integer createCategory(Category category, Integer weaponID);
    Integer deleteCategory(Integer categoryID);
    Integer updateCategory(Integer categoryID,String newName);
}
