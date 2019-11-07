package ch.romix.schirizettel.generator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TemplatesBox extends JPanel implements ActionListener {


  private TemplateListener templateListener;
  private ButtonGroup templateButtonGroup;
  private URL chosenTemplate;

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
    fireTemplateChanged(standardButton.getActionCommand());
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
    templateListener.onTemplateChanged(chosenTemplate);
  }

  public URL getChosenTemplate() {
    return chosenTemplate;
  }

  public interface TemplateListener {

    void onTemplateChanged(URL url);
  }
}
