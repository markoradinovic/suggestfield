package org.vaadin.suggestfield.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.suggestfield.StringSuggestionConverter;
import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.NewItemsHandler;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;
import org.vaadin.suggestfield.SuggestField.TokenHandler;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class AddressEditor extends CssLayout implements NewItemsHandler,
		SuggestionHandler, LayoutClickListener, TokenHandler {

	private SuggestField suggestField;

	private List<String> addresses = new LinkedList<String>();

	private EmailValidator validator;

	public AddressEditor() {
		super();
		setWidth("100%");
		addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		addStyleName("address-editor");

		suggestField = new SuggestField();
		suggestField.setNewItemsAllowed(true);
		suggestField.setNewItemsHandler(this);
		suggestField.setImmediate(true);
		suggestField.setTokenMode(true);
		suggestField.setSuggestionHandler(this);
		suggestField.setSuggestionConverter(new StringSuggestionConverter());
		suggestField.setTokenHandler(this);
		suggestField.setWidth("250px");
		suggestField.setPopupWidth(400);
		addComponent(suggestField);

		addLayoutClickListener(this);

		validator = new EmailValidator("Invalid email address");
	}
	

	@Override
	public void layoutClick(LayoutClickEvent event) {
		if (event.getClickedComponent() == null) {
			suggestField.focus();
		}

	}

	@Override
	public Object addNewItem(String newItemText) {
		if (validator.isValid(newItemText)) {
			addresses.add(newItemText);

		}
		return newItemText;
	}

	@Override
	public List<Object> searchItems(String query) {

		if ("".equals(query) || query == null) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<String>();
		
		int count = 0;
		for (String address : addresses) {
			if (address.toLowerCase().startsWith(query.toLowerCase()) && count < 10) {
				result.add(address);
				count++;
			}
		}
		System.out.println("Total: " + result.size());

		return new ArrayList<Object>(result);
	}


	private ClickListener addressRemoveClick = new ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {
			AddressEditor.this.removeComponent(event.getButton());
			event.getButton().removeClickListener(addressRemoveClick);
		}
	};

	@Override
	public void handleToken(Object token) {
		if (token != null) {
			final String address = (String) token;
			// Skip duplicates 
			if (!getValue().contains(address)) {
				addToken(generateToken(address));
			}
		}
	}

	private void clearAddresses() {
		while (getComponentCount() > 1) {
			if (getComponent(0) instanceof Button) {
				final Button btn = (Button) getComponent(0);
				btn.removeClickListener(addressRemoveClick);
				removeComponent(btn);
			}
		}
	}

	public boolean isValid(boolean required) {
		boolean valid = true;
		int count = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof Button) {
				count++;
				final Button btn = (Button) getComponent(i);
				final String address = (String) btn.getData();
				if (!validator.isValid(address)) {
					valid = false;
					break;
				}
			}
		}
		if (required && count == 0) {
			valid = false;
		}
		return valid;
	}

	public List<String> getValue() {
		List<String> values = new LinkedList<String>();
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof Button) {
				final Button btn = (Button) getComponent(i);
				values.add((String) btn.getData());
			}
		}
		return values;
	}

	public void setValue(List<String> value) {
		clearAddresses();
		if (value != null) {
			for (String address : value) {
				addToken(generateToken(address));
			}
		}
	}

	private void addToken(Button button) {
		int index = getComponentIndex(suggestField);
		addComponent(button, index);
	}

	private Button generateToken(String address) {
		final Button btn = new Button(address, FontAwesome.TIMES);
		btn.setData(address);
		btn.addStyleName(ValoTheme.BUTTON_SMALL);
		btn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		btn.addClickListener(addressRemoveClick);

		if (validator.isValid(address)) {
			btn.setDescription("Click to remove");
		} else {
			btn.addStyleName(ValoTheme.BUTTON_DANGER);
			btn.setDescription(validator.getErrorMessage());
		}
		return btn;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		suggestField.setEnabled(enabled);
	}
	
	public void setAddresses(List<String> addresses) {
		this.addresses.clear();
		this.addresses.addAll(addresses);
	}

}