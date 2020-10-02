package ch.romix.schirizettel.generator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;

public class FileComponents {
	private JTextField fileText;
	private JButton chooseButton;
	private Component dialogParent;
	private FileNameExtensionFilter fileFilter;

	public void setFileExtensionFilter(FileNameExtensionFilter extensionFilter) {
		this.fileFilter = extensionFilter;
	}

	public void setDialogParent(Component dialogParent) {
		this.dialogParent = dialogParent;
	}

	public void setFile(File file) {
		fileText.setText(file.getAbsolutePath());
	}

	public void createComponents(String fileChooseText) {
		fileText = new JTextField();
		chooseButton = new JButton(fileChooseText);
		chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
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
			if (!FilenameUtils.getExtension(reportFile.getName()).equalsIgnoreCase(fileFilter.getExtensions()[0])) {
				reportFile = new File(reportFile.getName() + "." + fileFilter.getExtensions()[0]);
			}
			fileText.setText(reportFile.getAbsolutePath());
		}
	}

	private void setDirectoryToChooser(String fileString, JFileChooser chooser) {
		if (!fileString.isEmpty()) {
			File file = new File(fileString);
			chooser.setCurrentDirectory(file);
		} else {
			chooser.setCurrentDirectory(chooser.getFileSystemView().getDefaultDirectory());
		}
	}

	private File getUserDir() {
		return Paths.get("").toAbsolutePath().toFile();
	}

}