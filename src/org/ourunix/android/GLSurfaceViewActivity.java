package org.ourunix.android;

import java.io.IOException;
import java.io.InputStream;

import org.ourunix.android.jpct.ball.BallCollisionRenderer;
import org.ourunix.android.jpct.particles.ParticlesGLRenderer;
import org.ourunix.android.jpct.solar.SolarRenderer;
import org.ourunix.android.jpctmd2demo.GLRenderer;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Wrapper activity demonstrating the use of {@link GLSurfaceView}, a view that
 * uses OpenGL drawing into a dedicated surface.
 */
public class GLSurfaceViewActivity extends Activity implements OnTouchListener {

    private RenderBase mCurrentRender = null;
	//这里设置为public static，是因为在MyRenderer里面用到
	public static boolean up = false; // 方向上下左右
	public static boolean down = false;
	public static boolean left = false;
	public static boolean right = false;
    public  boolean fire  = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("","--into GLSurfaceViewActivity---");
        Renderer render = null;
        Bundle bundle = this.getIntent().getExtras();

        int index = bundle.getInt("INDEX");
        
        switch (index) {
        default:
        case  0: render = new BallCollisionRenderer(this);  break;
        case  1: render = new ParticlesGLRenderer(this);  break;  
        case  2: render = new GLRenderer(this); break;
        case  3: render = new SolarRenderer(this);break;
        }

        if (render != null) {
            mCurrentRender = (RenderBase) render;
            mGLSurfaceView = new GLSurfaceView(this);
            mGLSurfaceView.setRenderer(render);
            mGLSurfaceView.setOnTouchListener(this);
            setContentView(mGLSurfaceView);

            String title = bundle.getString("TITLE");
            setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }
    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	 if (mCurrentRender != null) {
             boolean bRet = mCurrentRender.onTouch(v, event);
             if (bRet) {
                 return true;
             }
         }
		return false;
	}
    private GLSurfaceView mGLSurfaceView;
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCurrentRender != null) {
            boolean bRet = mCurrentRender.handleTouchEvent(event);
            if (bRet) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mCurrentRender != null) {
            boolean bRet = mCurrentRender.handleKeyEvent(keyCode, event);
            if (bRet) {
                // mGLSurfaceView.requestRender();
                return true;
            }
            up = false;
        	down = false;
        	left = false;
        	right = false;
        	//fire = false;
        }
        return super.onKeyUp(keyCode, event);
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 按键处理，当上下左右中的一个按下时，则将相应的变量置true
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				up = true;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				down = true;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				left = true;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				right = true;
				break;
			case KeyEvent.KEYCODE_BACK:
				 finish();
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				fire = true;
				break;
			}
			return super.onKeyDown(keyCode, event);
	}

}
