package ch.romix.schirizettel.generator;

import ch.romix.schirizettel.generator.TemplatesBox.TemplateListener;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GeneratorGUI {

  private JFrame frame;

  TemplatesBox templatesBox;
  private FileComponents dataComponents;
  private FileComponents fileComponents;
  private ITextGenerator iTextGenerator;
  private JButton generateButton;
  private JProgressBar progressBar;
  private ExecutorService executor;

  public GeneratorGUI() throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, UnsupportedLookAndFeelException {
    executor = Executors.newFixedThreadPool(1);
    chooseLookAndFeel();
    frame = new JFrame("Schirizettel Generator");
    createTitle();
    createDataFile();
    createOutputFile();
    createGenerateButton();
    createProgressBar();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    layout();
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, UnsupportedLookAndFeelException {
    new GeneratorGUI();
  }

  private void layout() {
    GridBagLayout layout = new GridBagLayout();
    Container pane = frame.getContentPane();
    pane.setLayout(layout);
    int gridy = 0;

    GridBagConstraints titleGBC = makeGBC(0, gridy, GridBagConstraints.BOTH, 1);
    titleGBC.gridwidth = 3;
    titleGBC.insets = new Insets(10, 0, 10, 0);
    pane.add(createTitle(), titleGBC);

    gridy++;
    JLabel templateHeader = createHeading("1. Wähle deine Vorlage:");
    pane.add(templateHeader, makeHeadingGBC(0, gridy));

    gridy++;
    ShippedTemplatesGUI shippedTemplatesGUI = new ShippedTemplatesGUI();
    templatesBox = new TemplatesBox(new TemplateListener() {
      @Override
      public void onTemplateChanged(URL url) {
        shippedTemplatesGUI.loadTemplate(url);
      }
    });
    pane.add(templatesBox, makeGBC(0, gridy, GridBagConstraints.HORIZONTAL, 1));
    pane.add(shippedTemplatesGUI, makeGBC(1, gridy, GridBagConstraints.BOTH, 1));

    gridy++;
    JLabel datasourceHeader = createHeading("2. Bestimme die Datenquelle (csv-Datei):");
    pane.add(datasourceHeader, makeHeadingGBC(0, gridy));

    gridy++;
    pane.add(dataComponents.getFileText(), makeGBC(0, gridy, GridBagConstraints.HORIZONTAL, 1));
    pane.add(dataComponents.getChooseButton(), makeGBC(1, gridy, GridBagConstraints.NONE, 0));

    gridy++;
    JLabel outputHeader = createHeading("3. Wähle, wohin die PDF-Datei geschrieben wird:");
    pane.add(outputHeader, makeHeadingGBC(0, gridy));

    gridy++;
    pane.add(fileComponents.getFileText(), makeGBC(0, gridy, GridBagConstraints.HORIZONTAL, 1));
    pane.add(fileComponents.getChooseButton(), makeGBC(1, gridy, GridBagConstraints.NONE, 0));

    gridy++;
    JLabel runHeader = createHeading("4. Starte den Generator:");
    pane.add(runHeader, makeHeadingGBC(0, gridy));

    gridy++;
    GridBagConstraints gbc = makeGBC(0, gridy, GridBagConstraints.BOTH, 1);
    gbc.gridwidth = 3;
    pane.add(generateButton, gbc);
    gbc.gridy++;
    pane.add(progressBar, gbc);
  }

  private JLabel createHeading(String text) {
    JLabel heading = new JLabel(text);
    Font font = heading.getFont();
    heading.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 2));
    return heading;
  }

  private GridBagConstraints makeGBC(int gridx, int gridy, int fill, double weightx) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.weightx = weightx;
    gbc.fill = fill;
    gbc.insets = new Insets(5, 5, 5, 5);
    return gbc;
  }

  private GridBagConstraints makeHeadingGBC(int gridx, int gridy) {
    GridBagConstraints gbc = makeGBC(gridx, gridy, GridBagConstraints.BOTH, 1);
    gbc.gridwidth = 3;
    gbc.insets = new Insets(10,5,5,5);
    return gbc;
  }

  private void chooseLookAndFeel() throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, UnsupportedLookAndFeelException {
    LookAndFeelInfo gtk = null;
    LookAndFeelInfo windows = null;
    LookAndFeelInfo nimbus = null;
    LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
    for (LookAndFeelInfo lookAndFeelInfo : installedLookAndFeels) {
      String name = lookAndFeelInfo.getName();
      switch (name) {
        case "GTK+":
          gtk = lookAndFeelInfo;
          break;
        case "Windows":
          windows = lookAndFeelInfo;
          break;
        case "Nimbus":
          nimbus = lookAndFeelInfo;
          break;
      }
    }
    if (gtk != null) {
      UIManager.setLookAndFeel(gtk.getClassName());
    } else if (windows != null) {
      UIManager.setLookAndFeel(windows.getClassName());
    } else if (nimbus != null) {
      UIManager.setLookAndFeel(nimbus.getClassName());
    }
  }

  private JLabel createTitle() {
    JLabel title = new JLabel("Schirizettel Generator", JLabel.CENTER);
    Font font = title.getFont();
    title.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 3));
    return title;
  }

  private void createDataFile() {
    dataComponents = new FileComponents();
    dataComponents.setDialogParent(frame);
    dataComponents.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
    dataComponents.createComponents();
  }

  private void createOutputFile() {
    fileComponents = new FileComponents();
    fileComponents.setDialogParent(frame);
    fileComponents.setFileFilter(new FileNameExtensionFilter("*.pdf", "pdf"));
    fileComponents.createComponents();
  }

  private void createGenerateButton() {
    generateButton = new JButton("Starten");
    generateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generateReport();
      }
    });
  }

  private void createProgressBar() {
    progressBar = new JProgressBar(new DefaultBoundedRangeModel());
  }

  protected void generateReport() {
    progressBar.getModel();
    final URL reportURL = templatesBox.getChosenTemplate();
    final File dataFile = dataComponents.getFile();
    if (!dataFile.exists()) {
      JOptionPane.showMessageDialog(frame, "Bitte wähle deine Daten-Datei aus.", frame.getTitle(),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (fileComponents.getFileText().getText().isEmpty()) {
      JOptionPane.showMessageDialog(frame, "Bitte Ausgabe-Datei wählen.", frame.getTitle(),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final File outputFile = fileComponents.getFile();
    if (outputFile.exists()) {
      int confirm =
          JOptionPane.showConfirmDialog(frame, "Ausgabe-Datei existiert bereits. Überschreiben?",
              frame.getTitle(), JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) {
        return;
      }
    }
    progressBar.setString("Zettel generieren...");
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          ITextGenerator iTextGenerator = getITextGenerator();
          iTextGenerator.setDataSource(dataFile);
          iTextGenerator.setTemplate(reportURL);
          iTextGenerator.setOutput(new FileOutputStream(outputFile));
          iTextGenerator.runReport();
          setProgressbarFinished();
          GenerationHint generationHint = iTextGenerator.analyzeGeneration();
          if (!generationHint.perfect) {
            String message = "Beim Generieren ist was aufgefallen:\n" + generationHint.getHints();
            JOptionPane.showMessageDialog(frame, message, "Hinweise", JOptionPane.WARNING_MESSAGE);
          }
          Desktop.getDesktop().open(outputFile);

        } catch (Exception e) {
          setProgressbarError(e);
        }
      }

    };
    executor.execute(runnable);
  }

  protected void setProgressbarFinished() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        progressBar.setString("Zettel wurden erfolgreich gespeichert.");
      }
    });
  }

  private void setProgressbarError(final Exception e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        progressBar.setString("Fehler beim Generieren!");
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        JOptionPane.showMessageDialog(frame, "Konnte Generator nicht starten.\n" //
            + e.getLocalizedMessage() + "\n"//
            + writer.toString(), frame.getTitle(), JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private ITextGenerator getITextGenerator() {
    if (iTextGenerator == null) {
      iTextGenerator = new ITextGenerator(progressBar.getModel());
    }
    return iTextGenerator;
  }
}
