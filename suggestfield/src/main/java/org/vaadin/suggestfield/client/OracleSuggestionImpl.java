package org.vaadin.suggestfield.client;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class OracleSuggestionImpl implements Suggestion {
  
  private final String displayString;
  private final String replacementString;
  private final SuggestFieldSuggestion wrappedSuggestion;
  
  public OracleSuggestionImpl(SuggestFieldSuggestion wrappedSuggestion) {
    this.wrappedSuggestion = wrappedSuggestion;
    this.displayString = wrappedSuggestion.getDisplayString();
    this.replacementString = wrappedSuggestion.getReplacementString();
  }

  @Override
  public String getDisplayString() {
    return displayString;
  }

  @Override
  public String getReplacementString() {
    return replacementString;
  }
  
  public SuggestFieldSuggestion getWrappedSuggestion() {
    return wrappedSuggestion;
  }

}
