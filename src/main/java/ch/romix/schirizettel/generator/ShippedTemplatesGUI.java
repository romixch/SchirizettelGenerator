package ch.romix.schirizettel.generator;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ShippedTemplatesGUI extends JPanel {

  private JLabel label;

  ShippedTemplatesGUI() {
    super();
    label = new JLabel();
    label.setHorizontalTextPosition(JLabel.CENTER);
    label.setVerticalTextPosition(JLabel.BOTTOM);
    this.add(label);
  }

  public void loadTemplate(URL url) {
    try {
      try (PDDocument document = PDDocument.load(url.openStream())) {
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage image = renderer.renderImageWithDPI(0, 20);
        ImageIcon imageIcon = new ImageIcon(image);
        label.setIcon(imageIcon);
        this.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
      }
    } catch (IOException e) {
      clearTemplate();
      throw new RuntimeException(e);
    }
  }

  private void clearTemplate() {
    label.setIcon(null);
  }
}
