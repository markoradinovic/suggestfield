package org.vaadin.suggestfield;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.vaadin.suggestfield.SuggestField.SuggestionConverter;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

/** Abstract base converter for beans
 * 
 * @author markoradinovic
 * @author pesse
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class BeanSuggestionConverter<T> implements SuggestionConverter<T> {

	private final Class<? extends Object> itemClass;
	private final String idPropertyName;
	private final String displayPropertyName;
	private final String replacementPropertyName;

	public BeanSuggestionConverter(Class<? extends T> itemClass,
			String idPropertyName, String displayPropertyName,
			String replacementPropertyName) {
		assert itemClass != null : "Item Class cannot be null";
		assert displayPropertyName != null : "Item display property name cannot be null";
		assert replacementPropertyName != null : "Item replacement property name cannot be null";
		this.itemClass = itemClass;
		this.idPropertyName = idPropertyName;
		this.displayPropertyName = displayPropertyName;
		this.replacementPropertyName = replacementPropertyName;

	}

	@Override
	public SuggestFieldSuggestion toSuggestion(T item) {
		if (item == null) {
			return new SuggestFieldSuggestion("-1", "", "");
		} else {
			return new SuggestFieldSuggestion(getItemPropertyValue(
					idPropertyName, item), getItemPropertyValue(
					displayPropertyName, item), getItemPropertyValue(
					replacementPropertyName, item));
		}
	}
	
	@Override
	public abstract T toItem(SuggestFieldSuggestion suggestion);
	
	private String getItemPropertyValue(String property, Object item) {
		try {
			Method getter = initGetterMethod(property, itemClass);
			Object value = getter.invoke(item);
			if (value != null) {
				return value.toString();
			} else {
				throw new IllegalArgumentException("Bean property '" + property
						+ "' cannot be null");
			}
		} catch (final java.lang.NoSuchMethodException e) {
			throw new IllegalArgumentException("Bean property '" + property
					+ "' not found", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Bean property '" + property
					+ "' not found", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Bean property '" + property
					+ "' not found", e);
		}
	}

	/**
	 * Find a getter method for a property (getXyz(), isXyz() or areXyz()).
	 * 
	 * @param propertyName
	 *            name of the property
	 * @param beanClass
	 *            class in which to look for the getter methods
	 * @return Method
	 * @throws NoSuchMethodException
	 *             if no getter found
	 */
	private static Method initGetterMethod(String propertyName,
			final Class<?> beanClass) throws NoSuchMethodException {
		propertyName = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);

		Method getMethod = null;
		try {
			getMethod = beanClass.getMethod("get" + propertyName,
					new Class[] {});
		} catch (final java.lang.NoSuchMethodException ignored) {
			try {
				getMethod = beanClass.getMethod("is" + propertyName,
						new Class[] {});
			} catch (final java.lang.NoSuchMethodException ignoredAsWell) {
				getMethod = beanClass.getMethod("are" + propertyName,
						new Class[] {});
			}
		}
		return getMethod;
	}

}
