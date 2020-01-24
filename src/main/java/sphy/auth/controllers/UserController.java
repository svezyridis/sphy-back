package sphy.auth.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sphy.Constants;
import sphy.ECDSA;
import sphy.RestResponse;
import sphy.Validator;
import sphy.auth.db.UserRepository;
import sphy.auth.models.NewUser;
import sphy.auth.models.User;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);


    @Value("${privateKey}")
    private String privateKeyStr;

    @Value("${publicKey}")
    private String publicKeyStr;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    @Autowired
    Validator validator;

    /**
     * @param username
     * @param password
     * @return a LoginResponse object with of the form
     * {
     * status:"success/error"
     * token:JWT token if login succeeded null otherwise
     * message:error message if any
     * }
     */
    @RequestMapping("/login")
    public RestResponse token(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            logger.info("user not found error");
            return new RestResponse("error", null, "user not found");
        } else if (!BCrypt.checkpw(password, user.getPassword())) {
            return new RestResponse("error", null, "wrong password");
        } else {
            String token = null;
            try {
                ECPrivateKey privateKey = ECDSA.reconstructPrivateKey(privateKeyStr);
                Algorithm algorithm = Algorithm.ECDSA256(null, privateKey);

                // Creating Object of ObjectMapper define in Jakson Api
                ObjectMapper obj = new ObjectMapper();
                //do not include null values in JSON
                obj.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                // get User object as a json string
                String jsonStr = obj.writeValueAsString(user);
                // Displaying JSON String
                logger.info("[UserController]:[token]: "+jsonStr);
                token = JWT.create()
                        .withIssuer(Constants.IDENTIFIER)
                        .withClaim("metadata", jsonStr)
                        .withClaim("role",user.getRole())
                        .sign(algorithm);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return new RestResponse("error", null, "internal error");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new RestResponse("error", null, "internal error");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new RestResponse("error", null, "could not fetch user metadata");
            }

            try {
                DecodedJWT jwt = JWT.decode(token);
                Map<String, Claim> decoded = jwt.getClaims();
                decoded.forEach((key, value) -> System.out.println("Key : " + key + " Value : " + value.asString()));
            } catch (JWTDecodeException exception) {
                //Invalid token
            }

            return new RestResponse("success", token, "");
        }
    }

    /**
     *
     * @param newUser Json body of the form:
     *                          newUser:{
     *                              serialNumber,
     *                              username,
     *                              password,
     *                              firstName,
     *                              lastName,
     *                              role,
     *                              rank
     *                          }
     * @return JSON message of the form:
     * {
     *     status:"success/error"
     *     message: error message (if any)
     * }
     */
    @PostMapping(value = "/user")
    public RestResponse register(@RequestBody NewUser newUser, @RequestHeader("authorization") String token) {

        if(!validator.simpleValidateToken(token))
            return new RestResponse("error", null,"invalid token");
        DecodedJWT jwt = validator.decode(token);
        User user=newUser.getNewUser();
        logger.info("[UserController]:[register]:{newUser : "+user+"}");
        System.out.println(user);
        if (!verifyNewUser(user))
            return new RestResponse("error",null, "missing user attributes");


        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        String newUserRole = user.getRole();
        Integer roleID = userRepository.findRoleID(newUserRole);
        if (roleID == null)
            return new RestResponse("error",null, "role does not exist");
        else
            user.setRoleID(roleID);

        //verify role rights
        String CreatorRole = jwt.getClaim("role").asString();
        switch (CreatorRole.toUpperCase()) {
            case Constants.ADMIN: {
                int res = userRepository.createUser(user);
                if (res == 1)
                    return new RestResponse("success",user, "user " + user.getUsername() + " successfully created");
                else if (res==-1)
                    return new RestResponse("error",null, "user already exists");
                else
                    return new RestResponse("error",null, "user creation failed");
            }
            case  Constants.UNIT_ADMIN: {
                switch (newUserRole.toUpperCase()) {
                    case Constants.ADMIN:
                    case Constants.UNIT_ADMIN:
                        return new RestResponse("error", null,"you don't have the rights to create " + newUserRole + " account");
                    case  Constants.TEACHER:
                    case Constants.USER:
                        int res = userRepository.createUser(user);
                        if (res == 1)
                            return new RestResponse("success",user, "user " + user.getUsername() + " successfully created");
                        else if (res==-1)
                            return new RestResponse("error",null, "user already exists");
                        else
                            return new RestResponse("error",null, "user creation failed");
                    default:
                        return new RestResponse("error",null, "unknown new user role");
                }
            }
            case Constants.TEACHER: {
                switch (newUserRole.toUpperCase()) {
                    case Constants.ADMIN:
                    case Constants.UNIT_ADMIN:
                    case Constants.TEACHER:
                        return new RestResponse("error", null,"you don't have the rights to create " + newUserRole + " account");
                    case Constants.USER:
                        int res = userRepository.createUser(user);
                        if (res == 1)
                            return new RestResponse("success",user, "user " + user.getUsername() + " successfully created");
                        else if (res==-1)
                            return new RestResponse("error",null, "user already exists");
                        else
                            return new RestResponse("error",null, "user creation failed");
                    default:
                        return new RestResponse("error",null, "unknown new user role");
                }
            }
        }

        return new RestResponse("success",user, "user " + user.getUsername() + " successfully created");
    }

    @DeleteMapping(value = "/user")
    public RestResponse deleteUser(@RequestHeader("authorization") String token,@RequestParam(value="username") String username){
        logger.info(username);
        if(!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid token");
        User user=userRepository.findByUsername(username);
        if(user==null)
            return new RestResponse("error", null, "user not found");
        Integer res=userRepository.deleteUser(user.getID());
        if(res==-1)
            return new RestResponse("error", null, "user could not be deleted");
        return new RestResponse("success",user,"user successfully deleted");
    }

    @RequestMapping(value = "/user")
    public RestResponse getAllUsers(@RequestHeader("authorization") String token){
        if(!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid token");
        List<User> users=userRepository.findAll();
        if(users==null)
            return new RestResponse("error", null, "user could not be fetched");
        return new RestResponse("success",users,null);
    }

    @PutMapping(value="/user")
    public RestResponse updateUser(@RequestBody User newUser,@RequestHeader("authorization") String token,@RequestParam(value = "username") String username){
        User oldUser=userRepository.findByUsername(username);
        logger.info("[UserController]:[updateUser]:{oldUser : "+oldUser+"}");
        if(!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid token");
        if(oldUser==null)
            return new RestResponse("error", null, "user not found");
        //TODO ask which values will be allowed to change
        return  null;
    }

    private boolean verifyNewUser(User user) {
        logger.info("[UserController]:[verifyNewUser]:{user : "+user+"}");
        return user.getPassword() != null && user.getRole() != null && user.getFirstName() != null && user.getLastName() != null
                && user.getSerialNumber() != null && user.getUsername() != null;
    }
}
