package ch.romix.schirizettel.generator;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.junit.Test;

public class GeneratorTest {

  @Test
  public void testGeneration() throws IOException, DocumentException {
    try (OutputStream testOutput = new FileOutputStream("testOutput.pdf")) {

      PdfReader pdfReader = new PdfReader(
          getClass().getClassLoader().getResourceAsStream("Vorlage.pdf"));
      PdfStamper pdfStamper = new PdfStamper(pdfReader, testOutput);
      AcroFields form = pdfStamper.getAcroFields();
      form.setGenerateAppearances(true);
      Map<String, Item> fields = form.getFields();
      for (String item : fields.keySet()) {
        System.out.println(item);
      }
      form.setField("Date1", "10.10.2019");
      form.setField("Time1", "09:13");
      form.setField("TeamA1", "Ruswil 1");
      form.setField("TenuA1", "Rot");
      form.setField("TrousersA1", "Rot");
      form.setField("TeamB1", "Ruswil 2");
      form.setField("TenuB1", "Gelb");
      form.setField("TrousersB1", "Schwarz");

      form.setField("TeamA2", "Neuenkirch");
      form.setField("TeamB2", "Nottwil");
      form.setField("TeamA3", "Willisau");
      form.setField("TeamB3", "Menznau");

      pdfStamper.setFormFlattening(true);
      pdfStamper.close();
      pdfReader.close();

    }
  }

}
