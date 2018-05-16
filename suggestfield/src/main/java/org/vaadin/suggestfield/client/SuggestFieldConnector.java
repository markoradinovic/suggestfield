package org.vaadin.suggestfield.client;

import java.io.Serializable;
import java.util.List;

import org.vaadin.suggestfield.SuggestField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.vaadin.client.EventHelper;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;

@Connect(SuggestField.class)
@SuppressWarnings("serial")
public class SuggestFieldConnector<T extends Serializable> extends AbstractFieldConnector implements
		VSuggestField.FindSuggestionsListener, SelectionHandler<Suggestion>,
		SuggestFieldClientRpc, FocusHandler, BlurHandler {

	private final SuggestFieldServerRpc serverRpc = RpcProxy.create(
			SuggestFieldServerRpc.class, this);

	private HandlerRegistration focusHandlerRegistration;
	private HandlerRegistration blurHandlerRegistration;

	public SuggestFieldConnector() {
		getWidget().setFindSuggestionsListener(this);
		getWidget().addSelectionHandler(this);
		registerRpc(SuggestFieldClientRpc.class, this);
	}

	@Override
	protected VSuggestField createWidget() {
		return GWT.create(VSuggestField.class);
	}

	@Override
	public VSuggestField getWidget() {
		return (VSuggestField) super.getWidget();
	}

	@Override
	public SuggestFieldState getState() {
		return (SuggestFieldState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		getWidget().client = getConnection();
		getWidget().setPlaceholder(getState().placeHolder);
		getWidget().trimQuery = getState().trimQuery;
		getWidget().minimumQueryCharacters = getState().minimumQueryCharacters;
		getWidget().popupWidth = getState().popupWidth;
		getWidget().allowNewItem = getState().allowNewItem;

		if (getState().readOnly || !getState().enabled) {
			getWidget().setEnabled(false);
		} else {
			getWidget().setEnabled(true);
		}
		
		getWidget().keyCode = getState().keyCode;
		getWidget().modifierKeys = getState().modifierKeys;
		
		focusHandlerRegistration = EventHelper.updateFocusHandler(this,
				focusHandlerRegistration);
		blurHandlerRegistration = EventHelper.updateBlurHandler(this,
				blurHandlerRegistration);
		if (!getState().tokenMode) {
			getWidget().setCurrentSuggestion(getState().fieldSuggestion);
		} else {
			getWidget().setCurrentSuggestion(null);			
		}
		getWidget().tokenMode = getState().tokenMode;
		
	}

	@Override
	public void findSuggestions(String query) {
		serverRpc.searchSuggestions(query);
	}
	
	@Override
	public void addNewSuggestion(String suggestion) {
		serverRpc.addNewSuggestion(suggestion);
		if (getWidget().tokenMode) {
			getWidget().setCurrentSuggestion(null);
		}
	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		SuggestFieldSuggestion suggestion = ((OracleSuggestionImpl) event
				.getSelectedItem()).getWrappedSuggestion();
		serverRpc.onSuggestionSelected(suggestion);
		if (getWidget().tokenMode) {
			getWidget().setCurrentSuggestion(null);
		} else {
			getWidget().setCurrentSuggestion(suggestion);
		}
	}

//	@Override
//	public void setCurrentSuggusetion(SuggestFieldSuggestion suggestion) {
//		getWidget().setCurrentSuggestion(suggestion);
//
//	}

	@Override
	public void onBlur(BlurEvent event) {
		getRpcProxy(FocusAndBlurServerRpc.class).blur();

	}

	@Override
	public void onFocus(FocusEvent event) {
		getRpcProxy(FocusAndBlurServerRpc.class).focus();

	}

	@Override
	public void setSuggusetion(List<SuggestFieldSuggestion> suggestions) {
		getWidget().setSuggestions(suggestions);

	}

	@Override
	public void clearValueImmediate() {
		getWidget().setCurrentSuggestion(null);
		
	}

	

}