package jvn.Cars.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BASE64DecodedMultipartFile  implements MultipartFile {
    private final byte[] imgContent;

    public BASE64DecodedMultipartFile(byte[] imgContent) {
        this.imgContent = imgContent;
    }

    @Override
    public String getName() {
        return this.getName();
    }

    @Override
    public String getOriginalFilename() {
        return this.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        return this.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty();
    }

    @Override
    public long getSize() {
        return this.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.getInputStream();
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        new FileOutputStream(file).write(imgContent);
    }
}
