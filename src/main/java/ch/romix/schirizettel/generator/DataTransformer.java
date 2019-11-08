package ch.romix.schirizettel.generator;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

public class DataTransformer {

  public static void transformDataToThreeDatasetsARow(File datasource, File destinationFile)
      throws IOException {
    CSVParser parser = CSVFileTester.createSuitableParser(datasource);
    try (FileInputStream fis = new FileInputStream(datasource)) {
      try (InputStreamReader isr = new InputStreamReader(fis)) {
        CSVReader csvReader = new CSVReaderBuilder(isr).withCSVParser(parser).build();
        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
          try (OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            ICSVWriter csvWriter = new CSVWriterBuilder(osw).withSeparator(',').withQuoteChar('"')
                .build();
            List<String[]> lines = csvReader.readAll();
            String[] newHeader = createNewHeader(lines.get(0));
            int origWidth = lines.get(0).length;
            int lineCountWithoutHeader = lines.size() - 1;
            int newLineCount = (int) Math.ceil((double) lineCountWithoutHeader / 3.0);
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
