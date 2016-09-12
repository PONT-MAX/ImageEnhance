package com.softa.imageenhancer;

import android.content.Context;
import android.graphics.Bitmap;

public interface ImageEnhancer {
	
	public Bitmap enhanceImage(Bitmap bitmap, int configuration, Context context);
	public int getProgress();
	public String[] getConfigurationOptions();

}
