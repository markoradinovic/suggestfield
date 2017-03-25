package org.vaadin.suggestfield;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.suggestfield.client.SuggestFieldClientRpc;
import org.vaadin.suggestfield.client.SuggestFieldServerRpc;
import org.vaadin.suggestfield.client.SuggestFieldState;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component.Focusable;

/** SuggestField server component
 * 
 * @author markoradinovic
 * @author pesse
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class SuggestField<T> extends AbstractField<T> implements
		SuggestFieldServerRpc, Focusable, BlurNotifier, FocusNotifier {
	
	/**
	 * This handler must be implemented 
	 *
	 */
	public interface SuggestionHandler<T> extends Serializable {
		
		/**
		 * Provide suggestions based on query string
		 * @param query Search string
		 * @return list of Items
		 */
		public List<T> searchItems(String query);
	}
	
	public interface NewItemsHandler<T> extends Serializable {
		/**
		 * Provide new suggestion based on newItemText
		 * @param newItemText typed by user
		 * @return new Item
		 */
		public T addNewItem(String newItemText);
	}
	
	public interface SuggestionConverter<T> extends Serializable {
		
		public SuggestFieldSuggestion toSuggestion(T item);
		
		public T toItem(SuggestFieldSuggestion suggestion);
		
	}
	
	public interface TokenHandler<T> extends Serializable {
		public void handleToken(T token);
	}
	
	FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {

		private static final long serialVersionUID = -780524775769549747L;

		@Override
		protected void fireEvent(Event event) {
			SuggestField.this.fireEvent(event);
		}
	};
	
	//private SuggestionConverter<String> suggestionConverter = new StringSuggestionConverter();
	// TODO: Add default Converter
	private SuggestionConverter<T> suggestionConverter;
	private SuggestionHandler<T> suggestionHandler;
	private NewItemsHandler<T> newItemsHandler;
	private TokenHandler<T> tokenHandler;

	public SuggestField() {
		registerRpc(this, SuggestFieldServerRpc.class);
		registerRpc(focusBlurRpc);
	}

	public SuggestField( SuggestionConverter<T> suggestionConverter )
	{
		super();
		
		this.suggestionConverter = suggestionConverter;
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
			List<T> searchResult = suggestionHandler.searchItems(query);
			if (searchResult == null) {
				searchResult = new ArrayList<T>();
			}
			for (T result : searchResult) {
				suggestions.add(suggestionConverter.toSuggestion(result));
			}
		}
		getRpcProxy(SuggestFieldClientRpc.class).setSuggusetion(suggestions);
	}

	@Override
	public void onSuggestionSelected(SuggestFieldSuggestion suggestion) {
		if (suggestionConverter != null) {
			if (getTokenMode() && tokenHandler != null) {
				tokenHandler.handleToken(suggestionConverter.toItem(suggestion));
			} else {
				setValue(suggestionConverter.toItem(suggestion), true);
			}
		}
	}
	
	@Override
	public void addNewSuggestion(String suggestion) {
		if (getNewItemsHandler() != null) {
			if (getTokenMode() && tokenHandler != null) {
				tokenHandler.handleToken(newItemsHandler.addNewItem(suggestion));
			} else {
				setValue(newItemsHandler.addNewItem(suggestion));
			}
		}
	}
	
	@Override
	protected void doSetValue(T newValue) {
		/*
		 * This allows for a new value to be sent to client if setValue was called before selecting suggestion
		 * This must be here for BeanFieldGroup to work.
		 */
		if (suggestionConverter != null) {
			//getRpcProxy(SuggestFieldClientRpc.class).setCurrentSuggusetion(suggestionConverter.toSuggestion(newValue));
			getState().value = suggestionConverter.toSuggestion(newValue);
			/*
			 * Immediate clear if value is null
			 */
			if (newValue == null) {
				getRpcProxy(SuggestFieldClientRpc.class).clearValueImmediate();
			}
		}
	}
	
	/** Returns the currently set value
	 */
	@Override
	public T getValue()
	{
		return suggestionConverter.toItem(getState().value);
	}

	public void setDelay(int delayMillis) {
		getState().delayMillis = delayMillis;
	}
	
	public void setTokenMode(boolean tokenMode) {
		getState().tokenMode = tokenMode;
	}
	
	public boolean getTokenMode() {
		return getState().tokenMode;
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
	
	/**
	 * Set width of popup. Width must be in <code>px</code>. For auto-width set <code>0</code> value.
	 * @param width Popup Width in px
	 */
	public void setPopupWidth(int width) {
		if (width == 0) {
			getState().popupWidth = null;
		} else {
			getState().popupWidth = width + "px";
		}
	}
	
	/**
	 * Return current popup width.
	 * @return <code>null</code> if is auto width.
	 */
	public String getPopupWidth() {
		return getState().popupWidth;
	}
	
	/**
     * Does the select allow adding new options by the user.
     * 
     * @return True if additions are allowed.
     */
    public boolean isNewItemsAllowed() {
        return getState().allowNewItem;
    }

    /**
     * Enables or disables possibility to add new items by the user.
     * 
     * @param allowNewItems <code>true</code> or <code>false</code>
     * 
     */
    public void setNewItemsAllowed(boolean allowNewItems) {
    	getState().allowNewItem = allowNewItems;
    }
    
    /**
     * Set ShortCut keys combination to be handled on client side. <br>
     * ShortCut has same effect as Enter of Tab key. <br>
     * To disable ShorCut set values <code>-1, new int[0]</code> <br>
     * Example <br>
     * <code>setShortCut(ShortcutAction.KeyCode.S, new int[] { ShortcutAction.ModifierKey.CTRL })</code>
     * 
     * @param keyCode
     * @param modifierKeys
     */
    public void setShortCut(int keyCode, int... modifierKeys) {
    	getState().keyCode = keyCode;
    	getState().modifierKeys = modifierKeys;
    }
	
	@Override
	public void focus() {
		super.focus();
	}
    
    public void setSuggestionHandler(SuggestionHandler<T> suggestionHandler) {
    	this.suggestionHandler = suggestionHandler;
    }

	@Override
	public Registration addFocusListener(FocusListener listener) {
		return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
				FocusListener.focusMethod);
	}

	@Override
	public Registration addBlurListener(BlurListener listener) {
		return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
				BlurListener.blurMethod);
		
	}
	
	public SuggestionConverter<T> getSuggestionConverter() {
		return suggestionConverter;
	}

	public void setSuggestionConverter(SuggestionConverter<T> suggestionConverter) {
		this.suggestionConverter = suggestionConverter;
	}

	public NewItemsHandler<T> getNewItemsHandler() {
		return newItemsHandler;
	}

	public void setNewItemsHandler(NewItemsHandler<T> newItemsHandler) {
		this.newItemsHandler = newItemsHandler;
	}

	public TokenHandler<T> getTokenHandler() {
		return tokenHandler;
	}

	public void setTokenHandler(TokenHandler<T> tokenHandler) {
		this.tokenHandler = tokenHandler;
	}

	

	/** Creates a SuggestField of Type String with the default StringSuggestionConverter set.
	 * This replaces the default behaviour of the old constructor before Vaadin 8 version.
	 * 
	 * @return
	 */
	public static SuggestField<String> createDefault()
	{
		SuggestField<String> field = new SuggestField<>();
		field.setSuggestionConverter(new StringSuggestionConverter());
		
		return field;
	}
}