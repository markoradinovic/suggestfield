package org.vaadin.suggestfield.client;

import com.vaadin.shared.AbstractFieldState;

@SuppressWarnings("serial")
public class SuggestFieldState extends AbstractFieldState {
	public SuggestFieldSuggestion value = null;
	public int delayMillis = 300;
	public String inputPrompt = "";
	public int minimumQueryCharacters = 3;
	public boolean trimQuery = true;
	public String popupWidth = null;
	public boolean allowNewItem = false;
	public int keyCode = -1;
	public int[] modifierKeys = new int[] {};
}
