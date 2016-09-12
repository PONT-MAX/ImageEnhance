package com.softa.imageenhancer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import com.android.rs.histEqRs.ScriptC_histEQ;



public class HisteqEnhancer implements ImageEnhancer {

	private int progress;

	public HisteqEnhancer() {

	}

	// Att göra: testa Texten, fixa någon start i histeq att testa


	public Bitmap enhanceImageHSV(Bitmap Im_in, int action,Context context) {

		// Set progress
		// action = vilken kanal som ska jobbas i
		progress = 0;
		
		// Get the image pixels
		int height = Im_in.getHeight();
		int width = Im_in.getWidth();
		Log.d("DEBUG", "Image size is " + width + "px by " + height + "px." );

		//Create new bitmap
		Bitmap Im_out = Im_in.copy(Im_in.getConfig(), true);

		//Create renderscript
		RenderScript rs = RenderScript.create(context);
		progress = 5;
		Log.d("DEBUG", "Histogram EQ: Create renderscript");

		//Create allocation from Bitmap
		Allocation allocationA = Allocation.createFromBitmap(rs, Im_out);

		//Create allocation with same type
		Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

		progress = 10;
		Log.d("DEBUG", "Histogram EQ: Allocation done");

		//Create script from rs file.
		ScriptC_histEQ histEqScript = new ScriptC_histEQ(rs);

		//Set size in script
		histEqScript.set_size(width*height);

		//Call the first kernel.
		histEqScript.forEach_root(allocationA, allocationB);

		progress = 40;
		Log.d("DEBUG", "Histogram EQ: Transform to YUV And make hist array");

		//Call the rs method to compute the remap array
		histEqScript.invoke_createTransformFunctionArray();

		progress = 50;
		Log.d("DEBUG", "Histogram EQ: Create Transform array");

		//Call the second kernel
		histEqScript.forEach_intensTransToRGB(allocationB, allocationA);

		progress = 90;
		Log.d("DEBUG", "Histogram EQ: Transform and switch to RGB");

	//Copy script result into bitmap
		allocationA.copyTo(Im_out);


		progress = 99;
		//Destroy everything to free memory
		allocationA.destroy();
		allocationB.destroy();
		histEqScript.destroy();
		rs.destroy();






		progress = 100;
		return Im_out;
	}

//	public void hist_eq(float[][] hsvPixels, int[] pixels){
//
//		Log.d("DEBUG", "EQ: Skapa Histogram");
//		int[] hist_array = make_histogram(hsvPixels);
//		Log.d("DEBUG", "EQ: Skapa Histogram length = " + hist_array.length);
//		progress = 60;
//
//		float N = (float) hsvPixels.length;
//		float bitdepth = (float)255;
//		float scalar = bitdepth/N;
//
//		final float[] hist_array_cs = new float[256];
//		hist_array_cs[0] = (float)hist_array[0]*scalar;
//
//		// kumulativa fördelningsfunktionen
//		for (int i = 1; i < hist_array.length; i++) {
//			hist_array_cs[i] = (float)hist_array[i]*scalar + hist_array_cs[i-1];
//		}
//		progress = 70;
//
//		// Transformera tillbaka
//		float old_i,new_i,f255 = 255;
//		final int pl = hsvPixels.length/3;
//		final int pl2 = pl*2;
//		final int pl3 = hsvPixels.length;
//
//		final float[][] hsvPixelsT = hsvPixels;
//		final int[] pixelsT = pixels;
//		//Loop1
//		Thread t1 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				float old_i,new_i,f255 = 255;
//				System.out.println("1 loop start: TB");
//				for (int i = 0; i < pl; i++) {
//					old_i = hsvPixelsT[i][2];
//					new_i = hist_array_cs[ Math.round( old_i*f255 ) ]/f255;
//
//					hsvPixelsT[i][2] = new_i;
//
//					//Konvertera tillbaka till RGB
//					pixelsT[i] = Color.HSVToColor(hsvPixelsT[i]);
//				}
//			}
//		});
//
//		t1.setPriority(10);
//		t1.start();
//
//		//Loop1
//		Thread t2 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				float old_i,new_i,f255 = 255;
//				System.out.println("2 loop start: TB");
//				for (int i = pl; i < pl2; i++) {
//					old_i = hsvPixelsT[i][2];
//					new_i = hist_array_cs[ Math.round( old_i*f255 ) ]/f255;
//
//					hsvPixelsT[i][2] = new_i;
//
//					//Konvertera tillbaka till RGB
//					pixelsT[i] = Color.HSVToColor(hsvPixelsT[i]);
//				}
//			}
//		});
//
//		t2.setPriority(10);
//		t2.start();
//
//		//Loop1
//		Thread t3 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				float old_i,new_i,f255 = 255;
//				System.out.println("3 loop start: TB");
//				for (int i = pl2; i < pl3; i++) {
//					old_i = hsvPixelsT[i][2];
//					new_i = hist_array_cs[ Math.round( old_i*f255 ) ]/f255;
//
//					hsvPixelsT[i][2] = new_i;
//
//					//Konvertera tillbaka till RGB
//					pixelsT[i] = Color.HSVToColor(hsvPixelsT[i]);
//				}
//			}
//		});
//
//		t3.setPriority(10);
//		t3.start();
//
//		while(t1.isAlive() ||t2.isAlive()||t3.isAlive()){
//			continue;
//		}
//
//
//		pixels = pixelsT;
//		hsvPixels = hsvPixelsT;
//
//		progress = 95;
//
//	}



//	public int[] make_histogram(float[][] hsvPixels){
//
//		final int[] hist_array = new int[256];
//
//
//		final float[][] hsvPixelsT = hsvPixels;
//		final int pl = hsvPixels.length/3;
//		final int pl2 = pl*2;
//		final int pl3 = hsvPixels.length;
//
//		//Loop1
//		Thread t1 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				int intens;
//				System.out.println("1 loop start: Make hist");
//				for (int i = 0; i < pl; i++) {
//					intens = Math.round( hsvPixelsT[i][2]*255 );
//
//					hist_array[ intens ] += 1;
//				}
//			}
//		});
//
//		t1.setPriority(10);
//		t1.start();
//
//		//Loop2
//		Thread t2 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				int intens;
//				System.out.println("2 loop start: Make hist");
//				for (int i = pl; i < pl2; i++) {
//					intens = Math.round( hsvPixelsT[i][2]*255 );
//
//					hist_array[ intens ] += 1;
//				}
//			}
//		});
//
//		t2.setPriority(10);
//		t2.start();
//
//		//Loop2
//		Thread t3 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				int intens;
//				System.out.println("3 loop start: Make hist");
//				for (int i = pl2; i < pl3; i++) {
//					intens = Math.round( hsvPixelsT[i][2]*255 );
//
//					hist_array[ intens ] += 1;
//				}
//			}
//		});
//
//		t3.setPriority(10);
//		t3.start();
//
//		while(t1.isAlive() ||t2.isAlive()||t3.isAlive()){
//			continue;
//		}
//
//		return hist_array;
//
//	}





//	private float[][] convertToHSV(int[] pixels) {
//
//		final float[][] hsvPixels = new float[pixels.length][3];
//		final int[] pixelsth = pixels;
//
//		final int pl = pixelsth.length/3;
//		final int pl2 = pl*2;
//		final int pl3 = pixelsth.length;
//		//final int pl4 = pixelsth.length;
//
//		//Loop1
//		Thread t1 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				System.out.println("1 loop start");
//				for (int i = 0; i < pl; i++) {
//					Color.RGBToHSV(Color.red(pixelsth[i]), Color.green(pixelsth[i]), Color.blue(pixelsth[i]), hsvPixels[i]);
//
//				}
//			}
//		});
//
//		t1.setPriority(10);
//		t1.start();
//
//		//Loop2
//		Thread t2 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				System.out.println("2 loop start");
//				for (int i = pl; i < pl2; i++) {
//					Color.RGBToHSV(Color.red(pixelsth[i]), Color.green(pixelsth[i]), Color.blue(pixelsth[i]), hsvPixels[i]);
//
//				}
//			}
//		});
//
//		t2.setPriority(10);
//		t2.start();
//
//		//Loop1
//		Thread t3 = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				System.out.println("3 loop start");
//				for (int i = pl2; i < pl3; i++) {
//					Color.RGBToHSV(Color.red(pixelsth[i]), Color.green(pixelsth[i]), Color.blue(pixelsth[i]), hsvPixels[i]);
//
//				}
//			}
//		});
//
//		t3.setPriority(9);
//		t3.start();
//
//
//
//		while(t1.isAlive() || t2.isAlive() || t3.isAlive()){
//			continue;
//		}
//
//		return hsvPixels;
//	}






//
//	private float[][] convertToHSV2(int[] pixels) {
//		float[][] hsvPixels = new float[pixels.length][3];
//		for (int i = 0; i < pixels.length; i++) {
//			Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvPixels[i]);
//
//		}
//		return hsvPixels;
//	}

	public int getProgress() {
		// Log.d("DEBUG", "Progress: "+progress);
		return progress;
	}

	@Override
	public Bitmap enhanceImage(Bitmap bitmap, int configuration, Context context) {

			return enhanceImageHSV(bitmap, 0,context); //HistEQ

	}

	@Override
	public String[] getConfigurationOptions() {
		return new String[]{ "Histogram EQ"};
	}

}


//public class rgb2hsvThread extends Thread {
//
//	public float[][] run(int[] pixels){
//		System.out.println("MyThread running");
//		float[][] hsvPixels = new float[pixels.length][3];
//		for (int i = 0; i < pixels.length; i++) {
//
//
//		}
//		return Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvPixels[i]);;
//	}
//}