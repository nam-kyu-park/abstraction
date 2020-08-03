package kr.co.sptek.abstraction.util;

import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class PathUtil {

    public static String join(String... paths){
        if (paths.length < 1)
            return paths.toString();

        StringBuilder root = new StringBuilder();
        for (String file : paths){
            root.append(file);
            root.append("/");
        }

        String path = Paths.get(root.toString()).toString();
        return FilenameUtils.separatorsToUnix(path);
    }

    public static boolean mkdirs(String path) {
        File directory = new File(path);
        if(!directory.exists())
            return directory.mkdirs();
        return true;
    }
}
