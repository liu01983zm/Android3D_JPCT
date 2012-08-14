package org.ourunix.android.jpctmd2demo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ourunix.android.R;
import org.ourunix.android.RenderBase;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

public class GLRenderer extends RenderBase {
	public Context context;
	//	jpct需要准备下述对象实例,虚拟世界、Framebuffer、3D对象、纹理等
	private World world;
	
	private FrameBuffer fb;
	
	private Object3D soilder;
	
	private String[] texturesName = {"snork"};
	
	private float scale = 0.8f;
	
	// 行走动画 相关参数 
	private int an = 2;  
	private float ind = 0; 
	
	public GLRenderer(Context ctx){
		super(ctx);
		context = ctx;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		
		doAnim();
		// 用颜色清除FrameBuffer
		fb.clear(RGBColor.BLACK);

		// 变换和灯光所有多边形
		world.renderScene(fb);
		
		// 绘制
		world.draw(fb);
		
		//显示
		fb.display();
		
	}

	/**
	 * 实现动画的代码
	 * */
	private void doAnim() {
		// TODO Auto-generated method stub
		//每一帧加0.018f  
		ind += 0.018f;  
		if (ind > 1f) {  
		ind -= 1f;  
		}  
		// 关于此处的两个变量，ind的值为0-1(jpct-ae规定),0表示第一帧，1为最后一帧；  
		//至于an这个变量，它的意思是sub-sequence如果在keyframe(3ds中),因为在一个  
		//完整的动画包含了seq和sub-sequence，所以设置为2表示执行sub-sequence的动画，  
		//但这里设置为2我就不太明白了，不过如果不填，效果会不自然，所以我就先暂时把它  
		//设置为2  
		soilder.animate(ind, an);  
	}
	float downX = 0;
	float downY = 0;
	float upX = 0;
	float upY = 0;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return super.onTouch(v, event);
	}
	@Override
	public void rightSwipe() {
		soilder.rotateY(-5);
		super.rightSwipe();
	}
	@Override
	public void leftSwipe() {
		soilder.rotateY(5);
		super.leftSwipe();
	}
   @Override
	public boolean handleTouchEvent(MotionEvent event) {
	   int action = event.getAction();

       switch (action) {

       case MotionEvent.ACTION_DOWN:

       	  downX = event.getX();
             downY = event.getY();
           break;

       case MotionEvent.ACTION_MOVE:
           
       	upX = event.getX();
           upY = event.getY();

           float deltaX = downX - upX;
           float deltaY = downY - upY;

           // swipe horizontal?
           if(Math.abs(deltaX) > SWIPE_MIN_DISTANCE){
               // left or right
               if(deltaX < 0) { rightSwipe(); }
               if(deltaX > 0) { leftSwipe();  }
           }
           else {
                   Log.i("", "Swipe was only " + Math.abs(deltaX) + " long, need at least " + SWIPE_MIN_DISTANCE);
                   return false; // We don't consume the event
           }

           // swipe vertical?
           if(Math.abs(deltaY) > SWIPE_MIN_DISTANCE){
               // top or down
               if(deltaY < 0) { downSwipe(); }
               if(deltaY > 0) { upSwipe();}
           }
           else {
                   Log.i("", "Swipe was only " + Math.abs(deltaX) + " long, need at least " + SWIPE_MIN_DISTANCE);
                   return false; // We don't consume the event
           }

           break;

       case MotionEvent.ACTION_UP:break;

       case MotionEvent.ACTION_CANCEL:break;

       }
		return super.handleTouchEvent(event);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// 如果FrameBuffer不为NULL,释放fb所占资源
		if (fb != null){
			fb.dispose();
		}
		fb = new FrameBuffer(gl, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//载入Assets文件夹下的文件
		new LoadAssets(context.getResources());
		// TODO Auto-generated method stub
		//实例化虚拟世界
		world = new World();
		
		//设置了环境光源强度。负:整个场景会变暗;正:将照亮了一切。
		
		world.setAmbientLight(150, 150, 150);
		 TextureManager tm = TextureManager.getInstance();  
		 Texture texture2 = new Texture(BitmapFactory.decodeResource(context.getResources(), R.raw.soilder)); 
         tm.addTexture(texturesName[0], texture2);  
	
		// 从assets文件夹中读取soilder.md2文件来实例化Object3D snork
		soilder = Loader.loadMD2(LoadAssets.loadf("soilder.md2"), scale);
		
		// 旋转soilder对象到"适当位置"  
		soilder.translate(0, 0, -50);
		
		//这才是将纹理添加进去
		soilder.setTexture(texturesName[0]);
		
		// 释放部分资源  
		soilder.strip();  
		// 编译  
		soilder.build();  
		
		// 将snork添加到World对象中  
		world.addObject(soilder);
		
		//获得Camera
		Camera cam = world.getCamera();
		
		cam.setPosition(0, 0, -100);
		
		cam.lookAt(soilder.getTransformedCenter());
		
	}
}

//加载assets类
class LoadAssets {
	public static Resources res;

	public LoadAssets(Resources resources) {
		res = resources;
	}

	public static InputStream loadf(String fileName) {
		AssetManager am = res.getAssets();
		try {
			return am.open(fileName, AssetManager.ACCESS_UNKNOWN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}