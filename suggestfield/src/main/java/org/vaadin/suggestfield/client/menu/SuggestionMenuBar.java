package org.vaadin.suggestfield.client.menu;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;

import java.util.ArrayList;
import java.util.List;

public class SuggestionMenuBar extends Widget {

	private static final String STYLENAME_DEFAULT = "gwt-MenuBar";

	/**
	 * List of all {@link SuggestionSuggestionMenuItem}s and
	 * {@link SuggestionSuggestionMenuItemSeparator}s.
	 */
	private ArrayList<UIObject> allItems = new ArrayList<UIObject>();

	/**
	 * List of {@link SuggestionMenuItem}s, not including
	 * {@link SuggestionSuggestionMenuItemSeparator}s.
	 */
	private ArrayList<SuggestionMenuItem> items = new ArrayList<SuggestionMenuItem>();

	private Element body;
	private SuggestionMenuItem selectedItem;
	private boolean focusOnHover = true;

	/**
	 * Creates an empty horizontal menu bar.
	 */
	public SuggestionMenuBar() {
		init();
		setStyleName("");
		setStylePrimaryName("v-filterselect-suggestmenu");
		setFocusOnHoverEnabled(false);
	}

	public int getNumItems() {
		return getItems().size();
	}

	/**
	 * Returns the index of the menu item that is currently selected.
	 *
	 * @return returns the selected item
	 */
	public int getSelectedItemIndex() {
		SuggestionMenuItem selectedItem = getSelectedItem();
		if (selectedItem != null) {
			return getItems().indexOf(selectedItem);
		}
		return -1;
	}

	/**
	 * Selects the item at the specified index in the menu. Selecting the item
	 * does not perform the item's associated action; it only changes the style
	 * of the item and updates the value of SuggestionMenu.selectedItem.
	 *
	 * @param index
	 *            index
	 */
	public void selectItem(int index) {
		List<SuggestionMenuItem> items = getItems();
		if (index > -1 && index < items.size()) {
			itemOver(items.get(index), false);
		}
	}

	/**
	 * Adds a menu item to the bar.
	 *
	 * @param item
	 *            the item to be added
	 * @return the {@link SuggestionMenuItem} object
	 */
	public SuggestionMenuItem addItem(SuggestionMenuItem item) {
		return insertItem(item, allItems.size());
	}

	/**
	 * Adds a menu item to the bar containing SafeHtml, that will fire the given
	 * command when it is selected.
	 *
	 * @param html
	 *            the item's html text
	 * @param cmd
	 *            the command to be fired
	 * @return the {@link SuggestionMenuItem} object created
	 */
	public SuggestionMenuItem addItem(SafeHtml html, ScheduledCommand cmd) {
		return addItem(new SuggestionMenuItem(html, cmd));
	}

	/**
	 * Adds a menu item to the bar, that will fire the given command when it is
	 * selected.
	 *
	 * @param text
	 *            the item's text
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as html
	 * @param cmd
	 *            the command to be fired
	 * @return the {@link SuggestionMenuItem} object created
	 */
	public SuggestionMenuItem addItem(String text, boolean asHTML,
			ScheduledCommand cmd) {
		return addItem(new SuggestionMenuItem(text, asHTML, cmd));
	}

	/**
	 * Adds a menu item to the bar, that will fire the given command when it is
	 * selected.
	 *
	 * @param text
	 *            the item's text
	 * @param cmd
	 *            the command to be fired
	 * @return the {@link SuggestionMenuItem} object created
	 */
	public SuggestionMenuItem addItem(String text, ScheduledCommand cmd) {
		return addItem(new SuggestionMenuItem(text, cmd));
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link SuggestionMenuItem}s.
	 *
	 * @return the {@link SuggestionMenuItemSeparator} object created
	 */
	public SuggestionMenuItemSeparator addSeparator() {
		return addSeparator(new SuggestionMenuItemSeparator());
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link SuggestionMenuItem}s.
	 *
	 * @param separator
	 *            the {@link SuggestionMenuItemSeparator} to be added
	 * @return the {@link SuggestionMenuItemSeparator} object
	 */
	public SuggestionMenuItemSeparator addSeparator(
			SuggestionMenuItemSeparator separator) {
		return insertSeparator(separator, allItems.size());
	}

