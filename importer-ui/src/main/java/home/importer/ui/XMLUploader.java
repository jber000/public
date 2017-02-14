package home.importer.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.StreamVariable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class XMLUploader extends VerticalLayout {

	private ProgressBar progress;
	private Html5File html5File;
	private static final String defaultMsg = "<i>Drop file here</i>";
	private Label label = new Label(defaultMsg, ContentMode.HTML);
	private StreamSource ss;

	public XMLUploader() {
		VerticalLayout dropPane = new VerticalLayout();
		dropPane.setWidth(100.0f, Unit.PERCENTAGE);
		dropPane.addComponent(label);

		XMLDropBox dropBox = new XMLDropBox(dropPane);

		Panel panel = new Panel(dropBox);
		addComponent(panel);

		progress = new ProgressBar();
		progress.setIndeterminate(true);
		progress.setVisible(false);
		addComponent(progress);
	}

	@Override
	public void attach() {
		super.attach();
	}

	private class XMLDropBox extends DragAndDropWrapper implements DropHandler {

		public XMLDropBox(Component root) {
			super(root);
			setDropHandler(this);
		}

		public void drop(DragAndDropEvent dropEvent) {
			WrapperTransferable tr = (WrapperTransferable) dropEvent.getTransferable();
			Html5File[] files = tr.getFiles();
			if (files != null) {
				if (files.length > 1) {
					Notification.show("Multiple selection not allowed", "Only one file is allowed",
							Notification.TYPE_ERROR_MESSAGE);
				} else {
					html5File = files[0];

					final ByteArrayOutputStream bas = new ByteArrayOutputStream();
					StreamVariable streamVariable = new StreamVariable() {

						public OutputStream getOutputStream() {
							return bas;
						}

						public boolean listenProgress() {
							return false;
						}

						public void onProgress(StreamingProgressEvent event) {
						}

						public void streamingStarted(StreamingStartEvent event) {
							progress.setVisible(true);
						}

						public void streamingFinished(StreamingEndEvent event) {
							progress.setVisible(false);
							processFile(bas);
							label.setValue(html5File.getFileName());
						}

						public void streamingFailed(StreamingErrorEvent event) {
							progress.setVisible(false);
						}

						public boolean isInterrupted() {
							return false;
						}
					};
					html5File.setStreamVariable(streamVariable);
				}
			}
		}

		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
	}

	private void processFile(final ByteArrayOutputStream bas) {
		ss = new StreamSource() {
			public InputStream getStream() {
				if (bas != null) {
					byte[] byteArray = bas.toByteArray();
					return new ByteArrayInputStream(byteArray);
				}
				return null;
			}
		};
		//StringWriter writer = new StringWriter();
		//IOUtils.copy(streamSource.getStream(), writer, "UTF-8");
		//String theString = writer.toString();
		//System.out.println(theString);
	}

	public StreamSource getStreamSource() {
		return ss;
	}
}
