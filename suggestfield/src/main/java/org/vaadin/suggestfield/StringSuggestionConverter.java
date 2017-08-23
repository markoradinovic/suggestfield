package org.vaadin.suggestfield;

import org.vaadin.suggestfield.SuggestField.SuggestionConverter;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

/**
 * Simple converter that uses {@link String} items.
 * 
 * @author markoradinovic
 *
 */
@SuppressWarnings("serial")
public class StringSuggestionConverter implements SuggestionConverter {

	@Override
	public SuggestFieldSuggestion toSuggestion(Object item) {
		assert (item != null) : "Item cannot be null";
		String value = (String) item;
		return new SuggestFieldSuggestion(value, value, value);
	}

	@Override
	public Object toItem(SuggestFieldSuggestion suggestion) {
		assert (suggestion != null) : "Suggestion cannot be null";
		return suggestion.getId();
	}

}
