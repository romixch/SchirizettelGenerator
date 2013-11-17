package ch.romix.schirizettel.generator;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.BoundedRangeModel;
import javax.swing.SwingUtilities;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

public class Generator {

	private static EngineConfig config;
	private InputStream templateStream;
	private OutputStream outputStream;
	private InputStream dataStream;
	private File datasource;
	private BoundedRangeModel progressbarModel;

	public Generator(BoundedRangeModel progressbarModel) throws BirtException {
		this.progressbarModel = progressbarModel;
		setProgressbarMaximum(2);
		config = new EngineConfig();
		setProgressbarValue(1);
		config.setLogConfig("/home/roman/Eclipse/Javagon/birtlog", Level.FINE);
		Platform.startup(config);
		setProgressFinished();
		datasource = new File("DataSource.csv");
	}

	public void setTemplate(InputStream templateStream) {
		this.templateStream = templateStream;
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
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			IReportEngine engine = factory.createReportEngine(config);
			final IReportRunnable design = engine.openReportDesign(templateStream);
			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
			task.setRenderOption(getRenderOptions(outputStream));
			task.setParameterValues(new HashMap<String, Object>());
			if (!task.validateParameters()) {
				throw new IllegalArgumentException("Parameters do not validate");
			}
			setProgressbarValue(2);
			task.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			setProgressFinished();
		}
	}

	private HTMLRenderOption getRenderOptions(OutputStream outs) {
		// set render options including output type
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputStream(outs);
		options.setSupportedImageFormats("PNG");
		options.setEmbeddable(true);
		options.setOutputFormat("pdf");
		return options;
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
