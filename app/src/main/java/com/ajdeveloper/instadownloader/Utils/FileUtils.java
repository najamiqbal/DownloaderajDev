package com.ajdeveloper.instadownloader.Utils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {


    public static ArrayList<File> getFilesList(String path) {
        ArrayList<File> files = new ArrayList<>();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            files.addAll(sortFilesByDate(file.listFiles()));
        }
        return files;
    }

    private static List<File> sortFilesByDate(File[] sortedByDate) {
        if (sortedByDate != null && sortedByDate.length > 0) {
            Arrays.sort(sortedByDate, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            return Arrays.asList(sortedByDate);
        }
        return new ArrayList<>();
    }

}
