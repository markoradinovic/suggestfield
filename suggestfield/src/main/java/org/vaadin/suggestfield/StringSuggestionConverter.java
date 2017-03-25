package org.vaadin.suggestfield;

import org.vaadin.suggestfield.SuggestField.SuggestionConverter;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

/**
 * Simple converter that uses {@link String} items
 * 
 * @author markoradinovic
 * @author pesse
 *
 */
@SuppressWarnings("serial")
public class StringSuggestionConverter implements SuggestionConverter<String> {

	@Override
	public SuggestFieldSuggestion toSuggestion(String item) {
		assert (item != null) : "Item cannot be null";
		String value = (String) item;
		return new SuggestFieldSuggestion(value, value, value);
	}

	@Override
	public String toItem(SuggestFieldSuggestion suggestion) {
		assert (suggestion != null) : "Suggestion cannot be null";
		return suggestion.getId();
	}

}
