package org.vaadin.suggestfield.client;

import java.io.Serializable;

public class SuggestFieldSuggestion implements Serializable {

	private static final long serialVersionUID = -2477614164160415074L;
	
	private String id;
	private String displayString;
	private String replacementString;

	public SuggestFieldSuggestion() {
	}

	public SuggestFieldSuggestion(String id, String displayString,
			String replacementString) {
		super();
		this.id = id;
		this.displayString = displayString;
		this.replacementString = replacementString;
	}

	public String getDisplayString() {
		return displayString;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public String getReplacementString() {
		return replacementString;
	}

	public void setReplacementString(String replacementString) {
		this.replacementString = replacementString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuggestFieldSuggestion other = (SuggestFieldSuggestion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}