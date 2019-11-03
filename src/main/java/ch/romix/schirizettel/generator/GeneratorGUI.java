package ch.romix.schirizettel.generator;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Paths;
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
import org.eclipse.birt.core.exception.BirtException;

public class GeneratorGUI {

  private JFrame frame;

  private JLabel title;
  private FileComponents reportComponents;
  private FileComponents dataComponents;
  private FileComponents fileComponents;
  private Generator generator;
  private ITextGenerator iTextGenerator;
  private JButton generateButton;
  private JProgressBar progressBar;
  private ExecutorService executor;

  public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, UnsupportedLookAndFeelException {
    new GeneratorGUI();
  }

  public GeneratorGUI() throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, UnsupportedLookAndFeelException {
    executor = Executors.newFixedThreadPool(1);
    chooseLookAndFeel();
    frame = new JFrame("Schirizettel Generator");
    frame.setSize(800, 600);
    createTitle();
    createReportFile();
    createDataFile();
    createOutputFile();
    createGenerateButton();
    createProgressBar();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    layout();
    frame.setVisible(true);
    asynchronouslyStartGenerator();
  }

  private void layout() {
    GridBagLayout layout = new GridBagLayout();
    Container pane = frame.getContentPane();
    pane.setLayout(layout);

    GridBagConstraints titleGBC = makeGBC(0, 0, GridBagConstraints.BOTH, 3);
    titleGBC.gridwidth = 3;
    titleGBC.insets = new Insets(10,0,10,0);
    pane.add(title, titleGBC);

    pane.add(reportComponents.getLabel(), makeGBC(0, 1, GridBagConstraints.NONE, 0));
    pane.add(reportComponents.getFileText(), makeGBC(1, 1, GridBagConstraints.HORIZONTAL, 1));
    pane.add(reportComponents.getChooseButton(), makeGBC(2, 1, GridBagConstraints.NONE, 0));

    pane.add(dataComponents.getLabel(), makeGBC(0, 2, GridBagConstraints.NONE, 0));
    pane.add(dataComponents.getFileText(), makeGBC(1, 2, GridBagConstraints.HORIZONTAL, 1));
    pane.add(dataComponents.getChooseButton(), makeGBC(2, 2, GridBagConstraints.NONE, 0));

    pane.add(fileComponents.getLabel(), makeGBC(0, 3, GridBagConstraints.NONE, 0));
    pane.add(fileComponents.getFileText(), makeGBC(1, 3, GridBagConstraints.HORIZONTAL, 1));
    pane.add(fileComponents.getChooseButton(), makeGBC(2, 3, GridBagConstraints.NONE, 0));

    GridBagConstraints gbc = makeGBC(0, 4, GridBagConstraints.BOTH, 1);
    gbc.gridwidth = 3;
    pane.add(generateButton, gbc);
    gbc.gridy++;
    pane.add(progressBar, gbc);
  }

  private GridBagConstraints makeGBC(int gridx, int gridy, int fill, double weightx) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.weightx = weightx;
    gbc.fill = fill;
    gbc.insets = new Insets(5,5,5,5);
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

  private void createTitle() {
    title = new JLabel("Schirizettel Generator", JLabel.CENTER);
    Font font = title.getFont();
    title.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 3));
  }

  private void createReportFile() {
    reportComponents = new FileComponents();
    reportComponents.setDialogParent(frame);
    reportComponents.setLabelText("PDF Formular (*.pdf): ");
    reportComponents.setFileFilter(new FileNameExtensionFilter("PDF Formular", "pdf"));
    reportComponents.createComponents();
    reportComponents.setFile(Paths.get("./src/main/resources", "Vorlage.pdf").toAbsolutePath().toFile());
  }

  private void createDataFile() {
    dataComponents = new FileComponents();
    dataComponents.setDialogParent(frame);
    dataComponents.setLabelText("Daten (*.csv): ");
    dataComponents.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
    dataComponents.createComponents();
  }

  private void createOutputFile() {
    fileComponents = new FileComponents();
    fileComponents.setDialogParent(frame);
    fileComponents.setLabelText("Ausgabe (*.pdf): ");
    fileComponents.setFileFilter(new FileNameExtensionFilter("*.pdf", "pdf"));
    fileComponents.createComponents();
  }

  private void createGenerateButton() {
    generateButton = new JButton("Generieren");
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
    final File reportFile = reportComponents.getFile();
    if (!reportFile.exists()) {
      JOptionPane.showMessageDialog(frame, "Bitte wähle eine Report-Datei aus.", frame.getTitle(),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
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
          iTextGenerator.setDataStream(new FileInputStream(dataFile));
          iTextGenerator.setTemplate(reportFile.toURI());
          iTextGenerator.setOutput(new FileOutputStream(outputFile));
          iTextGenerator.runReport();
          setProgressbarFinished();

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

  private Generator getGenerator() throws InterruptedException {
    return generator;
  }

  private void asynchronouslyStartGenerator() {
    progressBar.setStringPainted(true);
    progressBar.setString("Starte Report Generator...");
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          generator = new Generator(progressBar.getModel());
          updateProgressbarInUIThread();
        } catch (BirtException e) {
          e.printStackTrace();
        }
      }

      private void updateProgressbarInUIThread() {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            progressBar.setString("Report Generator bereit.");
          }
        });
      }
    };
    executor.execute(runnable);
  }
}
