package com.bazarnazar.cassandramapings.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created by Bazar on 27.05.16.
 */
public final class DataImportUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataImportUtil.class);

    private DataImportUtil() {
    }


    public static boolean isFileExists(String tableName) {
        File file = new File(tableName + ".csv");
        return file.exists();
    }

    public static String getTruncate(String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TRUNCATE ").append(tableName);
        return stringBuilder.toString();
    }

    public static Stream<String> getInserts(String tableName) {
        try {
            Scanner scanner = new Scanner(new FileInputStream(tableName + ".csv"));
            StringBuilder stringBuilder = new StringBuilder();
            String prefix = stringBuilder.append("INSERT INTO ").append(tableName).append("(")
                                         .append(scanner.nextLine()).append(") VALUES(").toString();
            List<String> rows = new ArrayList<>();
            String row;
            while (scanner.hasNext() && !"".equals(row = scanner.nextLine().trim())) {
                rows.add(prefix + row + ");");
            }
            return rows.stream();
        } catch (FileNotFoundException e) {
            LOGGER.info("No data file for " + tableName);
            return Stream.empty();
        }
    }

}
