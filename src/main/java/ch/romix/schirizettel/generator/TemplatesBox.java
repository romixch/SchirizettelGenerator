package ch.romix.schirizettel.generator;

import com.itextpdf.text.pdf.PdfReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TemplatesBox extends JPanel implements ActionListener {


  private TemplateListener templateListener;
  private ButtonGroup templateButtonGroup;
  private URL chosenTemplate;
  private JLabel templateDescription;

  public TemplatesBox(TemplateListener templateListener) {
    super();
    this.templateListener = templateListener;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    templateButtonGroup = new ButtonGroup();

    JRadioButton standardButton = new JRadioButton(
        "Standard Vorlage (Korbball Hallenmeisterschaft Zentralschweiz)", true);
    templateButtonGroup.add(standardButton);
    standardButton.setActionCommand("Vorlage.pdf");
    standardButton.addActionListener(this);
    add(standardButton);

    JRadioButton simpleButton = new JRadioButton("Simple Vorlage", false);
    templateButtonGroup.add(simpleButton);
    simpleButton.setActionCommand("VorlageSimpel.pdf");
    simpleButton.addActionListener(this);
    add(simpleButton);

    JButton ownButton = new JButton("Eigene Vorlage wählen");
    templateButtonGroup.add(ownButton);
    ownButton.setActionCommand("");
    ownButton.addActionListener(this);
    add(ownButton);

    templateDescription = new JLabel();
    add(templateDescription);

    fireTemplateChanged(standardButton.getActionCommand());
  }

  private void updateTemplateFields(Set<String> fields) {
    templateDescription.setText(
        "<html><br/>Folgende Felder kannst du mit der CSV-Datei befüllen:<br/><pre>" + String
            .join(", ", fields) + "</pre></html>");
  }

  private URL chooseFile() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("*.pdf", "pdf"));
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setCurrentDirectory(getUserDir());
    int returnCode = chooser.showOpenDialog(this);
    if (returnCode == JFileChooser.APPROVE_OPTION) {
      File reportFile = chooser.getSelectedFile();
      try {
        chosenTemplate = reportFile.toURI().toURL();
        templateButtonGroup.clearSelection();
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
    return chosenTemplate;
  }

  private File getUserDir() {
    return Paths.get("").toAbsolutePath().toFile();
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    String resourceName = actionEvent.getActionCommand();
    fireTemplateChanged(resourceName);
  }

  private void fireTemplateChanged(String resourceName) {
    if (resourceName == null || resourceName.isEmpty()) {
      chosenTemplate = chooseFile();
    } else {
      chosenTemplate = this.getClass().getClassLoader().getResource(resourceName);
    }
    updateTemplateFields(readTemplateFields(chosenTemplate));
    templateListener.onTemplateChanged(chosenTemplate);
  }

  public URL getChosenTemplate() {
    return chosenTemplate;
  }

  private Set<String> readTemplateFields(URL url) {
    try {
      System.out.println("packr lets the app crash at the following line");
      PdfReader pdfReader = new PdfReader(url.openStream());
      Set<String> allFields = pdfReader.getAcroFields().getFields().keySet();
      return allFields.stream().map(this::stripNumbers).collect(Collectors.toSet());
    } catch (IOException e) {
      e.printStackTrace();
      return Collections.emptySet();
    }
  }

  private String stripNumbers(String s) {
    while (s.charAt(s.length() - 1) >= '0' && s.charAt(s.length() - 1) <= '9') {
      s = s.substring(0, s.length() - 1);
    }
    return s;
  }

  public interface TemplateListener {

    void onTemplateChanged(URL url);
  }
}
