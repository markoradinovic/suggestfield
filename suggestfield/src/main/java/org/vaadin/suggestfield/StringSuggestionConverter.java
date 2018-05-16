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
public class StringSuggestionConverter implements SuggestionConverter<String> {

	public static final StringSuggestionConverter DEFAULT_INSTANCE = new StringSuggestionConverter();
	
	@Override
	public SuggestFieldSuggestion toSuggestion(String value) {
		assert (value != null) : "Value cannot be null";
		return new SuggestFieldSuggestion(value, value, value);
	}

	@Override
	public String toItem(SuggestFieldSuggestion suggestion) {
		assert (suggestion != null) : "Suggestion cannot be null";
		return suggestion.getId();
	}

}
