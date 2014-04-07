package de.dhbw.studientag.model;

import java.util.HashMap;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Color extends android.graphics.drawable.ColorDrawable implements
		Parcelable {

	private enum MyColors {

		GREEN("258039"), RED("b12c37"), YELLOW("f9a10d"), BLUE("548ed2"), PURPLE(
				"605ab2");
		private String colorString;

		MyColors(String colorString) {
			this.colorString = "#" + colorString;
		}

		public String getColorString() {
			return colorString;
		}

	};

	private int mColor;

	public Color() {

	}

	public Color(String colorString) throws IllegalArgumentException {
		int color = android.graphics.Color.WHITE;
		try {
			colorString = MyColors.valueOf(colorString.toUpperCase(Locale.GERMAN)).getColorString();

		} catch (IllegalArgumentException ex) {
			try {
				colorString = sColorNameMap.get(colorString).getColorString();
			} catch (NullPointerException nEx) {

			}

		}
		color = android.graphics.Color.parseColor(colorString);
		setColor(color);
		
	}

	@Override
	public int getColor() {
		return mColor;
	}
	
	@Override
	public void setColor(int color){
		this.mColor=color;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mColor);

	}

	private Color(Parcel source) {
		this();
		this.mColor = source.readInt();

	}

	public static final Parcelable.Creator<Color> CREATOR = new Parcelable.Creator<Color>() {
		public Color createFromParcel(Parcel in) {
			return new Color(in);
		}

		public Color[] newArray(int size) {
			return new Color[size];
		}
	};

	private static final HashMap<String, MyColors> sColorNameMap;

	static {
		sColorNameMap = new HashMap<String, MyColors>();
		sColorNameMap.put("grün", MyColors.GREEN);
		sColorNameMap.put("rot", MyColors.RED);
		sColorNameMap.put("gelb", MyColors.YELLOW);
		sColorNameMap.put("gruen", MyColors.GREEN);
		sColorNameMap.put("blau", MyColors.BLUE);
		sColorNameMap.put("lila", MyColors.PURPLE);

	}

}
