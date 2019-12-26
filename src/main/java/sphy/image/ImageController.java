package sphy.image;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sphy.Constants;
import sphy.image.storage.FileSystemStorageService;
import sphy.image.storage.StorageFileNotFoundException;
import sphy.ECDSA;

import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;


@RestController
public class ImageController {
    private final FileSystemStorageService storageService;

    @Value("${publicKey}")
    private String publicKeyStr;

    @Autowired
    public ImageController(FileSystemStorageService storageService) {
        this.storageService = storageService;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/image/{weapon}/{category}/{subject}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String weapon, @PathVariable String category, @PathVariable String subject, @PathVariable String filename, @RequestParam("token") String token)  {
        //Verify token
        DecodedJWT jwt = null;
        try {
            ECPublicKey publicKey = ECDSA.reconstructPublicKey(publicKeyStr);
            Algorithm algorithm = Algorithm.ECDSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(Constants.IDENTIFIER)
                    .build();
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return (ResponseEntity<Resource>) ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        Resource file = storageService.loadAsResource(weapon+'/'+category+'/'+subject+'/'+filename);
        String mimeType= URLConnection.guessContentTypeFromName(file.getFilename());
        return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(file);
    }

    @PostMapping("/image/{weapon}/{category}/{subject}")
    public void handleFileUpload(@PathVariable String weapon, @PathVariable String category, @PathVariable String subject,@RequestParam("file") MultipartFile file,@RequestParam("token") String token){
        DecodedJWT jwt = null;
        try {
            ECPublicKey publicKey = ECDSA.reconstructPublicKey(publicKeyStr);
            Algorithm algorithm = Algorithm.ECDSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(Constants.IDENTIFIER)
                    .withClaim("role",Constants.ADMIN)
                    .build();
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        }

        String relativePath=(weapon+'/'+category+'/'+subject+'/');
        System.out.println(relativePath);
        storageService.store(file,relativePath);
    }


}
