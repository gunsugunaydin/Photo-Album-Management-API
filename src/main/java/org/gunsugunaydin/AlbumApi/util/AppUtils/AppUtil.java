package org.gunsugunaydin.AlbumApi.util.AppUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import java.awt.image.BufferedImage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

public class AppUtil {

    public static String PATH = "src\\main\\resources\\static\\uploads\\";

    public static String get_photo_upload_path(String fileName, String folder_name, Long album_id) throws IOException {
        String path = PATH + album_id + "\\" + folder_name;
        //bu satır, pathdeki tüm dizinleri yaratır, dizinler mevcutsa bir işlem yapmaz.
        Files.createDirectories(Paths.get(path));
        //dosyanın kaydedileceği tam yolu geri döner. Eğer fileName'i path'e ekleseydik, 
        //bu metod bir dizin yerine tam dosya yolunu yaratmaya çalışırdı, bu da hatalara yol açabilirdi
        //çünkü Files.createDirectories dosyalar değil, sadece dizinler yaratır.
        return new File(path).getAbsolutePath() + "\\" + fileName;
    }
    
    public static BufferedImage getThumbnail(MultipartFile orginalFile, Integer width) throws IOException {  
        BufferedImage thumbImg = null;  
        BufferedImage img = ImageIO.read(orginalFile.getInputStream());  
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);   
        return thumbImg;  
    }

    public static boolean delete_photo_from_path(String fileName, String folder_name, Long album_id) {
        try {
            File f = new File(PATH + album_id + "\\" + folder_name + "\\"+fileName); //file to be delete
            if (f.delete())//inbuilt method for object file
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    } 

    //UrlResource, Spring framework'de dosya veya diğer kaynakları URL'ler üzerinden temsil eden bir sınıftır.
    //kullanıcıya bir dosya indirme özelliği sunmak istiyorsak, bu dosyayı bir UrlResource olarak döndürür, kullanıcıya dosyanın URL'sini sağlayabiliriz.
    public static Resource getFileAsResource(Long album_id, String folder_name, String file_name) throws IOException {
        String location = PATH + album_id + "\\" + folder_name + "\\" + file_name;
        File file = new File(location);
        if (file.exists()){
            Path path = Paths.get(file.getAbsolutePath());
            return new UrlResource(path.toUri());
        }else{
            return null;
        }       
    }

    public static boolean delete_album_directory(Long album_id) throws IOException {
        try {
            File albumDirectory = new File(PATH + album_id);
            return deleteDirectoryRecursively(albumDirectory);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean deleteDirectoryRecursively(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectoryRecursively(file)) {
                        return false;
                    }
                }
            }
        }
        return  directory.delete();
    }
}

