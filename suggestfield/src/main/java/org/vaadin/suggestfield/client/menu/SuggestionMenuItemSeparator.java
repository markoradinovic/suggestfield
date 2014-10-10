package org.vaadin.suggestfield.client.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A separator that can be placed in a
 * {@link com.google.gwt.user.client.ui.MenuBar}.
 */
public class SuggestionMenuItemSeparator extends UIObject {

	private static final String STYLENAME_DEFAULT = "gwt-MenuItemSeparator";

	private SuggestionMenuBar parentMenu;

	/**
	 * Constructs a new {@link MenuItemSeparator}.
	 */
	@SuppressWarnings("deprecation")
	public SuggestionMenuItemSeparator() {
		setElement(DOM.createTD());
		setStyleName(STYLENAME_DEFAULT);

		// Add an inner element for styling purposes
		Element div = DOM.createDiv();
		DOM.appendChild(getElement(), div);
		setStyleName(div, "menuSeparatorInner");
	}

	/**
	 * Gets the menu that contains this item.
	 * 
	 * @return the parent menu, or <code>null</code> if none exists.
	 */
	public SuggestionMenuBar getParentMenu() {
		return parentMenu;
	}

	void setParentMenu(SuggestionMenuBar parentMenu) {
		this.parentMenu = parentMenu;
	}
}
