package com.samdroid.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.samdroid.string.Strings;

public class ImageUtils {

	protected static final String TAG = "ImageUtils";

	public enum Orientation {
		LANDSCAPE, PORTRAIT, SQUARE, UNKNOWN
	}

	/**
	 * Dimensions for the users images
	 */
	public static final int
	THUMBNAIL_DIM = 128, 
	PROFILE_DIM = 256, 
	COVER_DIM_WIDTH = 480,
	COVER_DIM_HEIGHT = 320;

	public static final int IMAGE_FADE_IN_TIME = 1000;

	/**
	 * Blur an bitmap
	 *  
	 * http://blog.neteril.org/blog/2013/08/12/blurring-images-on-android/
	 *  
	 * @param input Bitmap to blur
	 * @return the blurred Bitmap
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static Bitmap blurImage (Context context, Bitmap input) {		
		RenderScript rsScript = RenderScript.create(context);
		Allocation alloc = Allocation.createFromBitmap(rsScript, input);

		ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, alloc.getElement());
		blur.setRadius(14);
		blur.setInput(alloc);

		Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
		Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
		blur.forEach(outAlloc);
		outAlloc.copyTo(result);

		rsScript.destroy();
		return result;
	}

	public static Bitmap fastblur(Bitmap bitmap, int radius) {

		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmapCopy.getWidth();
		int h = bitmapCopy.getHeight();

		int[] pix = new int[w * h];
		bitmapCopy.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
		sir[2] = (p & 0x0000ff);
		rbs = r1 - Math.abs(i);
		rsum += sir[0] * rbs;
		gsum += sir[1] * rbs;
		bsum += sir[2] * rbs;
		if (i > 0) {
			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];
		} else {
			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];
		}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
			sir[2] = (p & 0x0000ff);

			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];

			rsum += rinsum;
			gsum += ginsum;
			bsum += binsum;

			stackpointer = (stackpointer + 1) % div;
			sir = stack[(stackpointer) % div];

			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];

			rinsum -= sir[0];
			ginsum -= sir[1];
			binsum -= sir[2];

			yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmapCopy.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmapCopy);
	}

	/**
	 * Scale a drawable to fit a certain height
	 * 
	 * @param context
	 * @param resId
	 * @param targetHeight
	 * @return
	 */
	public static Drawable scaleImageToHeightDrawable (Context context, int resId, int targetHeight) {
		Bitmap original = BitmapFactory.decodeResource(context.getResources(), resId);

		return ImageUtils.scaleImageToHeightDrawable(context, original, targetHeight);
	}

