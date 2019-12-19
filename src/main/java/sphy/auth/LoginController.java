package sphy.auth;

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
import sphy.auth.models.LoginResponse;
import sphy.auth.models.User;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RestController
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Value("${privateKey}")
    private String privateKeyStr;

    @Value("${publicKey}")
    private String publicKeyStr;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;

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
    public LoginResponse token(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.info("user not found error");
            return new LoginResponse("error", null, "user not found");
        } else if (!BCrypt.checkpw(password, user.getPassword())) {
            return new LoginResponse("error", null, "wrong password");
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
                logger.info(jsonStr);
                token = JWT.create()
                        .withIssuer(Constants.IDENTIFIER)
                        .withClaim("metadata", jsonStr)
                        .withClaim("role",user.getRole())
                        .sign(algorithm);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return new LoginResponse("error", token, "internal error");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new LoginResponse("error", token, "internal error");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new LoginResponse("error", token, "could not fetch user metadata");
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
}
