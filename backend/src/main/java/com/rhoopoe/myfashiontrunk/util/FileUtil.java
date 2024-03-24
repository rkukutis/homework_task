package com.rhoopoe.myfashiontrunk.util;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class FileUtil {

    public static File convertByteArrayToFile(byte[] bytes) {
        try {
            File tempFile = File.createTempFile("uploaded-image", ".tmp");
            OutputStream outputStream = new FileOutputStream(tempFile);
            outputStream.write(bytes);
            outputStream.close();
            return tempFile;
        } catch (IOException exception) {
            throw new RuntimeException("An IO error has occurred while converting byte[] to file");
        }
    }
}