	/**
	 * Scale a bitmap to fit a certain height
	 * 
	 * @param context
	 * @param original
	 * @param targetHeight
	 * @return
	 */
	public static Drawable scaleImageToHeightDrawable (Context context, Bitmap original, int targetHeight) {
		// get original width and height
		int origWidth = original.getWidth();
		int origHeight = original.getHeight();

		// get the scale ratio for the height change
		float scaleRatio = (1f*targetHeight) / (1f*origHeight);

		int targetWidth = (int) (origWidth * scaleRatio);

		Bitmap scaledOriginal = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, false);
		return new BitmapDrawable(context.getResources(), scaledOriginal);		
	}

	public static Bitmap scaleImageToHeightBitmap (Bitmap original, int targetHeight) {
		// get original width and height
		int origWidth = original.getWidth();
		int origHeight = original.getHeight();

		// get the scale ratio for the height change
		float scaleRatio = (1f*targetHeight) / (1f*origHeight);

		int targetWidth = (int) (origWidth * scaleRatio);

		Bitmap bitmap = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, false);

		return bitmap;
	}

	/**
	 * Compress a bitmap to a JPEG byte array to a given % quality from 0-100
	 *
	 * @param bitmap
	 * @param quality
	 * @return the compressed bitmap in bytes
	 */
	public static byte[] compressBitmapToBytes (Bitmap bitmap, int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
		return os.toByteArray();		
	}

	/**
	 * Compress a bitmap to a bitmap to a given % quality from 0-100
	 *
	 * @param bitmap
	 * @param quality
	 * @return the compressed bitmap
	 */
	public static Bitmap compressBitmapToBitmap (Bitmap bitmap, int quality) {
		byte[] bytes = compressBitmapToBytes(bitmap, quality);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	/** 
	 * Use for decoding camera response data to a bitmap
	 * 
	 * @param data
	 * @param context 
	 * @return
	 */
	public static Bitmap getBitmapFromCameraData (Intent data, Context context) {
		return getBitmapFromUri(data.getData(), context);
	}

	/** 
	 * Use for decoding gallery response data to a bitmap
	 * 
	 * @param selectedImage
	 * @param context 
	 * @return
	 */
	public static Bitmap getBitmapFromUri (Uri selectedImage, Context context) {
		try {
			String[] filePathColumn = { MediaStore.Images.Media.DATA }; 
			Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null); 
			cursor.moveToFirst(); 
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]); 
			String picturePath = cursor.getString(columnIndex); 
			cursor.close(); 

			return decodeBitmapFromFile(picturePath);
		} catch (Exception e) {}

		return null;
	}

	public static Bitmap getBitmapFromCamera (String file) {
		// get the bitmap from the file
		Bitmap bm = decodeBitmapFromFile(file);

		ExifInterface exif;
		try {
			exif = new ExifInterface(file);

			String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

			// set the default rotation angle to rotate it 
			// counter-clockwise when the orientation is unknown			
			int rotationAngle = 0;

			// check if the image has been rotated from the camera
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

			// rotate the image with a Matrix
			Matrix matrix = new Matrix();
			matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
			return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Bitmap decodeBitmapFromFile (String picturePath) {
		if (Strings.isEmpty(picturePath)) return null;

		Bitmap bitmap;

		// try decoding original
		try {
			bitmap = BitmapFactory.decodeFile(picturePath);

			return bitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();

			System.gc();

			// else try to decode 1/4 the original size
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				bitmap = BitmapFactory.decodeFile(picturePath, options);
				return bitmap;

			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();

				// else try to decode 1/8 the original size
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					bitmap = BitmapFactory.decodeFile(picturePath, options);
					return bitmap;

				} catch (OutOfMemoryError e3) {
					e3.printStackTrace();

					// else try to decode 1/16 the original size
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 16;
						bitmap = BitmapFactory.decodeFile(picturePath, options);
						return bitmap;

					} catch (OutOfMemoryError e4) {
						e4.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	/**
	 * Decode a byte array to an image
	 * 
	 * @param bytes
	 * @return bitmap from the byte array
	 */
	public static Bitmap getBitmapFromBytes (byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		return BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
	}

	/**
	 * Get the orientation of a bitmap
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Orientation getImageOrientation (Bitmap bitmap) {
		if (bitmap == null) return Orientation.UNKNOWN;
		// get the image dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// if it is wider than it is tall
		if (width > height) return Orientation.LANDSCAPE;
		if (width < height) return Orientation.PORTRAIT;
		return Orientation.SQUARE;
	}

	/**
	 * Get the squared center of the bitmap
	 * 
	 * @param srcBmp
	 * @return
	 */
	public static Bitmap centerCropImage (Bitmap srcBmp) {
		Bitmap dstBmp;

		// if the width is bigger, take the height
		if (srcBmp.getWidth() >= srcBmp.getHeight()){

			dstBmp = Bitmap.createBitmap(
					srcBmp, 
					srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
					0,
					srcBmp.getHeight(), 
					srcBmp.getHeight()
					);

		} else {
			// else if the height is bigger, take the width
			dstBmp = Bitmap.createBitmap(
					srcBmp,
					0, 
					srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
					srcBmp.getWidth(),
					srcBmp.getWidth() 
					);
		}

		return dstBmp;
	}

	/**
	 * Format a bitmap to be used as a profile image for a User.
	 * 
	 * @param srcBmp the bitmap to format
	 * @return the resulting bitmap scaled down to PROFILE_DIMxPROFILE_DIM
	 */
	public static Bitmap formatProfileBitmap (Bitmap srcBmp) {
		if (srcBmp == null) return srcBmp;

		// get the square centre of the bitmap
		srcBmp = ImageUtils.centerCropImage(srcBmp);

		// scale it
		srcBmp = Bitmap.createScaledBitmap(srcBmp, ImageUtils.PROFILE_DIM, ImageUtils.PROFILE_DIM, true);

		// compress the bitmap to 90% quality
		srcBmp = ImageUtils.compressBitmapToBitmap(srcBmp, 90);

		return srcBmp;
	}

	/**
	 * @param srcBmp
	 * @return the center cropped cover image bitmap from a source bitmap
	 */
	public static Bitmap formatCoverBitmap (Bitmap srcBmp) {
		return formatCoverBitmap(srcBmp, false);
	}

	/**
	 * @param srcBmp
	 * @param allowTooSmall
	 * @return the center cropped cover image bitmap from a source bitmap
	 */
	public static Bitmap formatCoverBitmap (Bitmap srcBmp, boolean allowTooSmall) {
		if (srcBmp == null) return srcBmp;

		// get the dimensions of the bitmap
		int width = srcBmp.getWidth();
		int height = srcBmp.getHeight();

//		// if the image is not big enough and we are not allowing it to be too small, return null
//		if (!allowTooSmall && (width < ImageUtils.COVER_DIM_WIDTH || height < ImageUtils.COVER_DIM_HEIGHT)) {
//			MyLog.e(TAG, "Cover Image width of height is smaller than dimensions : " + ImageUtils.COVER_DIM_WIDTH + "x" + ImageUtils.COVER_DIM_HEIGHT + " -> " + width + "x" + height);
//			return null;
//		}

		float scaleRatio, targetHeight, targetWidth;

		// destination bitmap
		Bitmap dstBmp;

		// get the scale ratio
		scaleRatio = (1f*width) / (1f*height);

		if (scaleRatio < 1.5) {
			targetWidth = ImageUtils.COVER_DIM_WIDTH;
			targetHeight = (targetWidth / scaleRatio);

			// if the height is less than the minimum we need
			if (targetHeight < COVER_DIM_HEIGHT) {
				float rto = COVER_DIM_HEIGHT / targetHeight;

				// multiply both dimensions by this ratio
				targetHeight *= rto;
				targetWidth *= rto;
			}

		} else {
			targetHeight = ImageUtils.COVER_DIM_HEIGHT;
			targetWidth = (targetHeight * scaleRatio);

			// if the width is less than the minimum we need
			if (targetWidth < COVER_DIM_WIDTH) {
				float rto = COVER_DIM_WIDTH / targetWidth;

				// multiply both dimensions by this ratio
				targetWidth *= rto;
				targetHeight *= rto;
			}
		}

		// round to the nearest integer (correctly floating point precision)
		int targetWidthInt = Math.round(targetWidth);
		int targetHeightInt = Math.round(targetHeight);

		// scale the source bitmap down to the target width and height
		dstBmp = Bitmap.createScaledBitmap(srcBmp, targetWidthInt, targetHeightInt, true);

		// get the positions to start cropping from
		int x = (int) ((targetWidth - ImageUtils.COVER_DIM_WIDTH*1f)/2f);
		int y = (int) ((targetHeight - ImageUtils.COVER_DIM_HEIGHT*1f)/2f);

		// crop the scaled down bitmap to get the centre part of it
		dstBmp = Bitmap.createBitmap(
				dstBmp,
				x, 
				y,
				ImageUtils.COVER_DIM_WIDTH,
				ImageUtils.COVER_DIM_HEIGHT);

		return dstBmp;
	}

	/**
	 * Return the Base64 string from the bytes in the bitmap
	 * 
	 * @param bm
	 * @return
	 */
	public static String getBase64String (Bitmap bm) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

	/**
	 * Rotate a Bitmap by an angle clockwise
	 * 
	 * @param source
	 * @param angle
	 * @return
	 */
	public static Bitmap rotateBitmap (Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	/**
	 * Flip a bitmap vertically
	 * @param src
	 * @return
	 */
	public static Bitmap flipBitmap (Bitmap src) {
		if (src == null) return src;

		Matrix m = new Matrix();
		m.preScale(-1, 1);
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
		dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		return dst;
	}

	/**
	 * @param view
	 * @param bgColor the background colour of the canvas (default: transparent)
	 * @return a bitmap of the view and all its sub-views
	 */
	public static Bitmap getBitmapFromView (View view, Integer bgColor) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();

		if (bgDrawable != null) bgDrawable.draw(canvas);
		else if (bgColor != null) canvas.drawColor(bgColor);
		else canvas.drawColor(Color.TRANSPARENT);

		view.draw(canvas);
		return returnedBitmap;
	}

	/**
	 * @param view
	 * @return a bitmap of the view with a transparent background
	 */
	public static Bitmap getBitmapFromView (View view) {
		return getBitmapFromView(view, null);
	}

	/**
	 * Displays an image using the Loading Images pattern from Android Lollipop Material Design
	 * 
	 * http://stackoverflow.com/questions/27262022/how-to-implement-loading-images-pattern-opacity-exposure-and-saturation-fro
	 * 
	 * @param context
	 * @param imageView
	 * @param bmp
	 */
	protected static void displayImageLollipop (Context context, ImageView imageView, final Bitmap bmp) {
		if (imageView == null || bmp == null || context == null) return; 

		final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bmp);
		imageView.setImageBitmap(bmp);
		AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
		final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(evaluator.getColorMatrix());
		drawable.setColorFilter(filter);

		ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator, evaluator.getColorMatrix());

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate (ValueAnimator animation) {
				drawable.setColorFilter(filter);
			}

		});

		animator.setDuration(IMAGE_FADE_IN_TIME);
		animator.start();
	}

	/**
	 * Display a resource using the Loading Images pattern from Android Lollipop Material Design 
	 * on a device running v21+
	 * 
	 * @param context
	 * @param imageView
	 * @param res
	 */
	protected static void displayImageLollipop (Context context, ImageView imageView, int res) {
		displayImageLollipop(context, imageView, BitmapFactory.decodeResource(context.getResources(), res));
	}

	/**
	 * Display a resource using the Loading Images pattern from Android Lollipop Material Design
	 * on a device running version before v21
	 * 
	 * @param context
	 * @param imageView
	 * @param bm
	 */
	protected static void displayImagePreLollipop (Context context, ImageView imageView, Bitmap bm) {
		if (context == null || imageView == null || bm == null) return;

		final Drawable drawable = new BitmapDrawable(context.getResources(), bm);
		imageView.setImageDrawable(drawable);

		AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
		final AnimateColorMatrixColorFilter filter = new AnimateColorMatrixColorFilter(evaluator.getColorMatrix());
		drawable.setColorFilter(filter.getColorFilter());

		ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator, evaluator.getColorMatrix());

		animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate (ValueAnimator animation) {
				drawable.setColorFilter(filter.getColorFilter());
			}

		});

		animator.setDuration(IMAGE_FADE_IN_TIME);
		animator.start();
	}

	/**
	 * Display a resource using the Loading Images pattern from Android Lollipop Material Design
	 * on a device running version before v21
	 * 
	 * @param context
	 * @param imageView
	 * @param res
	 */
	protected static void displayImagePreLollipop (Context context, ImageView imageView, int res) {
		displayImagePreLollipop(context, imageView, BitmapFactory.decodeResource(context.getResources(), res));
	}

	/**
	 * Display an image, using the appropriate function based on the current device OS version
	 * specifying if to do it with the animation or not
	 * 
	 * @param context
	 * @param imageView
	 * @param bm
	 * @param animate
	 */
	public static void displayImage (Context context, ImageView imageView, Bitmap bm, boolean animate) {
		if (context == null || imageView == null || bm == null) return;

		// if not animating, just set the image and return
		if (!animate) {
			imageView.setImageBitmap(bm);
			return;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
			displayImagePreLollipop(context, imageView, bm);
		else displayImageLollipop(context, imageView, bm);
	}

	/**
	 * Display an image, using the appropriate function based on the current device OS version
	 * for the animation
	 * 
	 * @param context
	 * @param imageView
	 * @param bm
	 */
	public static void displayImage (Context context, ImageView imageView, Bitmap bm) {
		displayImage(context, imageView, bm, true);
	}

	/**
	 * Display an image, using the appropriate function based on the current device OS version
	 * specifying if to do it with the animation or not
	 * 
	 * @param context
	 * @param imageView
	 * @param res
	 * @param animate
	 */
	public static void displayImage (Context context, ImageView imageView, int res, boolean animate) {
		if (context == null || imageView == null) return;

		// if not animating, just set the image and return
		if (!animate) {
			imageView.setImageResource(res);
			return;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
			displayImagePreLollipop(context, imageView, res);
		else displayImageLollipop(context, imageView, res);
	}

	/**
	 * Display an image, using the appropriate function based on the current device OS version
	 * for the animation
	 * 
	 * @param context
	 * @param imageView
	 * @param res
	 */
	public static void displayImage (Context context, ImageView imageView, int res) {
		displayImage(context, imageView, res, true);
	}
	
	/**
	 * @param videoId the Youtube video ID
	 * @param number <b>null></b> for the default image, or specify a number from 0 up
	 * @return the URL for downloading a Youtube video thumbnail image
	 */
	public static String getYoutubeVideoThumbnailURL (String videoId, Integer number) {
		return "http://img.youtube.com/vi/" + videoId + "/" + (number == null ? "hqdefault" : "" + number.intValue()) + ".jpg";
	}

}
