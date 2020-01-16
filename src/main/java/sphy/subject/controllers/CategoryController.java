package sphy.subject.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sphy.RestResponse;
import sphy.Validator;
import sphy.subject.db.CategoryRepository;
import sphy.subject.models.Category;
import sphy.subject.models.Image;

import java.util.List;


@RestController
public class CategoryController {
    Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcCategoryRepository")
    private CategoryRepository categoryRepository;

    @Autowired
    Validator validator;

    /**
     *
     * @param weapon
     * @param token
     * @return all categories of the specified weapon
     */
    @RequestMapping(value = "category/{weapon}")
    public RestResponse getCategoriesByWeapon(@PathVariable String weapon,@RequestHeader("authorization") String token) {
        logger.info("[CategoryController]:[getCategoriesByWeapon]:{weapon: "+weapon+" }");
        if(!validator.simpleValidateToken(token))
            return new RestResponse("error",null,"token is invalid");

        Integer weaponID = categoryRepository.getWeaponID(weapon);
        if(weaponID==-1)
            return new RestResponse("error",null,"weapon does not exist");

        List<Category> categories = categoryRepository.getCategoriesOfWeapon(weaponID);
        for(Category cat:categories){
            Image image=categoryRepository.getImageOfCategory(cat.getImageID());
            cat.setImage(image);
            logger.info(cat.toString());
        }
        return new RestResponse("success", categories,null);
    }

    @PostMapping(value = "category/{weapon}")
    public RestResponse createCategory(@RequestHeader("authorization") String token,@PathVariable String weapon, @RequestBody Category category){
        logger.info("[CategoryController]:[createCategory]:{weapon: "+weapon+", category :"+ category +"}");
        if(!validator.validateAdminToken(token))
            return new RestResponse("error",null,"token is not a valid ADMIN token");

        Integer weaponID = categoryRepository.getWeaponID(weapon);
        if(weaponID==-1)
            return new RestResponse("error",null,"weapon does not exist");

        Integer categoryID= categoryRepository.getCategoryID(category.getURI(),weaponID);
        if(categoryID!=-1)
            return new RestResponse("error",null,"category already exists");

        int res=categoryRepository.createCategory(category,weaponID);
        if(res==0)
            return new RestResponse("error",null,"category creation failed");

        return new RestResponse("success",category,null);
    }

    @DeleteMapping(value = "category/{weapon}/{category}")
    public RestResponse deleteCategory(@PathVariable String weapon,@PathVariable String category,@RequestHeader("authorization") String token){
        logger.info("[CategoryController]:[deleteCategory]:{weapon: "+weapon+", category :"+ category +"}");
        if(!validator.validateAdminToken(token))
            return new RestResponse("error",null,"token is not a valid ADMIN token");

        Integer weaponID = categoryRepository.getWeaponID(weapon);
        if(weaponID==-1)
            return new RestResponse("error",null,"weapon does not exist");

        Integer categoryID= categoryRepository.getCategoryID(category,weaponID);
        if(categoryID==-1)
            return new RestResponse("error",null,"category does not exist");
        Integer res=categoryRepository.deleteCategory(categoryID);
        if(res==-1)
            return new RestResponse("error",null,"category could not be deleted");
        else
            return new RestResponse("success",null,"category deleted successfully");
    }

    @PutMapping(value = "category/{weapon}/{category}")
    public RestResponse changeCategoryName(@PathVariable String weapon,@PathVariable String category, @RequestParam("name") String newName, @RequestHeader("authorization") String token){
        logger.info("[CategoryController]:[changeCategoryName]:{weapon: "+weapon+", category :"+ category +", name: "+newName +"}");
        if(!validator.validateAdminToken(token))
            return new RestResponse("error",null,"token is not a valid ADMIN token");
        Integer weaponID = categoryRepository.getWeaponID(weapon);
        if(weaponID==-1)
            return new RestResponse("error",null,"weapon does not exist");

        Integer categoryID= categoryRepository.getCategoryID(category,weaponID);
        if(categoryID==-1)
            return new RestResponse("error",null,"category does not exist");
        Integer res=categoryRepository.updateCategory(categoryID,newName);
        if(res==-1)
            return new RestResponse("error",null,"category could not be updated");
        else
            return new RestResponse("success",null,"category updated successfully");
    }
}
