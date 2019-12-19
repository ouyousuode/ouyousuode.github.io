/*
  QRcodeBitmapUtil.java
 */
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWritter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorectionLevel;
import java.util.Hashtable;

public class QRcodeBitmapUtil {

    private static final String TAG = "QRcodeBitmapUtil";
    private static MultiFormatWritter mWriter = new MultiFormatWriter();
    private static Hashtable<EncodeHintType,Object> hints = new Hashtable<>();
    
    static {
	hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
	hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.H);
    }

    public static Bitmap getQRcodeBitmap(String contents,int width,int height) {
	try {
	    BitMatrix matrix = mWriter.encode(contents,BarcodeFormat.QR_CODE,width,height,hints);  
	    Boolean shouldLoop = true;
	    int blankX = 0;
	    int blankY = 0;
	    
	    for(int y = 0;shouldLoop && y < height;y++) {
		for(int x = 0;x < width;x++) {
		    if(matrix.get(x,y)) {
			blankX = x;
			blankY = y;
			shouldLoop = false;
			break;
		    }
		}
	    }
	    
	    if(blankX < 0 || blankY < 0) {
		return null;
	    }

	    int w1 = width - blankX * 2;
	    int h1 = height - blankY * 2;
	    int[] pixels = new int[w1 * h1];

	    for(int y = blankY;y < height-blankY;y++) {
		for(int x = blankX;x < width-blankX;x++) {
		    if(matrix.get(x,y)){
			pixels[(y-blankY)*w1+(x-blankX)] = Color.BLACK;
		    }
		}
	    }

	    Bitmap bitmap = Bitmap.createBitmap(w1,h1,Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels,0,w1,0,0,w1,h1);
	    // Zoom the bitmap
	    Matrix scaleMatrix = new Matrix();
	    float sx = ((float)width) / w1;
	    float sy = ((float)height) / h1;
	    scaleMatrix.postScale(sx,sy);
	    return Bitmap.createBitmap(bitmap,0,0,w1,h1,scaleMatrix,true);
	} catch (Exception e) {
	    LogUtil.e(TAG,"getQRcodeBitmap error",e);
	}

	return null;
    }

    private static final int STEP_MARGIN = 0;
    private static final int STEP_COLOR_LEFT = STEP_MARGIN + 1;
    private static final int STEP_COLOR_RIGHT = STEP_COLOR_LEFT + 1;

    public static Bitmap getQRcodeBitmapWithColor(String contents,int qrSize,int color) {
	try {
	    BitMatrix matrix = mWriter.encode(contents,BarcodeFormat.QR_CODE,qrSize,qrSize,hints);
	    
	    int margin = 0;
	    int colorLeft = 0;
	    boolean checkLeft = false;
	    int colorRight = 0;
	    int step = 0;

	    for(int i = 0;i < qrSize;i++) {
		if(step == STEP_MARGIN) {
		    margin = i;
		    step = STEP_COLOR_LEFT;
		} else if(step == STEP_COLOR_LEFT) {
		    if(!checkLeft && !matrix.get(i,i)) {
			checkLeft = true;
		    } else if(checkLeft && matrix.get(i,i)) {
			colorLeft = i;
			step = STEP_COLOR_RIGHT;
		    }
		} else if(step == STEP_COLOR_RIGHT) {
		    if(!matrix.get(i,i)) {
			colorRight = i + 1;
			break;
		    }
		}
	    }

	    int validSize = qrSize - margin*2;
	    int data[] = new int[validSize * validSize];
	    
	    for(int i = 0;i < validSize;i++) {
		for(int j = 0;j < validSize;j++) {
		    if(matrix.get(margin+j,margin+i)) {
			if(shouldDrawColor(qrSize,colorLeft,colorRight,margin+j,margin+i)) {
			    data[i*validSize+j] = color;
			} else {
			    data[i*validSize+j] = Color.BLACK;
			}
		    } 
		}
	    }

	    Bitmap bitmap = Bitmap.createBitmap(validSize,validSize,Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(data,0,validSize,0,0,validSize,validSize);
	    return bitmap;

	} catch(Exception e) {
	    LogUtil.e(TAG,"getQRcodeBitmapWithColor error",e);
	}
	
	return null;
    }

    private static boolean shouldDrawColor(int qrSize,int colorLeft,int colorRight,int x,int y) {

	// Check left-top
	if(x >= colorLeft && x <= colorRight && y >= colorLeft && y <= colorRight) {
	    return true;
	}
	// Check right-top
	if(x >= colorLeft && x <= colorRight && y >= qrSize-colorRight && y <= qrSize-colorLeft) {
	    return true;
	}
	// Check left-bottom
	if(x >= qrSize-colorRight && x <= qrSize-colorLeft && y >= colorLeft && y <= colorRight) {
	    return true;
	}
	// default
	return false;
    }

    /*
      And Barcode generator ! 
    */
    private static Bitmap getBarcodeBitmap(String contents,int width,int height) {
	
	try {
	    BitMatrix matrix = mWriter.encode(contents,BarcodeFormat.CODE_128,width,height);
	    
	    boolean shouldLoop = true;
	    int blankX = 0;
	    int blankY = 0;
	    
	    for(int y = 0;shouldLoop && y < height;y++) {
		for(int x = 0;x < widht;x++) {
		    if(matrix.get(x,y)) {
			blankX = x;
			blankY = y;
			shouldLoop = false;
			break;
		    }
		}
	    }

	    if(blankX < 0 || blankY < 0) {
		return null;
	    }

	    int w1 = width - blankX * 2;
	    int h1 = height - blankY * 2;
	    int[] pixels = new int[w1 * h1];

	    for(int y = blankY;y < height-blankY;y++) {
		for(int x = blankX;x < width-blankX;x++) {
		    if(matrix.get(x,y)) {
			pixels[(y-blankY)*w1 + (x-blankX)] = Color.BLACK;
		    }
		}
	    }

	    Bitmap bitmap = Bitmap.createBitmap(w1,h1,Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels,0,w1,0,0,w1,h1);
	    // Zoom the bitmap
	    Matrix scaleMatrix = new Matrix();
	    float sx = ((float)width)/w1;
	    float sy = ((float)height)/h1;
	    scaleMatrix.postScale(sx,sy);
	    return Bitmap.createBitmap(bitmap,0,0,w1,h1,scaleMatrix,true);
	} catch(Exception e) {
	    LogUtil.e(TAG,"getBarcodeBitmap error",e);
	}
    }

}