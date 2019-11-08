package ch.romix.schirizettel.generator;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CSVFileTester {

  public static CSVParser createSuitableParser(File datasource) {
    CSVParser parserComma = new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build();
    CSVParser parserSemi = new CSVParserBuilder().withSeparator(';').withQuoteChar('"').build();
    int columnsWithComa = columnCount(datasource, parserComma);
    int columnsWithSemi = columnCount(datasource, parserSemi);
    if (columnsWithComa > columnsWithSemi) {
      return parserComma;
    } else {
      return parserSemi;
    }
  }

  private static int columnCount(File datasource, CSVParser parser) {
    try (FileInputStream fis = new FileInputStream(datasource)) {
      try (InputStreamReader isr = new InputStreamReader(fis)) {
        CSVReader csvReader = new CSVReaderBuilder(isr).withCSVParser(parser).build();
        List<String[]> all = csvReader.readAll();
        if (all.isEmpty()) {
          return 0;
        } else {
          return all.get(0).length;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }
}
