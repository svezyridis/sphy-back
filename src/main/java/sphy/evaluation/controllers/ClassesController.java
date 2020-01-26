package sphy.evaluation.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import sphy.RestResponse;
import sphy.Validator;
import sphy.auth.db.UserRepository;
import sphy.evaluation.db.ClassRepository;
import sphy.subject.controllers.CategoryController;
import sphy.subject.db.CategoryRepository;

import java.util.Map;

@RestController
public class ClassesController {
    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcClassRepository")
    private ClassRepository classRepository;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    @Autowired
    Validator validator;

    @PostMapping(value = "class/{className}")
    public RestResponse createClass(@PathVariable String className, @CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)))
            return new RestResponse("error", null,"invalid token");
        String[] arrOfStr = token.split(" ");
        token=arrOfStr[1].replace("\"", "");
        System.out.println(token);
        DecodedJWT decrypted=JWT.decode(token);
        String metadata=decrypted.getClaim("metadata").asString();
        ObjectMapper mapper = new ObjectMapper();
        Integer id= null;
        try {
            id = mapper.readTree(metadata).get("id").asInt();
        } catch (JsonProcessingException e) {
            return new RestResponse("error", null,"teacher id not found in token");
        }


        return null;
    }
}