	/**
	 * Removes all menu items from this menu bar.
	 */
	public void clearItems() {
		// Deselect the current item
		selectItem(null);

		Element container = getItemContainerElement();
		while (DOM.getChildCount(container) > 0) {
			container.removeChild(DOM.getChild(container, 0));
		}

		// Set the parent of all items to null
		for (UIObject item : allItems) {
			setItemColSpan(item, 1);
			if (item instanceof SuggestionMenuItemSeparator) {
				((SuggestionMenuItemSeparator) item).setParentMenu(null);
			} else {
				((SuggestionMenuItem) item).setParentMenu(null);
			}
		}

		// Clear out all of the items and separators
		items.clear();
		allItems.clear();
	}

	/**
	 * Give this MenuBar focus.
	 */
	public void focus() {
		FocusImpl.getFocusImplForPanel().focus(getElement());
	}

	/**
	 * Get the index of a {@link SuggestionMenuItem}.
	 *
	 * @return the index of the item, or -1 if it is not contained by this
	 *         MenuBar
	 */
	public int getItemIndex(SuggestionMenuItem item) {
		return allItems.indexOf(item);
	}

	/**
	 * Get the index of a {@link SuggestionMenuItemSeparator}.
	 *
	 * @return the index of the separator, or -1 if it is not contained by this
	 *         MenuBar
	 */
	public int getSeparatorIndex(SuggestionMenuItemSeparator item) {
		return allItems.indexOf(item);
	}

	/**
	 * Adds a menu item to the bar at a specific index.
	 *
	 * @param item
	 *            the item to be inserted
	 * @param beforeIndex
	 *            the index where the item should be inserted
	 * @return the {@link SuggestionMenuItem} object
	 * @throws IndexOutOfBoundsException
	 *             if <code>beforeIndex</code> is out of range
	 */
	public SuggestionMenuItem insertItem(SuggestionMenuItem item,
			int beforeIndex) throws IndexOutOfBoundsException {
		// Check the bounds
		if (beforeIndex < 0 || beforeIndex > allItems.size()) {
			throw new IndexOutOfBoundsException();
		}

		// Add to the list of items
		allItems.add(beforeIndex, item);
		int itemsIndex = 0;
		for (int i = 0; i < beforeIndex; i++) {
			if (allItems.get(i) instanceof SuggestionMenuItem) {
				itemsIndex++;
			}
		}
		items.add(itemsIndex, item);

		// Setup the menu item
		addItemElement(beforeIndex, item.getElement());
		item.setParentMenu(this);
		item.setSelectionStyle(false);
		return item;
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link SuggestionMenuItem}s at the specified index.
	 *
	 * @param beforeIndex
	 *            the index where the separator should be inserted
	 * @return the {@link SuggestionMenuItemSeparator} object
	 * @throws IndexOutOfBoundsException
	 *             if <code>beforeIndex</code> is out of range
	 */
	public SuggestionMenuItemSeparator insertSeparator(int beforeIndex) {
		return insertSeparator(new SuggestionMenuItemSeparator(), beforeIndex);
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link SuggestionMenuItem}s at the specified index.
	 *
	 * @param separator
	 *            the {@link SuggestionMenuItemSeparator} to be inserted
	 * @param beforeIndex
	 *            the index where the separator should be inserted
	 * @return the {@link SuggestionMenuItemSeparator} object
	 * @throws IndexOutOfBoundsException
	 *             if <code>beforeIndex</code> is out of range
	 */
	public SuggestionMenuItemSeparator insertSeparator(
			SuggestionMenuItemSeparator separator, int beforeIndex)
			throws IndexOutOfBoundsException {
		// Check the bounds
		if (beforeIndex < 0 || beforeIndex > allItems.size()) {
			throw new IndexOutOfBoundsException();
		}

		setItemColSpan(separator, 2);
		addItemElement(beforeIndex, separator.getElement());
		separator.setParentMenu(this);
		allItems.add(beforeIndex, separator);
		return separator;
	}

