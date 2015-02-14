package org.vaadin.suggestfield.demo;

import java.util.LinkedList;
import java.util.List;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class TokenModeDemo extends VerticalLayout {
	
	private FormLayout form;
	
	private AddressEditor to;
	private AddressEditor cc;
	
	public TokenModeDemo() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		
		final Label caption = new Label("TokenMode Demo");
		addComponent(caption);
		
		final CssLayout wrap = new CssLayout();
		wrap.setWidth("600px");
		wrap.addStyleName(ValoTheme.LAYOUT_CARD);
		addComponent(wrap);
		setExpandRatio(wrap, 1);
		
		form = new FormLayout();
		form.setMargin(true);
		form.setSpacing(true);
		form.setWidth("100%");
		form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		wrap.addComponent(form);
		
		to = new AddressEditor();
		to.setAddresses(buildDemoAddresses());
		to.setCaption("To");
		to.setWidth("100%");
		form.addComponent(to);
		
		cc = new AddressEditor();
		cc.setAddresses(buildDemoAddresses());
		cc.setCaption("Cc");
		cc.setWidth("100%");
		form.addComponent(cc);
		
		TextField subject = new TextField();
		subject.setWidth("100%");
		form.addComponent(subject);
		
		RichTextArea body = new RichTextArea("Body");
		body.setWidth("100%");
		wrap.addComponent(body);
	}
	
	
	public List<String> buildDemoAddresses() {
		/* Create dummy data by randomly combining first and last names */
		String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
				"Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
				"Lisa", "Marge" };
		String[] lnames = { "Smith", "Gordon", "Simpson", "Brown", "Clavel",
				"Simons", "Verne", "Scott", "Allison", "Gates", "Rowling",
				"Barks", "Ross", "Schneider", "Tate" };
		
		List<String> result = new LinkedList<String>();
		for (int i = 0; i < 1000; i++) {
			String fname = fnames[(int) (fnames.length * Math.random())].toLowerCase();
			String lname = lnames[(int) (lnames.length * Math.random())].toLowerCase(); 
			String address = fname + "." + lname + "@demo.com";
			result.add(address);
		}

		return result;
	}

}
