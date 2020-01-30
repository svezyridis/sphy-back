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
import sphy.auth.models.Role;
import sphy.auth.models.Unit;
import sphy.auth.models.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
    public RestResponse token(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, HttpServletResponse response) {

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

            Cookie cookie = new Cookie("jwt", token);
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setSecure(false);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

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
    @PostMapping(value = "/users")
    public RestResponse register(@RequestBody NewUser newUser, @CookieValue(value = "jwt", defaultValue = "token") String token) {
        if(!(validator.validateUnitAdminToken(token)||validator.validateAdminToken(token)||validator.validateTeacherToken(token)))
            return new RestResponse("error", null,"invalid token");
        DecodedJWT jwt = validator.decode(token);
        User user=newUser.getNewUser();
        if (!verifyNewUser(user))
            return new RestResponse("error",null, "missing user attributes");
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        logger.info("[UserController]:[register]:{newUser : "+user+"}");
        Role role = userRepository.findRole(user.getRoleID());
        String newUserRole = role.getRole();
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

    @DeleteMapping(value = "/users/{username}")
    public RestResponse deleteUser(@CookieValue(value = "jwt", defaultValue = "token") String token,@PathVariable String username){
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

    @RequestMapping(value = "/users")
    public RestResponse getAllUsers(@CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)||validator.validateUnitAdminToken(token)))
            return new RestResponse("error", null, "invalid token");
        String userRole=validator.getUserRole(token);
        if(userRole.equals(Constants.ADMIN)){
            List<User> users=userRepository.findAll();
            if(users==null)
                return new RestResponse("error", null, "user could not be fetched");
            return new RestResponse("success",users,null);
        }
        // user is either unit_admin or teacher
        Integer userID=validator.getUserID(token);
        Integer unitID=userRepository.getUnitID(userID);
        List<User> users=userRepository.findAllUsersOfUnit(unitID);
        if(users==null)
            return new RestResponse("error", null, "user could not be fetched");
        return new RestResponse("success",users,null);
    }

    @PutMapping(value="/users")
    public RestResponse updateUser(@RequestBody User newUser,@CookieValue(value = "jwt", defaultValue = "token") String token,@RequestParam(value = "username") String username){
        User oldUser=userRepository.findByUsername(username);
        logger.info("[UserController]:[updateUser]:{oldUser : "+oldUser+"}");
        if(!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid token");
        if(oldUser==null)
            return new RestResponse("error", null, "user not found");
        //TODO ask which values will be allowed to change
        return  null;
    }

    @RequestMapping(value = "/roles")
    public RestResponse getRoles(@CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)))
            return new RestResponse("error", null, "invalid token");
        List<Role> roles=userRepository.getRoles();
        if(roles==null)
            return new RestResponse("error", null, "roles could not be fetched");
        return new RestResponse("success",roles,null);
    }

    @RequestMapping(value = "/units")
    public RestResponse getUnits(@CookieValue(value = "jwt", defaultValue = "token") String token){
        if(!(validator.validateAdminToken(token)||validator.validateTeacherToken(token)))
            return new RestResponse("error", null, "invalid token");
        List<Unit> units=userRepository.getUnits();
        if(units==null)
            return new RestResponse("error", null, "units could not be fetched");
        return new RestResponse("success",units,null);
    }

    private boolean verifyNewUser(User user) {
        logger.info("[UserController]:[verifyNewUser]:{user : "+user+"}");
        return user.getPassword() != null && user.getRoleID() != null && user.getFirstName() != null && user.getLastName() != null
                && user.getSerialNumber() != null && user.getUsername() != null;
    }
}