	/**
	 * Check whether or not this widget will steal keyboard focus when the mouse
	 * hovers over it.
	 *
	 * @return true if enabled, false if disabled
	 */
	public boolean isFocusOnHoverEnabled() {
		return focusOnHover;
	}

	/**
	 * Moves the menu selection down to the next item. If there is no selection,
	 * selects the first item. If there are no items at all, does nothing.
	 */
	public void moveSelectionDown() {
		if (selectFirstItemIfNoneSelected()) {
			return;
		}
		selectNextItem();
	}

	/**
	 * Moves the menu selection up to the previous item. If there is no
	 * selection, selects the first item. If there are no items at all, does
	 * nothing.
	 */
	public void moveSelectionUp() {
		if (selectFirstItemIfNoneSelected()) {
			return;
		}
		selectPrevItem();
	}

	@Override
	public void onBrowserEvent(Event event) {
		SuggestionMenuItem item = findItem(DOM.eventGetTarget(event));
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK: {
			FocusImpl.getFocusImplForPanel().focus(getElement());
			// Fire an item's command when the user clicks on it.
			if (item != null) {
				doItemAction(item, true, true);
			}
			break;
		}

		case Event.ONMOUSEOVER: {
			if (item != null) {
				itemOver(item, true);
			}
			break;
		}

		case Event.ONMOUSEOUT: {
			if (item != null) {
				itemOver(null, false);
			}
			break;
		}

		case Event.ONFOCUS: {
			selectFirstItemIfNoneSelected();
			break;
		}

		case Event.ONKEYDOWN: {
			int keyCode = event.getKeyCode();
			boolean isRtl = LocaleInfo.getCurrentLocale().isRTL();
			keyCode = KeyCodes.maybeSwapArrowKeysForRtl(keyCode, isRtl);
			switch (keyCode) {
			case KeyCodes.KEY_UP:
				moveSelectionUp();
				eatEvent(event);
				break;
			case KeyCodes.KEY_DOWN:
				moveSelectionDown();
				eatEvent(event);
				break;
			case KeyCodes.KEY_ESCAPE:
				selectItem(null);
				eatEvent(event);
				break;
			case KeyCodes.KEY_TAB:
				selectItem(null);
				break;
			case KeyCodes.KEY_ENTER:
				if (!selectFirstItemIfNoneSelected()) {
					doItemAction(selectedItem, true, true);
					eatEvent(event);
				}
				break;
			} // end switch(keyCode)

			break;
		} // end case Event.ONKEYDOWN
		} // end switch (DOM.eventGetType(event))
		super.onBrowserEvent(event);
	}

	/**
	 * Removes the specified menu item from the bar.
	 *
	 * @param item
	 *            the item to be removed
	 */
	public void removeItem(SuggestionMenuItem item) {
		// Unselect if the item is currently selected
		if (selectedItem == item) {
			selectItem(null);
		}

		if (removeItemElement(item)) {
			setItemColSpan(item, 1);
			items.remove(item);
			item.setParentMenu(null);
		}
	}

	/**
	 * Removes the specified {@link SuggestionMenuItemSeparator} from the bar.
	 *
	 * @param separator
	 *            the separator to be removed
	 */
	public void removeSeparator(SuggestionMenuItemSeparator separator) {
		if (removeItemElement(separator)) {
			separator.setParentMenu(null);
		}
	}

	/**
	 * Select the given SuggestionMenuItem, which must be a direct child of this
	 * MenuBar.
	 *
	 * @param item
	 *            the SuggestionMenuItem to select, or null to clear selection
	 */
	public void selectItem(SuggestionMenuItem item) {
		assert item == null || item.getParentMenu() == this;

		if (item == selectedItem) {
			return;
		}

		if (selectedItem != null) {
			selectedItem.setSelectionStyle(false);
		}

		if (item != null) {
			item.setSelectionStyle(true);
			Roles.getMenubarRole().setAriaActivedescendantProperty(
					getElement(), Id.of(item.getElement()));
		}

		selectedItem = item;
	}

	/**
	 * Enable or disable auto focus when the mouse hovers over the MenuBar. This
	 * allows the MenuBar to respond to keyboard events without the user having
	 * to click on it, but it will steal focus from other elements on the page.
	 * Enabled by default.
	 *
	 * @param enabled
	 *            true to enable, false to disable
	 */
	public void setFocusOnHoverEnabled(boolean enabled) {
		focusOnHover = enabled;
	}

	/**
	 * Returns a list containing the <code>SuggestionMenuItem</code> objects in
	 * the menu bar. If there are no items in the menu bar, then an empty
	 * <code>List</code> object will be returned.
	 *
	 * @return a list containing the <code>SuggestionMenuItem</code> objects in
	 *         the menu bar
	 */
	protected List<SuggestionMenuItem> getItems() {
		return this.items;
	}

	/**
	 * Returns the <code>SuggestionMenuItem</code> that is currently selected
	 * (highlighted) by the user. If none of the items in the menu are currently
	 * selected, then <code>null</code> will be returned.
	 *
	 * @return the <code>SuggestionMenuItem</code> that is currently selected,
	 *         or <code>null</code> if no items are currently selected
	 */
	public SuggestionMenuItem getSelectedItem() {
		return this.selectedItem;
	}

	/**
	 * <b>Affected Elements:</b>
	 * <ul>
	 * <li>-item# = the {@link SuggestionMenuItem} at the specified index.</li>
	 * </ul>
	 *
	 * @see UIObject#onEnsureDebugId(String)
	 */
	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		setSuggestionMenuItemDebugIds(baseID);
	}

	/*
	 * Performs the action associated with the given menu item. If the item has
	 * a popup associated with it, the popup will be shown. If it has a command
	 * associated with it, and 'fireCommand' is true, then the command will be
	 * fired. Popups associated with other items will be hidden.
	 * 
	 * @param item the item whose popup is to be shown. @param fireCommand
	 * <code>true</code> if the item's command should be fired,
	 * <code>false</code> otherwise.
	 */
	void doItemAction(final SuggestionMenuItem item, boolean fireCommand,
			boolean focus) {
		// Should not perform any action if the item is disabled
		if (!item.isEnabled()) {
			return;
		}

		// Ensure that the item is selected.
		selectItem(item);

		// if the command should be fired and the item has one, fire it
		if (fireCommand && item.getScheduledCommand() != null) {
			// Close this menu and all of its parents.
			selectItem(null);

			// Remove the focus from the menu
			// FocusPanel.impl.blur(getElement());
			FocusImpl.getFocusImplForPanel().blur(getElement());

			// Fire the item's command. The command must be fired in the same
			// event
			// loop or popup blockers will prevent popups from opening.
			final ScheduledCommand cmd = item.getScheduledCommand();
			Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					cmd.execute();
				}
			});
		}
		
	}

	public void itemOver(SuggestionMenuItem item, boolean focus) {

		if (item != null && !item.isEnabled()) {
			return;
		}

		// Style the item selected when the mouse enters.
		selectItem(item);
		if (focus && focusOnHover) {
			focus();
		}
	}

	/**
	 * Set the IDs of the menu items.
	 *
	 * @param baseID
	 *            the base ID
	 */
	public void setSuggestionMenuItemDebugIds(String baseID) {
		int itemCount = 0;
		for (SuggestionMenuItem item : items) {
			item.ensureDebugId(baseID + "-item" + itemCount);
			itemCount++;
		}
	}

	/**
	 * Physically add the td element of a {@link SuggestionMenuItem} or
	 * {@link SuggestionMenuItemSeparator} to this {@link MenuBar}.
	 *
	 * @param beforeIndex
	 *            the index where the separator should be inserted
	 * @param tdElem
	 *            the td element to be added
	 */
	private void addItemElement(int beforeIndex, Element tdElem) {

		Element tr = DOM.createTR();
		DOM.insertChild(body, tr, beforeIndex);
		DOM.appendChild(tr, tdElem);

	}

	private void eatEvent(Event event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private SuggestionMenuItem findItem(Element hItem) {
		for (SuggestionMenuItem item : items) {
			if (item.getElement().isOrHasChild(hItem)) {
				return item;
			}
		}
		return null;
	}

	private Element getItemContainerElement() {
		return body;
	}

	// AbstractImagePrototype subMenuIcon
	private void init() {
		Element table = DOM.createTable();
		body = DOM.createTBody();
		DOM.appendChild(table, body);

		Element outer = FocusImpl.getFocusImplForPanel().createFocusable();
		DOM.appendChild(outer, table);
		setElement(outer);

		Roles.getMenubarRole().set(getElement());

		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONFOCUS | Event.ONKEYDOWN);

		setStyleName(STYLENAME_DEFAULT);
		addStyleDependentName("vertical");

		// Hide focus outline in Mozilla/Webkit/Opera
		getElement().getStyle().setProperty("outline", "0px");

		// Hide focus outline in IE 6/7
		getElement().setAttribute("hideFocus", "true");

		// Deselect items when blurring without a child menu.
		addDomHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				selectItem(null);
			}
		}, BlurEvent.getType());
	}


	/**
	 * Removes the specified item from the {@link MenuBar} and the physical DOM
	 * structure.
	 *
	 * @param item
	 *            the item to be removed
	 * @return true if the item was removed
	 */
	private boolean removeItemElement(UIObject item) {
		int idx = allItems.indexOf(item);
		if (idx == -1) {
			return false;
		}

		Element container = getItemContainerElement();
		container.removeChild(DOM.getChild(container, idx));
		allItems.remove(idx);
		return true;
	}

	/**
	 * Selects the first item in the menu if no items are currently selected.
	 * Has no effect if there are no items.
	 *
	 * @return true if no item was previously selected, false otherwise
	 */
	private boolean selectFirstItemIfNoneSelected() {
		if (selectedItem == null) {
			for (SuggestionMenuItem nextItem : items) {
				if (nextItem.isEnabled()) {
					selectItem(nextItem);
					break;
				}
			}
			return true;
		}
		return false;
	}

	private void selectNextItem() {
		if (selectedItem == null) {
			return;
		}

		int index = items.indexOf(selectedItem);
		// We know that selectedItem is set to an item that is contained in the
		// items collection.
		// Therefore, we know that index can never be -1.
		assert (index != -1);

		SuggestionMenuItem itemToBeSelected;

		int firstIndex = index;
		while (true) {
			index = index + 1;
			if (index == items.size()) {
				// we're at the end, loop around to the start
				index = 0;
			}
			if (index == firstIndex) {
				itemToBeSelected = items.get(firstIndex);
				break;
			} else {
				itemToBeSelected = items.get(index);
				if (itemToBeSelected.isEnabled()) {
					break;
				}
			}
		}

		selectItem(itemToBeSelected);
	}

	private void selectPrevItem() {
		if (selectedItem == null) {
			return;
		}

		int index = items.indexOf(selectedItem);
		// We know that selectedItem is set to an item that is contained in the
		// items collection.
		// Therefore, we know that index can never be -1.
		assert (index != -1);

		SuggestionMenuItem itemToBeSelected;

		int firstIndex = index;
		while (true) {
			index = index - 1;
			if (index < 0) {
				// we're at the start, loop around to the end
				index = items.size() - 1;
			}
			if (index == firstIndex) {
				itemToBeSelected = items.get(firstIndex);
				break;
			} else {
				itemToBeSelected = items.get(index);
				if (itemToBeSelected.isEnabled()) {
					break;
				}
			}
		}

		selectItem(itemToBeSelected);
	}

	/**
	 * Set the colspan of a {@link SuggestionMenuItem} or
	 * {@link SuggestionMenuItemSeparator}.
	 *
	 * @param item
	 *            the {@link SuggestionMenuItem} or
	 *            {@link SuggestionMenuItemSeparator}
	 * @param colspan
	 *            the colspan
	 */
	private void setItemColSpan(UIObject item, int colspan) {
		item.getElement().setPropertyInt("colSpan", colspan);
	}
}
