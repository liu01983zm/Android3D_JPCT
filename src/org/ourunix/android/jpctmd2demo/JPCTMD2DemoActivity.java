package org.ourunix.android.jpctmd2demo;

import java.io.IOException;
import java.io.InputStream;

import org.ourunix.android.R;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
//在Android上使用3D 引擎(JPCT-AE)加载MD2文件
//http://www.ourunix.org/android/post/49.html
public class JPCTMD2DemoActivity extends Activity {
	/** Called when the activity is first created. */

	GLSurfaceView glView;

	GLRenderer renderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		
		glView = new GLSurfaceView(this);

		renderer = new GLRenderer(this);

		glView.setRenderer(renderer);

		setContentView(glView);
	}
	
}

