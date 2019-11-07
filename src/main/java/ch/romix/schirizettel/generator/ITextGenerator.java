package ch.romix.schirizettel.generator;

import au.com.bytecode.opencsv.CSVReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoundedRangeModel;
import javax.swing.SwingUtilities;

public class ITextGenerator {

  private URL templateURL;
  private OutputStream outputStream;
  private InputStream dataStream;
  private File datasource;
  private BoundedRangeModel progressbarModel;

  public ITextGenerator(BoundedRangeModel progressbarModel) {
    this.progressbarModel = progressbarModel;
    datasource = new File(System.getProperty("user.dir"), "DataSource.csv");
  }

  public void setTemplate(URL templateURL) {
    this.templateURL = templateURL;
  }

  public void setDataStream(InputStream dataStream) {
    this.dataStream = dataStream;
  }

  public void setOutput(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public void runReport() {
    try {
      setProgressbarMaximum(3);
      setProgressbarValue(1);
      DataTransformer.transformDataToThreeDatasetsARow(dataStream, datasource);
      generateReport();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      setProgressFinished();
    }
  }

  private void generateReport() throws IOException, DocumentException {
    CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(datasource)),
        ',', '"');
    List<File> pages = new ArrayList<>();
    List<String[]> lines = csvReader.readAll();
    String[] header = lines.get(0);

    for (int i = 1; i < lines.size(); i++) {
      String[] line = lines.get(i);

      File tempPdf = File.createTempFile("temp_", ".pdf", new File("./"));
      pages.add(tempPdf);
      tempPdf.deleteOnExit();
      try (OutputStream outputStream = new FileOutputStream(tempPdf)) {

        PdfReader pdfReader = new PdfReader(templateURL.openStream());
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        AcroFields form = pdfStamper.getAcroFields();
        form.setGenerateAppearances(true);

        for (int fieldId = 0; fieldId < header.length; fieldId++) {
          form.setField(header[fieldId], line[fieldId]);
        }

        pdfStamper.setFormFlattening(true);
        pdfStamper.close();
        pdfReader.close();
      }
    }

    mergePages(pages);
  }

  private void mergePages(List<File> pages) throws DocumentException, IOException {
    PdfReader pdfReader = new PdfReader(templateURL.openStream());
    Rectangle originalPageSize = pdfReader.getPageSize(1);
    pdfReader.close();

    Document document = new Document();
    document.setPageSize(originalPageSize);
    PdfWriter writer = PdfWriter.getInstance(document, outputStream);
    document.open();
    PdfContentByte cb = writer.getDirectContent();

    for (File pageFile : pages) {
      InputStream in = new FileInputStream(pageFile);
      PdfReader reader = new PdfReader(in);
      for (int i = 1; i <= reader.getNumberOfPages(); i++) {
        document.newPage();
        //import the page from source pdf
        PdfImportedPage page = writer.getImportedPage(reader, i);
        //add the page to the destination pdf
        cb.addTemplate(page, 0, 0);
      }
    }

    outputStream.flush();
    document.close();
    outputStream.close();
  }

  private void setProgressbarMaximum(final int val) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        progressbarModel.setMinimum(0);
        progressbarModel.setMaximum(val);
      }
    });
  }

  private void setProgressbarValue(final int val) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        progressbarModel.setValue(val);
      }
    });
  }

  private void setProgressFinished() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        progressbarModel.setValue(progressbarModel.getMaximum());
      }
    });
  }
}
