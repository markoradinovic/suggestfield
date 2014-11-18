package org.vaadin.suggestfield.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VTextField;

import org.vaadin.suggestfield.client.menu.SuggestionMenuBar;
import org.vaadin.suggestfield.client.menu.SuggestionMenuItem;

public class VSuggestField extends Composite implements HasText, Focusable,
		HasEnabled, HasAllKeyHandlers, HasValue<String>,
		HasSelectionHandlers<Suggestion>, IsEditor<LeafValueEditor<String>> {

	private static final String CLASSNAME = "v-textfield";

	/**
	 * The callback used when a user selects a {@link Suggestion}.
	 */
	public static interface SuggestionCallback {
		void onSuggestionSelected(Suggestion suggestion);
	}

	/**
	 * Callback for Connector
	 */
	public interface FindSuggestionsListener {
		void findSuggestions(String query);
	}

	public ApplicationConnection client;

	private String currentText;
	private final VTextField box;
	private final SuggestionPopup popup;
	private final SuggestionMenuBar menu;

	private SuggestionTimer suggestionTimer = null;
	private FindSuggestionsListener suggestionListener = null;
	private List<SuggestFieldSuggestion> suggestions = new ArrayList<SuggestFieldSuggestion>();

	public boolean trimQuery = true;
	public int minimumQueryCharacters = 3;
	private SuggestFieldSuggestion currentSuggestion;
	
	public String popupWidth = null;
	
	/*
	 * Callback for selecting suggestion
	 */
	private final SuggestionCallback suggestionCallback = new SuggestionCallback() {
		public void onSuggestionSelected(Suggestion suggestion) {
			box.setFocus(true);
			setNewSelection(suggestion);
		}
	};

	/**
	 * Constructor
	 */
	public VSuggestField() {
		box = GWT.create(VTextField.class);
		box.setImmediate(true);
		popup = new SuggestionPopup();
		menu = new SuggestionMenuBar();
		popup.setWidget(menu);
		suggestionTimer = new SuggestionTimer();
		initWidget(box);

		addEventsToTextBox();
		setStyleName(CLASSNAME);
	}

	/*
	 * Schedule loading suggestions
	 */
	private void scheduleQuery(final String query) {

		suggestionTimer.cancel();
		if (query != null && query.equals(box.getText())) {
			suggestionTimer.setQuery(query);
			suggestionTimer.schedule(300);
		}
	}

	/*
	 * Delayed loading from server 
	 */
	private class SuggestionTimer extends Timer {

		private String query;

		@Override
		public void run() {
			if (suggestionListener != null && query != null) {
				suggestionListener.findSuggestions(query);
			}
		}

		public void setQuery(String query) {
			this.query = query;
		}
	}

	/*
	 * After suggestions is loaded from server, diplay popup
	 */
	public void setSuggestions(List<SuggestFieldSuggestion> suggestions) {
		this.suggestions = Collections.unmodifiableList(suggestions);
		showSuggestions();
	}

	public void setCurrentSuggestion(SuggestFieldSuggestion suggestion) {
		this.currentSuggestion = suggestion;
		resetText();
		hideSuggestions();

	}

	private List<SuggestOracle.Suggestion> wrapSuggestions(
			List<SuggestFieldSuggestion> in) {
		List<SuggestOracle.Suggestion> out = new ArrayList<SuggestOracle.Suggestion>();
		for (final SuggestFieldSuggestion wrappedSuggestion : in) {
			out.add(new OracleSuggestionImpl(wrappedSuggestion));
		}
		return out;

	}

	private void addEventsToTextBox() {
		class TextBoxEvents implements KeyDownHandler, KeyUpHandler,
				ValueChangeHandler<String>, BlurHandler {

			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_DOWN:
					moveSelectionDown();
					break;
				case KeyCodes.KEY_UP:
					moveSelectionUp();
					break;
				case KeyCodes.KEY_ENTER:
				case KeyCodes.KEY_TAB:
					Suggestion suggestion = getCurrentSelection();
					if (suggestion == null) {
						hideSuggestions();
					} else {
						setNewSelection(suggestion);
					}
					break;
				}
			}

			public void onKeyUp(KeyUpEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_DOWN:
				case KeyCodes.KEY_UP:
				case KeyCodes.KEY_ENTER:
				case KeyCodes.KEY_TAB:
					break;
				case KeyCodes.KEY_ESCAPE:
					hideSuggestions();
					resetText();
					event.preventDefault();
					event.stopPropagation();
					break;
				default:
					// After every user key input, refresh the popup's
					// suggestions.
					refreshSuggestions();
				}
			}

			public void onValueChange(ValueChangeEvent<String> event) {
				delegateEvent(VSuggestField.this, event);
			}

			@Override
			public void onBlur(BlurEvent event) {
				resetText();

			}
		}

		TextBoxEvents events = new TextBoxEvents();
		box.addKeyDownHandler(events);
		box.addKeyUpHandler(events);
		box.addValueChangeHandler(events);
		box.addBlurHandler(events);
	}

	public void resetText() {
		box.updateFieldContent((currentSuggestion != null) ? currentSuggestion
				.getReplacementString() : "");
	}

	/**
	 * Refreshes the current list of suggestions.
	 */
	public void refreshSuggestionList() {
		if (isAttached()) {
			refreshSuggestions();
		}
	}

	void showSuggestions() {
		// invoke the callback
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {

				// Hide the popup if there are no suggestions to display.
				boolean anySuggestions = (suggestions != null && suggestions
						.size() > 0);
				if (!anySuggestions) {
					hideSuggestions();
					return;
				}
				// Hide the popup before we manipulate the menu within it.
				// If we do not
				// do this, some browsers will redraw the popup as items are
				// removed
				// and added to the menu.
				if (popup.isAttached()) {
					popup.hide();
				}

				menu.clearItems();

				for (final Suggestion curSuggestion : wrapSuggestions(suggestions)) {
					final SuggestionMenuItem menuItem = new SuggestionMenuItem(
							curSuggestion, true);
					menuItem.setScheduledCommand(new ScheduledCommand() {
						public void execute() {
							suggestionCallback
									.onSuggestionSelected(curSuggestion);
						}
					});

					menu.addItem(menuItem);
				}

				if (anySuggestions) {
					// Select the first item in the suggestion menu.
					menu.selectItem(0);
				}
				popup.removeAutoHidePartner(getElement());
				popup.addAutoHidePartner(getElement());
				popup.showPopup();
			}
		});
	}

	private void fireSuggestionEvent(Suggestion selectedSuggestion) {
		SelectionEvent.fire(this, selectedSuggestion);
	}

	private void refreshSuggestions() {
		// Get the raw text.
		String text = getText();
		if (text.equals(currentText)) {
			return;
		} else {
			currentText = text;
		}
		// send event to the server side
		if (trimQuery) {
			text = text.trim();
		}
		if (text.length() >= minimumQueryCharacters) {
			scheduleQuery(text);
		}
	}

	/**
	 * Set the new suggestion in the text box.
	 *
	 * @param curSuggestion
	 *            the new suggestion
	 */
	private void setNewSelection(Suggestion curSuggestion) {
		assert curSuggestion != null : "suggestion cannot be null";
		currentText = curSuggestion.getReplacementString();
		setText(currentText);
		hideSuggestions();
		fireSuggestionEvent(curSuggestion);
	}
	
	/*
	 * 
	 * Used by connector
	 */
	public void setFindSuggestionsListener(FindSuggestionsListener listener) {
		this.suggestionListener = listener;
	}
	
	public void setInputPrompt(String inputPrompt) {
		box.setInputPrompt(inputPrompt);
	}
	
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}

	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addDomHandler(handler, KeyPressEvent.getType());
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return addDomHandler(handler, KeyUpEvent.getType());
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Suggestion> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public LeafValueEditor<String> asEditor() {
		return box.asEditor();
	}

	public int getTabIndex() {
		return box.getTabIndex();
	}

	public String getText() {
		return box.getText();
	}

	public String getValue() {
		return box.getValue();
	}

	public ValueBoxBase<String> getValueBox() {
		return box;
	}

	public boolean isEnabled() {
		return box.isEnabled();
	}

	public void setAccessKey(char key) {
		box.setAccessKey(key);
	}

	public void setEnabled(boolean enabled) {
		box.setEnabled(enabled);
		if (!enabled) {
			hideSuggestions();
		}
	}

	public void setFocus(boolean focused) {
		box.setFocus(focused);
	}

	public void setTabIndex(int index) {
		box.setTabIndex(index);
	}

	public void setText(String text) {
		box.updateFieldContent(text);
	}

	public void setValue(String newValue) {
		box.setValue(newValue);
	}

	public void setValue(String value, boolean fireEvents) {
		box.setValue(value, fireEvents);
	}

	/*
	 * Inline suggestion display
	 * 
	 * ==========================================================================
	 */
	protected Suggestion getCurrentSelection() {
		if (!isSuggestionListShowing()) {
			return null;
		}
		SuggestionMenuItem item = menu.getSelectedItem();
		return item == null ? null : ((SuggestionMenuItem) item)
				.getSuggestion();
	}

	protected void hideSuggestions() {
		popup.hide();
	}

	protected void moveSelectionDown() {
		if (isSuggestionListShowing()) {
			menu.selectItem(menu.getSelectedItemIndex() + 1);
		}
	}

	protected void moveSelectionUp() {

		if (isSuggestionListShowing()) {
			if (menu.getSelectedItemIndex() == -1) {
				menu.selectItem(menu.getNumItems() - 1);
			} else {
				menu.selectItem(menu.getSelectedItemIndex() - 1);
			}
		}
	}

	public boolean isSuggestionListShowing() {
		return popup.isShowing();
	}

	/*
	 * 
	 * ==========================================================================
	 */

	/**
	 * SuggestField Popup
	 *
	 */
	public class SuggestionPopup extends VOverlay implements PositionCallback,
			CloseHandler<PopupPanel> {

		private static final int Z_INDEX = 30000;

		private int popupOuterPadding = -1;
		private int topPosition;

		@SuppressWarnings("deprecation")
		public SuggestionPopup() {
			super(true, true, true);
			getElement().getStyle().setZIndex(Z_INDEX);
			setStylePrimaryName("v-filterselect-suggestpopup");
			Roles.getListRole().set(getElement());
			setOwner(VSuggestField.this);
			addCloseHandler(this);
			setAutoHideEnabled(true);
		}

		public void showPopup() {
			// Add TT anchor point
			getElement().setId("VAADIN_COMBOBOX_OPTIONLIST");
			final int x = box.getAbsoluteLeft();

			topPosition = box.getAbsoluteTop();
			topPosition += box.getOffsetHeight();

			setPopupPosition(x, topPosition);
			setPopupPositionAndShow(this);
		}

		@Override
		protected ApplicationConnection getApplicationConnection() {
			return super.getApplicationConnection();
		}

		@Override
		public void setPosition(int offsetWidth, int offsetHeight) {

			int top = -1;
			int left = -1;

			offsetHeight = getOffsetHeight();

			int desiredWidth = box.getOffsetWidth();
			if (popupWidth != null) {
				try {
					desiredWidth = Integer.valueOf(popupWidth.replace("px", ""));
				} catch (NumberFormatException e) {
					desiredWidth = box.getOffsetWidth();
				}
				
			}
			Element menuFirstChild = getWidget().getElement().getFirstChild()
					.cast();
			int naturalMenuWidth = menuFirstChild.getOffsetWidth();

			if (popupOuterPadding == -1) {
				popupOuterPadding = Util.measureHorizontalPaddingAndBorder(
						getElement(), 2);
			}

			if (naturalMenuWidth < desiredWidth) {
				getWidget().setWidth((desiredWidth - popupOuterPadding) + "px");
				menuFirstChild.getStyle().setWidth(100, Unit.PCT);
				naturalMenuWidth = desiredWidth;
			}

			if (BrowserInfo.get().isIE()) {
				/*
				 * IE requires us to specify the width for the container
				 * element. Otherwise it will be 100% wide
				 */
				int rootWidth = naturalMenuWidth - popupOuterPadding;
				getContainerElement().getStyle().setWidth(rootWidth, Unit.PX);
			}

			if (offsetHeight + getPopupTop() > Window.getClientHeight()
					+ Window.getScrollTop()) {
				// popup on top of input instead
				top = getPopupTop() - offsetHeight - box.getOffsetHeight();
				if (top < 0) {
					top = 0;
				}
			} else {
				top = getPopupTop();
				/*
				 * Take popup top margin into account. getPopupTop() returns the
				 * top value including the margin but the value we give must not
				 * include the margin.
				 */
				int topMargin = (top - topPosition);
				top -= topMargin;
			}

			// fetch real width (mac FF bugs here due GWT popups overflow:auto )
			offsetWidth = menuFirstChild.getOffsetWidth();
			if (offsetWidth + getPopupLeft() > Window.getClientWidth()
					+ Window.getScrollLeft()) {
				left = box.getAbsoluteLeft() + box.getOffsetWidth()
						+ Window.getScrollLeft() - offsetWidth;
				if (left < 0) {
					left = 0;
				}
			} else {
				left = getPopupLeft();
			}
			setPopupPosition(left, top);

		}
	}

}
