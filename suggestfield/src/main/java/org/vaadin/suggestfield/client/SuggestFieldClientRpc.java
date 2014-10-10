package org.vaadin.suggestfield.client;

import java.util.List;

import com.vaadin.shared.communication.ClientRpc;

public interface SuggestFieldClientRpc extends ClientRpc {

	public void setCurrentSuggusetion(SuggestFieldSuggestion suggestion);

	public void setSuggusetion(List<SuggestFieldSuggestion> suggestions);
}
