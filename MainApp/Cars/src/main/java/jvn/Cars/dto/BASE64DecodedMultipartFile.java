package jvn.Cars.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class BASE64DecodedMultipartFile  implements MultipartFile {
    private final byte[] imgContent;

    private final String originalName;

    public BASE64DecodedMultipartFile(byte[] imgContent,String originalName) {
        this.originalName = originalName;
        this.imgContent = imgContent;
    }

    @Override
    public String getName() {
        return  originalName;
    }

    @Override
    public String getOriginalFilename() {
        return  originalName;
    }

    @Override
    public String getContentType() {
        return "Content";
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize(){ return imgContent.length;}

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        new FileOutputStream(file).write(imgContent);
    }
}
