package org.vaadin.suggestfield;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.suggestfield.client.SuggestFieldClientRpc;
import org.vaadin.suggestfield.client.SuggestFieldServerRpc;
import org.vaadin.suggestfield.client.SuggestFieldState;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component.Focusable;

@SuppressWarnings("serial")
public class SuggestField extends AbstractField<Object> implements
		SuggestFieldServerRpc, Focusable, BlurNotifier, FocusNotifier {
	
	/**
	 * This handler must be implemented 
	 *
	 */
	public interface SuggestionHandler extends Serializable {
		
		/**
		 * Provide suggestions based on query string
		 * @param query
		 * @return
		 */
		public List<Object> searchItems(String query);
	}
	
	public interface SuggestionConverter extends Serializable {
		
		public SuggestFieldSuggestion toSuggestion(Object item);
		
		public Object toItem(SuggestFieldSuggestion suggestion);
		
	}
	
	FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {

		private static final long serialVersionUID = -780524775769549747L;

		@Override
		protected void fireEvent(Event event) {
			SuggestField.this.fireEvent(event);
		}
	};
	
	private SuggestionConverter suggestionConverter = new StringSuggestionConverter();
	private SuggestionHandler suggestionHandler;

	public SuggestField() {
		registerRpc(this, SuggestFieldServerRpc.class);
		registerRpc(focusBlurRpc);
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}

	@Override
	public SuggestFieldState getState() {
		return (SuggestFieldState) super.getState();
	}

	/**
	 * ServerRpc
	 */
	public void searchSuggestions(String query) {
		List<SuggestFieldSuggestion> suggestions = new ArrayList<SuggestFieldSuggestion>();
		if (suggestionHandler != null && suggestionConverter != null) {
			List<Object> searchResult = suggestionHandler.searchItems(query);
			if (searchResult == null) {
				searchResult = new ArrayList<Object>();
			}
			for (Object result : searchResult) {
				suggestions.add(suggestionConverter.toSuggestion(result));
			}
		}
		getRpcProxy(SuggestFieldClientRpc.class).setSuggusetion(suggestions);
	}

	@Override
	public void onSuggestionSelected(SuggestFieldSuggestion suggestion) {
		if (suggestionConverter != null) {
			setValue(suggestionConverter.toItem(suggestion), false);
		}
		
	}
	
	@Override
	protected void setValue(Object newFieldValue, boolean repaintIsNotNeeded)
			throws com.vaadin.data.Property.ReadOnlyException,
			ConversionException, InvalidValueException {
		
		super.setValue(newFieldValue, repaintIsNotNeeded);
		/*
		 * This allows for a new value to be sent to client if setValue was called before selecting suggestion
		 */
		if (suggestionConverter != null) {
			getRpcProxy(SuggestFieldClientRpc.class).setCurrentSuggusetion(suggestionConverter.toSuggestion(newFieldValue));
		}
	}

	public void setDelay(int delayMillis) {
		getState().delayMillis = delayMillis;
	}

	/**
	 * Gets the current input prompt.
	 * 
	 * @see #setInputPrompt(String)
	 * @return the current input prompt, or null if not enabled
	 */
	public String getInputPrompt() {
		return getState().inputPrompt;
	}

	/**
	 * Sets the input prompt - a textual prompt that is displayed when the field
	 * would otherwise be empty, to prompt the user for input.
	 * 
	 * @param inputPrompt
	 *            inpitprompth
	 */
	public void setInputPrompt(String inputPrompt) {
		getState().inputPrompt = inputPrompt;
		markAsDirty();
	}

	public void setMinimumQueryCharacters(int minimumQueryCharacters) {
		getState().minimumQueryCharacters = minimumQueryCharacters;
	}

	public void setTrimQuery(boolean trimQuery) {
		getState().trimQuery = trimQuery;
	}
	
	@Override
	public void focus() {
		super.focus();
	}
    
    public void setSuggestionHandler(SuggestionHandler suggestionHandler) {
    	this.suggestionHandler = suggestionHandler;
    }

	@Override
	public void addFocusListener(FocusListener listener) {
		addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
				FocusListener.focusMethod);
	}

	@Deprecated
	@Override
	public void addListener(FocusListener listener) {
		addFocusListener(listener);
		
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
		
	}

	@Deprecated
	@Override
	public void removeListener(FocusListener listener) {
		removeFocusListener(listener);
		
	}

	@Override
	public void addBlurListener(BlurListener listener) {
		addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
				BlurListener.blurMethod);
		
	}

	@Deprecated
	@Override
	public void addListener(BlurListener listener) {
		addBlurListener(listener);
		
	}

	@Override
	public void removeBlurListener(BlurListener listener) {
		removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
		
	}

	@Deprecated
	@Override
	public void removeListener(BlurListener listener) {
		removeBlurListener(listener);	
	}
	
	public SuggestionConverter getSuggestionConverter() {
		return suggestionConverter;
	}

	public void setSuggestionConverter(SuggestionConverter suggestionConverter) {
		this.suggestionConverter = suggestionConverter;
	}
}