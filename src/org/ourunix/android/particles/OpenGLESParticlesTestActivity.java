package org.ourunix.android.particles;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLESParticlesTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView mGLSurfaceView = new GLSurfaceView(this);
        GLRenderer mGLRenderer = new org.ourunix.android.particles.GLRenderer(this);
        mGLSurfaceView.setRenderer(mGLRenderer);
        
        setContentView(mGLSurfaceView);
    }
}

class LoadImag{
	public static Bitmap bitmap;
	public static Bitmap loadI(Resources res, int id){
		bitmap = BitmapFactory.decodeResource(res, id);
		return bitmap;
	}
}