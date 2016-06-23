package com.samdroid.resource;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import java.util.HashMap;

public class ColourUtils {
	
	public static final String Black = "10131a";
	public static final String White = "FFFFFF";
	public static final String Orange = "F39019";
	public static final String Blue = "51A7F9";		
	public static final String Green = "70BF41";
	public static final String Purple = "9B59B6";
	public static final String Yellow = "E6C821";
	public static final String Turquoise = "1ABC9C";
	public static final String Red = "EC5D57";
	public static final String LightGrey = "EBEBEB";
	public static final String Grey = "979797";
	public static final String Background = "D8D8D8";
	public static final String Primary = "6AC6B3";
	public static final String PrimaryDark = "5fb1a0";
	public static final String PrimaryLight = "96d7c9";

	public enum Colours {
		BLUE, RED, YELLOW, PURPLE, ORANGE, BLACK, WHITE, TURQUOISE, GREEN, LIGHT_GREY
	}

	public static HashMap<Colours, String> hexColours = new HashMap<Colours, String>();

	static {
		hexColours.put(Colours.BLACK, "10131a");
		hexColours.put(Colours.WHITE, "FFFFFF");
		hexColours.put(Colours.ORANGE, "F39019");
		hexColours.put(Colours.BLUE, "51A7F9");		
		hexColours.put(Colours.GREEN, "70BF41");
		hexColours.put(Colours.PURPLE, "9B59B6");
		hexColours.put(Colours.YELLOW, "E6C821");
		hexColours.put(Colours.TURQUOISE, "1ABC9C");
		hexColours.put(Colours.RED, "EC5D57");
		hexColours.put(Colours.LIGHT_GREY, "EBEBEB");
	}

	/**
	 * Get the hex string for a colour
	 * @param color
	 * @return
	 */
	public static String getHex (Colours color) {
		return hexColours.get(color);
	}

	public static int getColour (Context context, int resId) {
		if (context == null) return 0;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return context.getColor(resId);
		return context.getResources().getColor(resId);
	}

}