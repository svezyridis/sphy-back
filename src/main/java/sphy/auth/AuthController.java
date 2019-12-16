package sphy.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RestController
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Value("${privateKey}")
    private String privateKeyStr;

    @Value("${publicKey}")
    private String publicKeyStr;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RegisterResponse register(@RequestBody RegisterModel newRegisterModel) {
        String token = newRegisterModel.getToken();

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
            return new RegisterResponse("error", "invalid token");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        User user = newRegisterModel.getNewUser();
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

    @RequestMapping("/login")
    public LoginResponse token(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new LoginResponse("error", null, "user not found");
        } else if (!BCrypt.checkpw(password, user.getPassword())) {
            return new LoginResponse("error", null, "wrong password");
        } else {
            String token = null;
            try {
                ECPrivateKey privateKey = ECDSA.reconstructPrivateKey(privateKeyStr);
                Algorithm algorithm = Algorithm.ECDSA256(null, privateKey);
                token = JWT.create()
                        .withIssuer(Constants.IDENTIFIER)
                        .withClaim("user", username)
                        .withClaim("role", user.getRole())
                        .sign(algorithm);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return new LoginResponse("error", token, "internal error");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new LoginResponse("error", token, "internal error");
            }

            try {
                DecodedJWT jwt = JWT.decode(token);
                Map<String, Claim> decoded = jwt.getClaims();
                decoded.forEach((key, value) -> System.out.println("Key : " + key + " Value : " + value.asString()));
            } catch (JWTDecodeException exception) {
                //Invalid token
            }
            return new LoginResponse("success", token, "");
        }
    }

    private boolean verifyNewUser(User user) {
        return user.getPassword() != null && user.getRole() != null && user.getFirstName() != null && user.getLastName() != null
                && user.getSerialNumber() != null && user.getUsername() != null;
    }

}
