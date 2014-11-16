package ch.romix.schirizettel.generator;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
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

  private FileComponents reportComponents;
  private FileComponents dataComponents;
  private FileComponents fileComponents;
  private Generator generator;
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
    frame.setSize(800, 200);
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

    pane.add(reportComponents.getLabel(), makeGBC(0, 0, GridBagConstraints.NONE, 0));
    pane.add(reportComponents.getFileText(), makeGBC(1, 0, GridBagConstraints.HORIZONTAL, 1));
    pane.add(reportComponents.getChooseButton(), makeGBC(2, 0, GridBagConstraints.NONE, 0));

    pane.add(dataComponents.getLabel(), makeGBC(0, 1, GridBagConstraints.NONE, 0));
    pane.add(dataComponents.getFileText(), makeGBC(1, 1, GridBagConstraints.HORIZONTAL, 1));
    pane.add(dataComponents.getChooseButton(), makeGBC(2, 1, GridBagConstraints.NONE, 0));

    pane.add(fileComponents.getLabel(), makeGBC(0, 2, GridBagConstraints.NONE, 0));
    pane.add(fileComponents.getFileText(), makeGBC(1, 2, GridBagConstraints.HORIZONTAL, 1));
    pane.add(fileComponents.getChooseButton(), makeGBC(2, 2, GridBagConstraints.NONE, 0));

    GridBagConstraints gbc = makeGBC(0, 3, GridBagConstraints.BOTH, 1);
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

  private void createReportFile() {
    reportComponents = new FileComponents();
    reportComponents.setDialogParent(frame);
    reportComponents.setLabelText("Report (*.rptdesign): ");
    reportComponents.setFileFilter(new FileNameExtensionFilter("BIRT-Reports", "rptdesign"));
    reportComponents.createComponents();
    reportComponents.setFile(Paths.get("", "schirizettel.rptdesign").toAbsolutePath().toFile());
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
          Generator generator = getGenerator();
          generator.setDataStream(new FileInputStream(dataFile));
          generator.setTemplate(new FileInputStream(reportFile));
          generator.setOutput(new FileOutputStream(outputFile));
          generator.runReport();
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
