package org.vaadin.suggestfield.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.suggestfield.StringSuggestionConverter;
import org.vaadin.suggestfield.SuggestField;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class AddressEditor extends CssLayout  {

	private SuggestField<String> suggestField;

	private List<String> addresses = new LinkedList<String>();

	private EmailValidator validator;

	private Registration addressRemoveRegistration;

	public AddressEditor() {
		super();
		setWidth("100%");
		addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		addStyleName("address-editor");

		suggestField = SuggestField.forString();
		suggestField.setNewItemsAllowed(true);
		suggestField.setNewItemsHandler(newItemText->{
			if (!validator.apply(newItemText, null).isError()) {
				addresses.add(newItemText);
			}
			return newItemText;
		});
		suggestField.setTokenMode(true);
		suggestField.setSuggestionHandler(this::searchItems);
		suggestField.setTokenHandler(token->{
			if (token != null) {
				final String address = (String) token;
				// Skip duplicates 
				if (!getValue().contains(address)) {
					addToken(generateToken(address));
				}
			}
		});
		suggestField.setWidth("250px");
		suggestField.setPopupWidth(400);
		addComponent(suggestField);

		addLayoutClickListener(event->{
			if (event.getClickedComponent() == null) {
				suggestField.focus();
			}
		});

		validator = new EmailValidator("Invalid email address");
	}

	private List<String> searchItems(String query) {

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

		return new ArrayList<>(result);
	}

	private void clearAddresses() {
		while (getComponentCount() > 1) {
			if (getComponent(0) instanceof Button) {
				final Button btn = (Button) getComponent(0);
				addressRemoveRegistration.remove();
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
				if (validator.apply(address, null).isError()) {
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
		final Button btn = new Button(address, VaadinIcons.CLOSE);
		btn.setData(address);
		btn.addStyleName(ValoTheme.BUTTON_SMALL);
		btn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		addressRemoveRegistration = btn.addClickListener(event -> {
			AddressEditor.this.removeComponent(event.getButton());

		});

		ValidationResult validationResult = validator.apply(address, null);

		if (!validationResult.isError()) {
			btn.setDescription("Click to remove");
		} else {
			btn.addStyleName(ValoTheme.BUTTON_DANGER);
			btn.setDescription(validationResult.getErrorMessage());
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