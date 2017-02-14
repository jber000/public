package home.importer.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**

 */
@Theme("mytheme")
public class ImporterWeb extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		ImporterMain main = new ImporterMain();
		setContent(main);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = ImporterWeb.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
