package sphy.image.db;

public interface ImageRepository {
    Integer addImage(String URL,Integer subjectID,String label);
    Integer deleteImage(Integer imageID);
    Integer getImageByFileName(String filename,Integer subjectID);
}
