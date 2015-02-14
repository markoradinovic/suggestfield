package org.vaadin.suggestfield.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

@Theme("demo")
@Title("SuggestField Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.suggestfield.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}


	@Override
	protected void init(VaadinRequest request) {

		TabSheet tab = new TabSheet();
		tab.setSizeFull();
		setContent(tab);
		
		final SuggestFieldDemo suggestFieldDemo = new SuggestFieldDemo();
		tab.addTab(suggestFieldDemo, "SuggestField demo");
		
		final TokenModeDemo tokenModeDemo = new TokenModeDemo();
		tab.addTab(tokenModeDemo, "TokenMode demo");
		
	}
	

}
