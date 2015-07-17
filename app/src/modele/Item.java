/*
 * @author Oneltone Da Silva
 * @version 1 
 */

package modele;

import android.graphics.drawable.Drawable;

public class Item {

	public final String text;
	public final Drawable icon;

	public Item(String text, Drawable icon) {
		this.text = text;
		this.icon = icon;
	}

	@Override
	public String toString() {
		return text;
	}
}