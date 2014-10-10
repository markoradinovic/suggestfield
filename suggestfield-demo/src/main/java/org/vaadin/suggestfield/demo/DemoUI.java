package org.vaadin.suggestfield.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.vaadin.suggestfield.BeanSuggestionConverter;
import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@Theme("valo")
@Title("SuggestField Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.suggestfield.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	private final SuggestField search1 = new SuggestField();

	@Override
	protected void init(VaadinRequest request) {

		buildItems();
		
		final Button btnEnabled = new Button("Set enabled/disabled");
		btnEnabled.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				search1.setEnabled(!search1.isEnabled());
				
			}
		});
		final Button btnSetValue = new Button("Set field value");
		btnSetValue.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				search1.setValue(items.get(58));
				
			}
		});
		final Button btnGetValue = new Button("Get field value");
		btnGetValue.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Value" + search1.getValue());
				
			}
		});
		

		search1.setInputPrompt("Type country name");
		search1.setEnabled(true);
		search1.setWidth("100%");
		search1.addFocusListener(new FocusListener() {

			@Override
			public void focus(FocusEvent event) {
				System.out.println("Focus event");

			}
		});

		search1.addBlurListener(new BlurListener() {

			@Override
			public void blur(BlurEvent event) {
				System.out.println("Blur event");
			}
		});

		// Show it in the middle of the screen
		final VerticalLayout layout = new VerticalLayout();
		layout.setStyleName("demoContentLayout");
		layout.setWidth("400px");
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.addComponent(search1);
		
		layout.addComponent(btnEnabled);
		layout.addComponent(btnSetValue);
		layout.addComponent(btnGetValue);

		setContent(layout);

		setUpAutocomplete(search1);
	}

	private void setUpAutocomplete(final SuggestField search) {
		
		search.setSuggestionHandler(new SuggestionHandler() {

			@Override
			public List<Object> searchItems(String query) {
				System.out.println("Query: " + query);
				return handleSearchQuery(query);
			}
		});
		
		search.setSuggestionConverter(new CountrySuggestionConverter());

		search.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Notification.show("Selected " + search.getValue());

			}
		});

	}
	
	private class CountrySuggestionConverter extends BeanSuggestionConverter {

		public CountrySuggestionConverter() {
			super(CountryBean.class, "id", "name", "name");
		}

		@Override
		public Object toItem(SuggestFieldSuggestion suggestion) {
			CountryBean result = null;
			for (CountryBean bean : items) {
				if (bean.getId().toString().equals(suggestion.getId())) {
					result = bean;
					break;
				}
			}
			assert result != null : "This should not be happening";
			return result;
		}
		
	}

	protected void handleSuggestionSelection(Integer suggestion) {
		Notification.show("Selected " + suggestion);
	}

	private List<Object> handleSearchQuery(String query) {
		if ("".equals(query) || query == null) {
			return Collections.emptyList();
		}
		List<CountryBean> result = new ArrayList<CountryBean>();

		for (CountryBean country : items) {
			if (country.getName().toLowerCase().startsWith(query.toLowerCase())) {
				result.add(country);
			}
		}
		System.out.println("Total: " +  result.size());

		return new ArrayList<Object>(result);
	}

	private List<CountryBean> items = new ArrayList<DemoUI.CountryBean>();

	public static class CountryBean implements Serializable {
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

	private void buildItems() {
		long id = 0;
		// @formatter:off
		items.addAll(Arrays
				.asList(new CountryBean(id++, "Afghanistan"),
						new CountryBean(id++, "Albania"),
						new CountryBean(id++, "Algeria"),
						new CountryBean(id++, "Andorra"),
						new CountryBean(id++, "Angola"),
						new CountryBean(id++, "Antigua and Barbuda"),
						new CountryBean(id++, "Argentina"),
						new CountryBean(id++, "Armenia"),
						new CountryBean(id++, "Australia"),
						new CountryBean(id++, "Austria"),
						new CountryBean(id++, "Azerbaijan"),
						new CountryBean(id++, "Bahamas"),
						new CountryBean(id++, "Bahrain"),
						new CountryBean(id++, "Bangladesh"),
						new CountryBean(id++, "Barbados"),
						new CountryBean(id++, "Belarus"),
						new CountryBean(id++, "Belgium"),
						new CountryBean(id++, "Belize"),
						new CountryBean(id++, "Benin"),
						new CountryBean(id++, "Bhutan"),
						new CountryBean(id++, "Bolivia"),
						new CountryBean(id++, "Bosnia and Herzegovina"),
						new CountryBean(id++, "Botswana"),
						new CountryBean(id++, "Brazil"),
						new CountryBean(id++, "Brunei Darussalam"),
						new CountryBean(id++, "Bulgaria"),
						new CountryBean(id++, "Burkina Faso"),
						new CountryBean(id++, "Burundi"),
						new CountryBean(id++, "Cabo Verde"),
						new CountryBean(id++, "Cambodia"),
						new CountryBean(id++, "Cameroon"),
						new CountryBean(id++, "Canada"),
						new CountryBean(id++, "Central African Republic"),
						new CountryBean(id++, "Chad"),
						new CountryBean(id++, "Chile"),
						new CountryBean(id++, "China"),
						new CountryBean(id++, "Colombia"),
						new CountryBean(id++, "Comoros"),
						new CountryBean(id++, "Congo"),
						new CountryBean(id++,
								"Democratic Republic of the Congo"),
						new CountryBean(id++, "Costa Rica"),
						new CountryBean(id++, "Côte d'Ivoire"),
						new CountryBean(id++, "Croatia"),
						new CountryBean(id++, "Cuba"),
						new CountryBean(id++, "Cyprus"),
						new CountryBean(id++, "Czech Republic"),
						new CountryBean(id++, "Denmark"),
						new CountryBean(id++, "Djibouti"),
						new CountryBean(id++, "Dominica"),
						new CountryBean(id++, "Dominican Republic"),
						new CountryBean(id++, "Ecuador"),
						new CountryBean(id++, "Egypt"),
						new CountryBean(id++, "El Salvador"),
						new CountryBean(id++, "Equatorial Guinea"),
						new CountryBean(id++, "Eritrea"),
						new CountryBean(id++, "Estonia"),
						new CountryBean(id++, "Ethiopia"),
						new CountryBean(id++, "Fiji"),
						new CountryBean(id++, "Finland"),
						new CountryBean(id++, "France"),
						new CountryBean(id++, "Gabon"),
						new CountryBean(id++, "Gambia"),
						new CountryBean(id++, "Georgia"),
						new CountryBean(id++, "Germany"),
						new CountryBean(id++, "Ghana"),
						new CountryBean(id++, "Greece"),
						new CountryBean(id++, "Grenada"),
						new CountryBean(id++, "Guatemala"),
						new CountryBean(id++, "Guinea"),
						new CountryBean(id++, "Guinea-Bissau"),
						new CountryBean(id++, "Guyana"),
						new CountryBean(id++, "Haiti"),
						new CountryBean(id++, "Honduras"),
						new CountryBean(id++, "Hungary"),
						new CountryBean(id++, "Iceland"),
						new CountryBean(id++, "India"),
						new CountryBean(id++, "Indonesia"),
						new CountryBean(id++, "Iran (Islamic Republic of)"),
						new CountryBean(id++, "Iraq"),
						new CountryBean(id++, "Ireland"),
						new CountryBean(id++, "Israel"),
						new CountryBean(id++, "Italy"),
						new CountryBean(id++, "Jamaica"),
						new CountryBean(id++, "Japan"),
						new CountryBean(id++, "Jordan"),
						new CountryBean(id++, "Kazakhstan"),
						new CountryBean(id++, "Kenya"),
						new CountryBean(id++, "Kiribati"),
						new CountryBean(id++,
								"Democratic People's Republic of Korea"),
						new CountryBean(id++, "Republic of Korea"),
						new CountryBean(id++, "Kuwait"),
						new CountryBean(id++, "Kyrgyzstan"),
						new CountryBean(id++,
								"Lao People's Democratic Republic"),
						new CountryBean(id++, "Latvia"),
						new CountryBean(id++, "Lebanon"),
						new CountryBean(id++, "Lesotho"),
						new CountryBean(id++, "Liberia"),
						new CountryBean(id++, "Libya"),
						new CountryBean(id++, "Liechtenstein"),
						new CountryBean(id++, "Lithuania"),
						new CountryBean(id++, "Luxembourg"),
						new CountryBean(id++,
								"The former Yugoslav Republic of Macedonia"),
						new CountryBean(id++, "Madagascar"),
						new CountryBean(id++, "Malawi"),
						new CountryBean(id++, "Malaysia"),
						new CountryBean(id++, "Maldives"),
						new CountryBean(id++, "Mali"),
						new CountryBean(id++, "Malta"),
						new CountryBean(id++, "Marshall Islands"),
						new CountryBean(id++, "Mauritania"),
						new CountryBean(id++, "Mauritius"),
						new CountryBean(id++, "Mexico"),
						new CountryBean(id++,
								"Micronesia (Federated States of)"),
						new CountryBean(id++, "Republic of Moldova"),
						new CountryBean(id++, "Monaco"),
						new CountryBean(id++, "Mongolia"),
						new CountryBean(id++, "Montenegro"),
						new CountryBean(id++, "Morocco"),
						new CountryBean(id++, "Mozambique"),
						new CountryBean(id++, "Myanmar"),
						new CountryBean(id++, "Namibia"),
						new CountryBean(id++, "Nauru"),
						new CountryBean(id++, "Nepal"),
						new CountryBean(id++, "Netherlands"),
						new CountryBean(id++, "New Zealand"),
						new CountryBean(id++, "Nicaragua"),
						new CountryBean(id++, "Niger"),
						new CountryBean(id++, "Nigeria"),
						new CountryBean(id++, "Norway"),
						new CountryBean(id++, "Oman"),
						new CountryBean(id++, "Pakistan"),
						new CountryBean(id++, "Palau"),
						new CountryBean(id++, "Panama"),
						new CountryBean(id++, "Papua New Guinea"),
						new CountryBean(id++, "Paraguay"),
						new CountryBean(id++, "Peru"),
						new CountryBean(id++, "Philippines"),
						new CountryBean(id++, "Poland"),
						new CountryBean(id++, "Portugal"),
						new CountryBean(id++, "Qatar"),
						new CountryBean(id++, "Romania"),
						new CountryBean(id++, "Russian Federation"),
						new CountryBean(id++, "Rwanda"),
						new CountryBean(id++, "Saint Kitts and Nevis"),
						new CountryBean(id++, "Saint Lucia"),
						new CountryBean(id++,
								"Saint Vincent and the Grenadines"),
						new CountryBean(id++, "Samoa"),
						new CountryBean(id++, "San Marino"),
						new CountryBean(id++, "Sao Tome and Principe"),
						new CountryBean(id++, "Saudi Arabia"),
						new CountryBean(id++, "Senegal"),
						new CountryBean(id++, "Serbia"),
						new CountryBean(id++, "Seychelles"),
						new CountryBean(id++, "Sierra Leone"),
						new CountryBean(id++, "Singapore"),
						new CountryBean(id++, "Slovakia"),
						new CountryBean(id++, "Slovenia"),
						new CountryBean(id++, "Solomon Islands"),
						new CountryBean(id++, "Somalia"),
						new CountryBean(id++, "South Africa"),
						new CountryBean(id++, "South Sudan"),
						new CountryBean(id++, "Spain"),
						new CountryBean(id++, "Sri Lanka"),
						new CountryBean(id++, "Sudan"),
						new CountryBean(id++, "Suriname"),
						new CountryBean(id++, "Swaziland"),
						new CountryBean(id++, "Sweden"),
						new CountryBean(id++, "Switzerland"),
						new CountryBean(id++, "Syrian Arab Republic"),
						new CountryBean(id++, "Tajikistan"),
						new CountryBean(id++, "United Republic of Tanzania"),
						new CountryBean(id++, "Thailand"),
						new CountryBean(id++, "Timor-Leste"),
						new CountryBean(id++, "Togo"),
						new CountryBean(id++, "Tonga"),
						new CountryBean(id++, "Trinidad and Tobago"),
						new CountryBean(id++, "Tunisia"),
						new CountryBean(id++, "Turkey"),
						new CountryBean(id++, "Turkmenistan"),
						new CountryBean(id++, "Tuvalu"),
						new CountryBean(id++, "Uganda"),
						new CountryBean(id++, "Ukraine"),
						new CountryBean(id++, "United Arab Emirates"),
						new CountryBean(id++,
								"United Kingdom of Great Britain and Northern Ireland"),
						new CountryBean(id++, "United States of America"),
						new CountryBean(id++, "Uruguay"), new CountryBean(id++,
								"Uzbekistan"),
						new CountryBean(id++, "Vanuatu"), new CountryBean(id++,
								"Venezuela (Bolivarian Republic of)"),
						new CountryBean(id++, "Viet Nam"), new CountryBean(
								id++, "Yemen"),
						new CountryBean(id++, "Zambia"), new CountryBean(id++,
								"Zimbabwe")));
	}

}
