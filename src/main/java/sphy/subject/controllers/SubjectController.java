package sphy.subject.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sphy.Validator;
import sphy.subject.models.*;
import sphy.subject.db.SubjectRepository;

import java.util.Collections;
import java.util.List;


@RestController
public class SubjectController {
    Logger logger = LoggerFactory.getLogger(SubjectController.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcSubjectRepository")
    private SubjectRepository subjectRepository;

    @Autowired
    Validator validator;


    /**
     *
     * @param weapon
     * @param token
     * @return all categories of the specified weapon
     */
    @RequestMapping(value = "categories/{weapon}")
    public RestResponse getCategoriesByWeapon(@PathVariable String weapon, @RequestParam(value = "token") String token) {
        //TODO add error message
        if(!validator.simpleValidateToken(token))
            return new RestResponse("error",null,"token is invalid");
        Integer weaponID = subjectRepository.getWeaponID(weapon);
        List<Category> categories = subjectRepository.getCategoriesOfWeapon(weaponID);
        for(Category cat:categories){
            Image image=subjectRepository.getRandomImageOfCategory(cat.getID());
            cat.setRandomImage(image);
            logger.info(cat.toString());
        }
        return new RestResponse("success", categories,"");
    }

    /**
     *
     * @param category
     * @param token
     * @return all subjects of the specified category
     */
    @RequestMapping(value = "subjects/{weapon}/{category}")
    public RestResponse getSubjectByCategory(@PathVariable String category, @PathVariable String weapon, @RequestParam(value = "token") String token){
        //TODO add error message
        if(!validator.simpleValidateToken(token))
            return new RestResponse("error",null,"token is invalid");
        Integer weaponID = subjectRepository.getWeaponID(weapon);
        Integer categoryID= subjectRepository.getCategoryID(category,weaponID);
        List<Subject> subjects=subjectRepository.getSubjectsOfCategory(categoryID);
        for(Subject sub:subjects){
            List<Image> images=subjectRepository.getImagesOfSubject(sub.getID());
            sub.setImages(images);
            sub.setCategory(category);
        }
        return  new RestResponse("success", subjects,"");
    }


}
