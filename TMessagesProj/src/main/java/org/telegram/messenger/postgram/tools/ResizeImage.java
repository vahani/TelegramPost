package org.telegram.messenger.postgram.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

public class ResizeImage {

	private static String scaleAndSaveImageInternal(Bitmap bitmap, int w,
			int h, float photoW, float photoH, float scaleFactor, int quality,
			boolean cache, boolean scaleAnyway, String TmpAddress)
			throws Exception {

		// /

		// /

		Bitmap scaledBitmap = null;
		if (scaleFactor > 1 || scaleAnyway) {
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
		} else {
			scaledBitmap = bitmap;
		}

		int volume_id = Integer.MIN_VALUE;
		int local_id = 1;

		String fileName = "";
		if (!cache) {
			fileName = volume_id + "_" + local_id + ".jpg";
			File Media_Dir = new File(TmpAddress);
			final File cacheFile = new File(Media_Dir, fileName);
			FileOutputStream stream = new FileOutputStream(cacheFile);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
		} else {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
		}
		if (scaledBitmap != bitmap) {
			scaledBitmap.recycle();
		}

		return fileName;
	}

	public static String scaleAndSaveImage(Bitmap bitmap, float maxWidth,
			float maxHeight, int quality, boolean cache, int minWidth,
			int minHeight, String TmpAddress) {
		if (bitmap == null) {
			return "";
		}
		float photoW = bitmap.getWidth();
		float photoH = bitmap.getHeight();
		if (photoW == 0 || photoH == 0) {
			return "";
		}
		boolean scaleAnyway = false;
		float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
		if (scaleFactor < 1 && minWidth != 0 && minHeight != 0) {
			scaleFactor = Math.max(photoW / minWidth, photoH / minHeight);
			scaleAnyway = true;
		}
		int w = (int) (photoW / scaleFactor);
		int h = (int) (photoH / scaleFactor);
		if (h == 0 || w == 0) {
			return "";
		}

		try {
			return scaleAndSaveImageInternal(bitmap, w, h, photoW, photoH,
					scaleFactor, quality, cache, scaleAnyway, TmpAddress);
		} catch (Throwable e) {
			Log.w("tmessages", e);
			System.gc();
			try {
				return scaleAndSaveImageInternal(bitmap, w, h, photoW, photoH,
						scaleFactor, quality, cache, scaleAnyway, TmpAddress);
			} catch (Throwable e2) {
				Log.w("tmessages", e2);
				return "";
			}
		}
	}

	public static Bitmap loadBitmap(String path, Uri uri, float maxWidth,
			float maxHeight, Context ctx) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		FileDescriptor fileDescriptor = null;
		ParcelFileDescriptor parcelFD = null;

		if (path == null && uri != null && uri.getScheme() != null) {
			if (uri.getScheme().contains("file")) {
				path = uri.getPath();
			} else {
				try {
				} catch (Throwable e) {
					Log.w("tmessages", e);
				}
			}
		}

		if (path != null) {
			BitmapFactory.decodeFile(path, bmOptions);
		} else if (uri != null) {
			try {
				parcelFD = ctx.getContentResolver()
						.openFileDescriptor(uri, "r");
				fileDescriptor = parcelFD.getFileDescriptor();
				BitmapFactory.decodeFileDescriptor(fileDescriptor, null,
						bmOptions);
			} catch (Throwable e) {
				Log.w("tmessages", e);
				try {
					if (parcelFD != null) {
						parcelFD.close();
					}
				} catch (Throwable e2) {
					Log.w("tmessages", e2);
				}
				return null;
			}
		}
		float photoW = bmOptions.outWidth;
		float photoH = bmOptions.outHeight;
		float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
		if (scaleFactor < 1) {
			scaleFactor = 1;
		}
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = (int) scaleFactor;

		String exifPath = null;
		if (path != null) {
			exifPath = path;
		} else if (uri != null) {
		}

		Matrix matrix = null;

		if (exifPath != null) {
			ExifInterface exif;
			try {
				exif = new ExifInterface(exifPath);
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 1);
				matrix = new Matrix();
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					matrix.postRotate(90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					matrix.postRotate(180);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					matrix.postRotate(270);
					break;
				}
			} catch (Throwable e) {
				Log.w("tmessages", e);
			}
		}

		Bitmap b = null;
		if (path != null) {
			try {
				b = BitmapFactory.decodeFile(path, bmOptions);
				if (b != null) {
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
							b.getHeight(), matrix, true);
				}
			} catch (Throwable e) {
				Log.w("tmessages", e);
				try {
					if (b == null) {
						b = BitmapFactory.decodeFile(path, bmOptions);
					}
					if (b != null) {
						b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
								b.getHeight(), matrix, true);
					}
				} catch (Throwable e2) {
					Log.w("tmessages", e2);
				}
			}
		} else if (uri != null) {
			try {
				b = BitmapFactory.decodeFileDescriptor(fileDescriptor, null,
						bmOptions);
				if (b != null) {
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
							b.getHeight(), matrix, true);
				}
			} catch (Throwable e) {
				Log.w("tmessages", e);
			} finally {
				try {
					if (parcelFD != null) {
						parcelFD.close();
					}
				} catch (Throwable e) {
					Log.w("tmessages", e);
				}
			}
		}

		return b;
	}

	static int photoSize = 0;

	public static int getPhotoSize() {
		if (photoSize == 0) {
			if (Build.VERSION.SDK_INT >= 16) {
				photoSize = 1366;
			} else {
				photoSize = 1024;
			}
		}
		return photoSize;
	}

}