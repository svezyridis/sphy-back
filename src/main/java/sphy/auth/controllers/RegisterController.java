package sphy.auth.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sphy.Constants;
import sphy.ECDSA;
import sphy.auth.db.UserRepository;
import sphy.auth.models.RegisterBody;
import sphy.auth.models.RegisterResponse;
import sphy.auth.models.User;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;

@RestController
public class RegisterController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${publicKey}")
    private String publicKeyStr;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    /**
     *
     * @param newRegisterBody Json body of the form:
     *                        {
     *                          token:JWT token,
     *                          newUser:{
     *                              serialNumber,
     *                              username,
     *                              password,
     *                              firstName,
     *                              lastName,
     *                              role,
     *                              rank
     *                          }
     *                      }
     * @return JSON message of the form:
     * {
     *     status:"success/error"
     *     message: error message (if any)
     * }
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RegisterResponse register(@RequestBody RegisterBody newRegisterBody) {
        String token = newRegisterBody.getToken();

        //Verify token
        DecodedJWT jwt = null;
        try {
            ECPublicKey publicKey = ECDSA.reconstructPublicKey(publicKeyStr);
            Algorithm algorithm = Algorithm.ECDSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(Constants.IDENTIFIER)
                    .build(); //Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return new RegisterResponse("error", "invalid token");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        User user = newRegisterBody.getNewUser();
        System.out.println(user);
        if (!verifyNewUser(user))
            return new RegisterResponse("error", "missing user attributes");


        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        String newUserRole = user.getRole();
        Integer roleID = userRepository.findRoleID(newUserRole);
        if (roleID == null)
            return new RegisterResponse("error", "role does not exist");
        else
            user.setRoleID(roleID);

        //verify role rights
        String CreatorRole = jwt.getClaim("role").asString();
        switch (CreatorRole.toUpperCase()) {
            case Constants.ADMIN: {
                int res = userRepository.createUser(user);
                if (res == 1)
                    return new RegisterResponse("success", "user " + user.getUsername() + " successfully created");
                else if (res==-1)
                    return new RegisterResponse("error", "user already exists");
                else
                    return new RegisterResponse("error", "user creation failed");
            }
            case  Constants.UNIT_ADMIN: {
                switch (newUserRole.toUpperCase()) {
                    case Constants.ADMIN:
                    case Constants.UNIT_ADMIN:
                        return new RegisterResponse("error", "you don't have the rights to create " + newUserRole + " account");
                    case  Constants.TEACHER:
                    case Constants.USER:
                        int res = userRepository.createUser(user);
                        if (res == 1)
                            return new RegisterResponse("success", "user " + user.getUsername() + " successfully created");
                        else if (res==-1)
                            return new RegisterResponse("error", "user already exists");
                        else
                            return new RegisterResponse("error", "user creation failed");
                    default:
                        return new RegisterResponse("error", "unknown new user role");
                }
            }
            case Constants.TEACHER: {
                switch (newUserRole.toUpperCase()) {
                    case Constants.ADMIN:
                    case Constants.UNIT_ADMIN:
                    case Constants.TEACHER:
                        return new RegisterResponse("error", "you don't have the rights to create " + newUserRole + " account");
                    case Constants.USER:
                        int res = userRepository.createUser(user);
                        if (res == 1)
                            return new RegisterResponse("success", "user " + user.getUsername() + " successfully created");
                        else if (res==-1)
                            return new RegisterResponse("error", "user already exists");
                        else
                            return new RegisterResponse("error", "user creation failed");
                    default:
                        return new RegisterResponse("error", "unknown new user role");
                }
            }
        }

        return new RegisterResponse("success", "user successfully created");
    }

    private boolean verifyNewUser(User user) {
        System.out.println(user.getUsername()+user.getPassword()+user.getRole()+user.getFirstName()+user.getLastName()+user.getSerialNumber());
        return user.getPassword() != null && user.getRole() != null && user.getFirstName() != null && user.getLastName() != null
                && user.getSerialNumber() != null && user.getUsername() != null;
    }
}
