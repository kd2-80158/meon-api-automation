package com.api.dataprovider;

import java.io.File;
import org.testng.annotations.DataProvider;

public class OCRFileDataProvider {

    @DataProvider(name = "aadhaarFiles")
    public static Object[][] aadhaarFilesProvider() {

        File folder = new File(System.getProperty("user.dir")
                + "/src/test/resources/testdata/eadhaar");

        File[] files = folder.listFiles(file ->
                file.isFile() &&
                (file.getName().endsWith(".jpg")
              || file.getName().endsWith(".jpeg")
              || file.getName().endsWith(".png")
              || file.getName().endsWith(".pdf"))
        );

        if (files == null || files.length == 0) {
            throw new RuntimeException("No Aadhaar files found in testdata/aadhaar");
        }

        Object[][] data = new Object[files.length][1];
        for (int i = 0; i < files.length; i++) {
            data[i][0] = files[i];
        }
        return data;
    }
}
