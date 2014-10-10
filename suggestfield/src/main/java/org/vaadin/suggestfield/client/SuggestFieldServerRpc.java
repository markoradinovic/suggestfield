package org.vaadin.suggestfield.client;

import com.vaadin.shared.communication.ServerRpc;

public interface SuggestFieldServerRpc extends ServerRpc {
	
	public void searchSuggestions(String query);

	public void onSuggestionSelected(SuggestFieldSuggestion suggestion);
}
