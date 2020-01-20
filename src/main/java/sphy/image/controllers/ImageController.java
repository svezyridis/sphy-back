package sphy.image.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sphy.Validator;
import sphy.image.db.ImageRepository;
import sphy.image.storage.FileSystemStorageService;
import sphy.image.storage.StorageException;
import sphy.image.storage.StorageFileNotFoundException;
import sphy.RestResponse;
import sphy.subject.db.CategoryRepository;
import sphy.subject.db.SubjectRepository;
import sphy.subject.models.Subject;

import java.net.URLConnection;
import java.util.concurrent.TimeUnit;


@RestController
public class ImageController {
    private final FileSystemStorageService storageService;
    Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    Validator validator;

    @Autowired
    public ImageController(FileSystemStorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    @Qualifier("jdbcCategoryRepository")
    private CategoryRepository categoryRepository;

    @Autowired
    @Qualifier("jdbcSubjectRepository")
    private SubjectRepository subjectRepository;

    @Autowired
    @Qualifier("jdbcImageRepository")
    private ImageRepository imageRepository;


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/image/{weapon}/{category}/{subject}/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String weapon, @PathVariable String category, @PathVariable String subject, @PathVariable String filename, @RequestHeader("authorization") String token) {
        logger.info("[ImageController]:[getImage]:{weapon : "+weapon+",category : "+category+",subject : "+subject+ ",filename : "+filename+" }");
        if (!validator.simpleValidateToken(token))
            return new ResponseEntity<String>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        try {
            Resource file = storageService.loadAsResource(weapon + '/' + category + '/' + subject + '/' + filename);
            String mimeType = URLConnection.guessContentTypeFromName(file.getFilename());
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)).contentType(MediaType.valueOf(mimeType)).body(file);
        } catch (StorageException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/image/{weapon}/{category}/{subject}")
    public RestResponse addImage(@PathVariable String weapon, @PathVariable String category, @PathVariable String subject, @RequestParam("isDefault") boolean isDefault,
                                         @RequestParam("file") MultipartFile file, @RequestParam("label") String label, @RequestHeader("authorization") String token) {
        logger.info("[ImageController]:[addImage]:{weapon : "+weapon+",category : "+category+",subject : "+subject+ ", label : "+label+" }");
        if (!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid token");
        Integer weaponID = categoryRepository.getWeaponID(weapon);
        if (weaponID == -1)
            return new RestResponse("error", null, "weapon does not exist");

        Integer categoryID = categoryRepository.getCategoryID(category, weaponID);
        if (categoryID == -1)
            return new RestResponse("error", null, "category does not exist");

        Integer subjectID = subjectRepository.getSubjectID(subject);
        if (subjectID == -1)
            return new RestResponse("error", null, "subject does not exist");
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String relativePath=(weapon+'/'+category+'/'+subject+'/');
        try {
            storageService.store(file, relativePath);
        } catch (StorageException e) {
            return new RestResponse("error", null, "could not save image");
        }
        //TODO check same image filename
        Integer result = imageRepository.addImage(filename, subjectID, label);
        if (result == -1)
            return new RestResponse("error", null, "image was not be inserted into database");
        if(isDefault){
        }

        return new RestResponse("success", null, "image successfully saved");
    }

    @DeleteMapping(value = "/image/{weapon}/{category}/{subject}/{filename}")
    public RestResponse deleteImage(@PathVariable String weapon, @PathVariable String category, @PathVariable String subject, @PathVariable String filename, @RequestHeader("authorization") String token){
        logger.info("[ImageController]:[addImage]:{weapon : "+weapon+",category : "+category+",subject : "+subject+ ",filename : "+filename+" }");
        if (!validator.validateAdminToken(token))
            return new RestResponse("error", null, "invalid token");
        Integer weaponID = categoryRepository.getWeaponID(weapon);
        if (weaponID == -1)
            return new RestResponse("error", null, "weapon does not exist");

        Integer categoryID = categoryRepository.getCategoryID(category, weaponID);
        if (categoryID == -1)
            return new RestResponse("error", null, "category does not exist");

        Integer subjectID = subjectRepository.getSubjectID(subject);
        if (subjectID == -1)
            return new RestResponse("error", null, "subject does not exist");
        Integer imageID=imageRepository.getImageByFileName(filename,subjectID);
        Integer res=imageRepository.deleteImage(imageID);
        if(res==-1)
            return new RestResponse("error", null, "image could not be deleted from database");
        if(!storageService.delete(weapon+'/'+category+'/'+subject+'/'+filename))
            return new RestResponse("error", null, "image could not be deleted from storage");
        return new RestResponse("success",null,"image deleted successfully");
    }

}
