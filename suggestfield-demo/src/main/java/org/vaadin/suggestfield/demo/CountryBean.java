package org.vaadin.suggestfield.demo;

import java.io.Serializable;

public class CountryBean implements Serializable {
	private Long id;
	private String name;

	public CountryBean() {
		// TODO Auto-generated constructor stub
	}

	public CountryBean(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CountryBean [id=" + id + ", name=" + name + "]";
	}

}