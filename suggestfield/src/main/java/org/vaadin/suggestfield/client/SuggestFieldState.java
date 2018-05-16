package org.vaadin.suggestfield.client;

import java.io.Serializable;

import com.vaadin.shared.AbstractFieldState;

@SuppressWarnings("serial")
public class SuggestFieldState extends AbstractFieldState {
	public Object value = null;
	public SuggestFieldSuggestion fieldSuggestion = null;
	public int delayMillis = 300;
	public String placeHolder = "";
	public int minimumQueryCharacters = 3;
	public boolean trimQuery = true;
	public String popupWidth = null;
	public boolean allowNewItem = false;
	public int keyCode = -1;
	public int[] modifierKeys = new int[] {};
	public boolean tokenMode = false;
}
