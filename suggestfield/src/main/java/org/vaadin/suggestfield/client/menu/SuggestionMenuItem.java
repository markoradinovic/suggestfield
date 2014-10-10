package org.vaadin.suggestfield.client.menu;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SuggestionMenuItem extends UIObject implements HasHTML,
		HasEnabled, HasSafeHtml {

	private static final String STYLENAME_DEFAULT = "gwt-MenuItem";
	private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";
	private static final String DEPENDENT_STYLENAME_DISABLED_ITEM = "disabled";

	private ScheduledCommand command;
	private SuggestionMenuBar parentMenu; //, subMenu;
	private boolean enabled = true;
	
	private Suggestion suggestion;
	
	public SuggestionMenuItem(Suggestion suggestion, boolean asHTML) {
		this(suggestion.getDisplayString(), asHTML);
		// Each suggestion should be placed in a single row in the
		// suggestion
		// menu. If the window is resized and the suggestion cannot fit on a
		// single row, it should be clipped (instead of wrapping around and
		// taking up a second row).
		getElement().getStyle().setProperty("whiteSpace", "nowrap");
		getElement().getStyle().setProperty("overflow", "hidden");
		setStyleName(STYLENAME_DEFAULT);
		setSuggestion(suggestion);
	}
	
	public Suggestion getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

	/**
	 * Constructs a new menu item that fires a command when it is selected.
	 *
	 * @param html
	 *            the item's html text
	 */
	public SuggestionMenuItem(SafeHtml html) {
		this(html.asString(), true);
	}

	/**
	 * Constructs a new menu item that fires a command when it is selected.
	 *
	 * @param html
	 *            the item's text
	 * @param cmd
	 *            the command to be fired when it is selected
	 */
	public SuggestionMenuItem(SafeHtml html, ScheduledCommand cmd) {
		this(html.asString(), true, cmd);
	}


	/**
	 * Constructs a new menu item that fires a command when it is selected.
	 *
	 * @param text
	 *            the item's text
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as html
	 * @param cmd
	 *            the command to be fired when it is selected
	 */
	public SuggestionMenuItem(String text, boolean asHTML, ScheduledCommand cmd) {
		this(text, asHTML);
		setScheduledCommand(cmd);
	}

	/**
	 * Constructs a new menu item that fires a command when it is selected.
	 *
	 * @param text
	 *            the item's text
	 * @param cmd
	 *            the command to be fired when it is selected
	 */
	public SuggestionMenuItem(String text, ScheduledCommand cmd) {
		this(text, false);
		setScheduledCommand(cmd);
	}


	@SuppressWarnings("deprecation")
	public SuggestionMenuItem(String text, boolean asHTML) {
		setElement(DOM.createTD());
		setSelectionStyle(false);

		if (asHTML) {
			setHTML(text);
		} else {
			setText(text);
		}
		setStyleName("gwt-MenuItem");

		getElement().setAttribute("id", DOM.createUniqueId());
		// Add a11y role "menuitem"
		Roles.getMenuitemRole().set(getElement());
	}

	/**
	 * Gets the command associated with this item. If a scheduled command is
	 * associated with this item a command that can be used to execute the
	 * scheduled command will be returned.
	 *
	 * @return the command
	 * @deprecated use {@link #getScheduledCommand()} instead
	 */
	@Deprecated
	public Command getCommand() {
		Command rtnVal;

		if (command == null) {
			rtnVal = null;
		} else if (command instanceof Command) {
			rtnVal = (Command) command;
		} else {
			rtnVal = new Command() {
				@Override
				public void execute() {
					if (command != null) {
						command.execute();
					}
				}
			};
		}

		return rtnVal;
	}

	@Override
	public String getHTML() {
		return getElement().getInnerHTML();
	}

	/**
	 * Gets the menu that contains this item.
	 *
	 * @return the parent menu, or <code>null</code> if none exists.
	 */
	public SuggestionMenuBar getParentMenu() {
		return parentMenu;
	}

	/**
	 * Gets the scheduled command associated with this item.
	 *
	 * @return this item's scheduled command, or <code>null</code> if none
	 *         exists
	 */
	public ScheduledCommand getScheduledCommand() {
		return command;
	}

	@Override
	public String getText() {
		return getElement().getInnerText();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the command associated with this item.
	 *
	 * @param cmd
	 *            the command to be associated with this item
	 * @deprecated use {@link #setScheduledCommand(ScheduledCommand)} instead
	 */
	@Deprecated
	public void setCommand(Command cmd) {
		command = cmd;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (enabled) {
			removeStyleDependentName(DEPENDENT_STYLENAME_DISABLED_ITEM);
		} else {
			addStyleDependentName(DEPENDENT_STYLENAME_DISABLED_ITEM);
		}
		this.enabled = enabled;
	}

	@Override
	public void setHTML(SafeHtml html) {
		setHTML(html.asString());
	}

	@Override
	public void setHTML(String html) {
		getElement().setInnerHTML(html);
	}

	/**
	 * Sets the scheduled command associated with this item.
	 *
	 * @param cmd
	 *            the scheduled command to be associated with this item
	 */
	public void setScheduledCommand(ScheduledCommand cmd) {
		command = cmd;
	}

	@Override
	public void setText(String text) {
		getElement().setInnerText(text);
	}

	protected void setSelectionStyle(boolean selected) {
		if (selected) {
			addStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
		} else {
			removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
		}
	}

	void setParentMenu(SuggestionMenuBar parentMenu) {
		this.parentMenu = parentMenu;
	}
}