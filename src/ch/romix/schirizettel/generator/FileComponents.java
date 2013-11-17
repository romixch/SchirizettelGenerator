package ch.romix.schirizettel.generator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class FileComponents {
	private JLabel label;
	private JTextField fileText;
	private JButton chooseButton;
	private Component dialogParent;
	private FileFilter fileFilter;
	private String labelText;

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public void setDialogParent(Component dialogParent) {
		this.dialogParent = dialogParent;
	}

	public void setFile(File file) {
		fileText.setText(file.getAbsolutePath());
	}

	public void createComponents() {
		label = new JLabel(labelText);
		fileText = new JTextField();
		chooseButton = new JButton("Datei suchen...");
		chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
	}

	public JLabel getLabel() {
		return label;
	}

	public JTextField getFileText() {
		return fileText;
	}

	public JButton getChooseButton() {
		return chooseButton;
	}

	public File getFile() {
		return new File(fileText.getText());
	}

	private void chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(fileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		setDirectoryToChooser(fileText.getText(), chooser);
		int returnCode = chooser.showOpenDialog(dialogParent);
		if (returnCode == JFileChooser.APPROVE_OPTION) {
			File reportFile = chooser.getSelectedFile();
			fileText.setText(reportFile.getAbsolutePath());
		}
	}

	private void setDirectoryToChooser(String fileString, JFileChooser chooser) {
		if (!fileString.isEmpty()) {
			File file = new File(fileString);
			chooser.setCurrentDirectory(file);
		} else {
			chooser.setCurrentDirectory(getUserDir());
		}
	}

	private File getUserDir() {
		return Paths.get("").toAbsolutePath().toFile();
	}

}
