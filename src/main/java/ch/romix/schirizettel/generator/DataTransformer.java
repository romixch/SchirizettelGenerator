package ch.romix.schirizettel.generator;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class DataTransformer {

    public static void transformDataToThreeDatasetsARow(InputStream dataStream, File destinationFile)
            throws IOException {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(dataStream), ',', '"');
             CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)), ',',
                     '"')) {
            List<String[]> lines = csvReader.readAll();
            String[] newHeader = createNewHeader(lines.get(0));
            int origWidth = lines.get(0).length;
            int lineCountWithoutHeader = lines.size() - 1;
            int newLineCount = lineCountWithoutHeader / 3 + 1;
            addTwoEmptyLines(lines, origWidth);
            csvWriter.writeNext(newHeader);
            for (int line = 0; line < newLineCount; line++) {
                String[] newline = new String[newHeader.length];
                int newColumnIndex = 0;
                for (int origMultiplicator = 0; origMultiplicator < 3; origMultiplicator++) {
                    int oldLineIndex = (line + 1) + origMultiplicator * newLineCount;
                    if (lines.size() - 1 >= oldLineIndex) {
                        for (int oldColumnIndex = 0; oldColumnIndex < origWidth; oldColumnIndex++) {
                            newline[newColumnIndex] = lines.get(oldLineIndex)[oldColumnIndex];
                            newColumnIndex++;
                        }
                    }
                }
                csvWriter.writeNext(newline);
            }
        }
    }

    private static void addTwoEmptyLines(List<String[]> lines, int origWidth) {
        String[] emptyLine = new String[origWidth];
        Arrays.fill(emptyLine, "");
        lines.add(emptyLine);
        lines.add(emptyLine);
    }

    private static String[] createNewHeader(String[] origHeaders) {
        String[] newHeaders = new String[origHeaders.length * 3];
        for (int headerSuffix = 1; headerSuffix <= 3; headerSuffix++) {
            for (int i = 0; i < origHeaders.length; i++) {
                newHeaders[(headerSuffix - 1) * origHeaders.length + i] = origHeaders[i] + headerSuffix;
            }
        }
        return newHeaders;
    }
}
