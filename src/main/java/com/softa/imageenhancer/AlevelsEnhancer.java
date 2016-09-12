package com.softa.imageenhancer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class AlevelsEnhancer implements ImageEnhancer {

	private int progress;

	public AlevelsEnhancer() {

	}

	// Att göra: testa Texten, fixa någon start i histeq att testa

	public Bitmap enhanceImageHSV(Bitmap theImage, int action) {

		// Set progress HEJ
		// action = vilken kanal som ska jobbas i
		progress = 0;
		
		// Get the image pixels
		int height = theImage.getHeight();
		int width = theImage.getWidth();
		Log.d("DEBUG", "Image size is " + width + "px by " + height + "px." );
		int[] pixels = new int[height * width];
		theImage.getPixels(pixels, 0, width,0,0, width, height);
		
		progress = 5;

		Log.d("DEBUG", "pixels length = " + pixels.length);
		
		//Convert pixels to brightness values;
		float[][] hsvPixels = convertToHSV(pixels);
		
		progress = 40;
		
		Log.d("DEBUG", "hsvPixels length = " + hsvPixels.length);

		// Here below some manipulations of the image is made as examples.
		// This should be changed to your image enhancement algorithms.



		Log.d("DEBUG", "al gamma = " + action);
		auto_levels(hsvPixels, pixels, action);


		progress = 80;
		Log.d("DEBUG","creating BITMAP,width x height "+width+" "+height);
        Bitmap modifiedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);

		progress = 100;
		return modifiedImage;
	}


	public void auto_levels(float[][] hsvPixels, int[] pixels, int bins) {

		//Histogram
		int[] hist_array = make_histogram(hsvPixels);

		// Total intensitet i bilden
		int sum_intensity = 0;
		for (int i = 0 ; i < hist_array.length; ++i ) {
			sum_intensity += hist_array[i];
		}

		// Find lv
		int lv = 0;
		int lv_sum = hist_array[0];
		int limit = sum_intensity/1000;
		while(lv_sum < limit){
			++lv;
			lv_sum += hist_array[lv];
		}
		Log.d("DEBUG", "AL: lv = " + lv);

		// Find hv
		int hv = 255;
		int hv_sum = hist_array[255];
		limit = sum_intensity/1000;
		while(hv_sum < limit){
			--hv;
			hv_sum += hist_array[hv];
		}

		progress = 50;
		Log.d("DEBUG", "AL: hv = " + hv);

		int[] T = new int[256];
		float k = (float)255/(float)(hv-lv);
		Log.d("DEBUG", "AL: k = " + k);
		int m = - Math.round(k*(float)lv);
		Log.d("DEBUG", "AL: m = " + m);

		for(int i = lv; i < hv; ++i){
			T[i] = Math.round(k*(float)i + (float)m);
		}
		Log.d("DEBUG", "AL: F1-done");

		for(int i = hv; i < 256; ++i){
			T[i] = 255;
		}
		Log.d("DEBUG", "AL: F2-done");
		progress = 55;
		// Transformera bilden utjämna kontrasten med linjär transformation
		Log.d("DEBUG", "AL: Tranformerar bild steg 1");
		float old_i,new_i;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = T[ Math.round( old_i*(float)255 ) ]/(float)255;
			hsvPixels[i][2] = new_i;

		}

		progress = 60;
		// Fas två interpolera gamma-kurva
		Log.d("DEBUG", "AL: Histogram 2");
		hist_array = make_histogram(hsvPixels);

		// Total intensitet i bilden
		sum_intensity = 0;
		for (int i = 0 ; i < hist_array.length; ++i ) {
			sum_intensity += hist_array[i];
		}



		int sum_bins = 0;
		int edge_i = 0;

		int[] edge_x = new int[bins-1];

		Log.d("DEBUG", "AL: Kanter");

		for (int j = 1 ; j < bins; ++j){

			while(sum_bins < (sum_intensity*j/bins)){
				sum_bins += hist_array[edge_i];
				++edge_i;
			}

			edge_x[j-1] = edge_i;

		}

		//
		// Hitta rätt gamma genom att minimera felfunktionen
		double gamma = 1,dx,bdd = (double)255;

		int curve_side=0;

		int start = 0,stop = edge_x.length;

		if(edge_x.length > 10){ // är bara intresserad i mitten av kurvan
			start = edge_x.length/3;
			stop = edge_x.length - start;
		}


		for(int i = start; i < stop; ++i){
			curve_side += Math.round( edge_x[i] - (float)255/bins*(float)(i+1)  );
		}
		Log.d("DEBUG", "AL:curve_size :" + curve_side);

		// Tar reda på om gamma är <|> 1
		if(curve_side > 0) { // gamma > 1
			dx = 0.01;
			Log.d("DEBUG", "AL:gamma > 1");
		}
		else { //gamma < 1
			dx = -0.01;
			Log.d("DEBUG", "AL:gamma < 1");
		}


		float bdf = 255, binsf = (float)bins,last_errro=0,error=0;
		float[] edge_y = new float[edge_x.length];
		float[] gamma_y = new float[edge_x.length];
		float[] gamma_x = new float[edge_x.length];

		for(int i = 0; i < edge_x.length; ++i){
			edge_y[i] = bdf/binsf*(float)(i+1);
		}



		for(int i = start; i < stop; ++i) {
			gamma_y[i] = bdf*(float)(Math.pow((double)edge_x[i]/bdd, gamma));
			gamma_x[i] = bdf*(float)(Math.pow((double)edge_y[i]/bdd, 1/gamma));
			last_errro += Math.abs( edge_y[i] - gamma_y[i]) + Math.abs( edge_y[i] - gamma_y[i]);
		}
		gamma += dx;
		for(int i = start; i < stop; ++i) {
			gamma_y[i] = bdf*(float)(Math.pow((double)edge_x[i]/bdd, gamma));
			gamma_x[i] = bdf*(float)(Math.pow((double)edge_y[i]/bdd, 1/gamma));
			error += Math.abs( edge_y[i] - gamma_y[i]) + Math.abs( edge_y[i] - gamma_y[i]);
		}

		// Interpolerar genom att minimera felfunktionen Manhattan norm
		while(error < last_errro) {
			gamma += dx;
			last_errro = error;
			error = 0;
			for(int i = start; i < stop; ++i) {
				gamma_y[i] = bdf*(float)(Math.pow((double)edge_x[i]/bdd, gamma));
				gamma_x[i] = bdf*(float)(Math.pow((double)edge_y[i]/bdd, 1/gamma));
				error += Math.abs( edge_y[i] - gamma_y[i]) + Math.abs( edge_y[i] - gamma_y[i]);
			}
		}
		gamma -= dx;

		// Minskar gamma lite då felfunktionen i vissa fall ger extrema gamma
		if(gamma > 1){
			gamma = (gamma -1.0)/2.0 + 1.0;
		}
		else{
			gamma = 1.0 - (1.0-gamma)/2.0;
		}
		progress = 80;
		Log.d("DEBUG", "A-Levels: Gamma: " + gamma);
		for(int x = 0; x <= 255; ++x){

			T[x] = Math.round(bdf*(float)(Math.pow((double)x/bdd,gamma)));

		}

		progress = 90;
		Log.d("DEBUG", "A-Levels: Transformera tillbaka bilden");
		// Transformera bilden
		//float old_i,new_i;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = T[ Math.round( old_i*bdf ) ]/bdf;
			hsvPixels[i][2] = new_i;

			//Konvertera tillbaka till RGB
			pixels[i] = Color.HSVToColor(hsvPixels[i]);
		}


	}

	public int[] make_histogram(float[][] hsvPixels){

		int[] hist_array = new int[256];
		int intens;
		for (int i = 0; i < hsvPixels.length; i++) {
			intens = Math.round( hsvPixels[i][2]*255 );

			hist_array[ intens ] += 1;

		}

		return hist_array;

	}

	private float[][] convertToHSV(int[] pixels) {
		float[][] hsvPixels = new float[pixels.length][3];
		for (int i = 0; i < pixels.length; i++) {
			Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvPixels[i]);
			
		}
		return hsvPixels;
	}

	public int getProgress() {
		// Log.d("DEBUG", "Progress: "+progress);
		return progress;
	}

	@Override
	public Bitmap enhanceImage(Bitmap bitmap, int configuration, Context context) {

			return enhanceImageHSV(bitmap, configuration);

	}

	@Override
	public String[] getConfigurationOptions() {
		return new String[]{ "Auto Levels"};
	}

}
