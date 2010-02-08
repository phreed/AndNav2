/**
 * 
 */
package org.andnav2.ui.common;

/**
 * @author Nicolas Gramlich
 * @since 17:28:08 - 01.06.2009
 */
public interface CommonCallback<T extends Object>{
	public void onSuccess(final T result);
	public void onFailure(final Throwable t);
}
