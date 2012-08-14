package org.ourunix.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;


abstract public class RenderBase implements Renderer {
	protected Context mContext;
	
	protected static final int SWIPE_MIN_DISTANCE = 120;
	protected static final int SWIPE_MAX_OFF_PATH = 250;
	protected static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	
	public RenderBase(Context context){
		mContext = context;
		
	}
	
	public FloatBuffer createFloatBuffer(float data[]){
		ByteBuffer vbb = ByteBuffer.allocateDirect(data.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer outBuffer = vbb.asFloatBuffer();
		outBuffer.put(data).position(0);
		return outBuffer;
	}
	
	public Bitmap getTextureFromBitmapResource(Context context, int resourceId) {
		Bitmap bitmap = null;
		Matrix yFlipMatrix = new Matrix();
		yFlipMatrix.postScale(1, -1); // flip Y axis		
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), yFlipMatrix, false);
		}
		finally	{
			if (bitmap != null) {
				bitmap.recycle();
			}
		}
	}		
	  public class MyGestureDetector extends SimpleOnGestureListener {
	        @Override
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	            try {
	                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	                    return false;
	                // right to left swipe
	                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                    leftSwipe();
	                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                	rightSwipe();
	                }
	            } catch (Exception e) {
	                // nothing
	            }
	            return false;
	        }

	    }
	public void leftSwipe(){
		 
	}
	public void rightSwipe(){
		
	}
	public void upSwipe(){
		 
	}
	public void downSwipe(){
		
	}
    public boolean onTouch(View v, MotionEvent event) {
    	Log.i("RenderBase","----------OnTouch-------------");
        return false;
    }
	public boolean handleTouchEvent(MotionEvent event) {
		return false;
	}
	
	public boolean handleKeyEvent(int keyCode, KeyEvent event) {
		return false;
	}	

	public void glutSolidCube(GL10 gl, float size){
		float v[] = new float[108];	   // 108 =  6*18

		final float cubev[] = 
		{
			-1.0f, -1.0f, 1.0f,	/* front */
			1.0f, -1.0f, 1.0f,
			-1.0f,  1.0f, 1.0f,

			1.0f, -1.0f, 1.0f,
			1.0f,  1.0f, 1.0f,
			-1.0f,  1.0f, 1.0f,

			-1.0f,  1.0f, -1.0f,	/* back */
			1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, -1.0f,

			-1.0f,  1.0f, -1.0f,
			1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,

			-1.0f, -1.0f, -1.0f,	/* left */
			-1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f,

			-1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f,

			1.0f, -1.0f,  1.0f,	/* right */
			1.0f, -1.0f, -1.0f,
			1.0f,  1.0f,  1.0f,

			1.0f, -1.0f, -1.0f,
			1.0f,  1.0f, -1.0f,
			1.0f,  1.0f,  1.0f,

			-1.0f,  1.0f,  1.0f,	/* top */
			1.0f,  1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f,

			1.0f,  1.0f,  1.0f,
			1.0f,  1.0f, -1.0f,
			-1.0f,  1.0f, -1.0f,

			-1.0f, -1.0f, -1.0f,	/* bottom */
			1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f,  1.0f,

			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,
		};

		final float cuben[] = 
		{
			0.0f, 0.0f, 1.0f,	/* front */
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,

			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,

			0.0f, 0.0f, -1.0f,	/* back */
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,

			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,

			-1.0f, 0.0f, 0.0f,	/* left */
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,

			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,

			1.0f, 0.0f, 0.0f,	/* right */
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,

			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,

			0.0f, 1.0f, 0.0f,	/* top */
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,

			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,

			0.0f, -1.0f, 0.0f,	/* bottom */
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,

			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
		};

		int i;
		size /= 2;

		for(i = 0; i < 108; i++) {
			v[i] = cubev[i] * size;
		}
		
		FloatBuffer mVBuffer;
		FloatBuffer mCubenBuffer;
		
		mVBuffer = createFloatBuffer(v);
		mCubenBuffer = createFloatBuffer(cuben);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, mCubenBuffer);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 36);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);		
	}
}