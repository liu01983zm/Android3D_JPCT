package org.ourunix.android.jpct.solar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ourunix.android.GLSurfaceViewActivity;
import org.ourunix.android.R;
import org.ourunix.android.RenderBase;

import com.threed.jpct.*;
import com.threed.jpct.Matrix;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.*;
//http://blog.csdn.net/itde1/article/details/754341
//http://www.jpct.net/jpct-ae/doc/index.html
//http://blog.csdn.net/wangziling100/article/details/7287803
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.ourunix.android.jpct.util.Util;
public class SolarRenderer extends RenderBase {

	// FrameBuffer对象
	private FrameBuffer fb;
	// World对象
	private World world;
	// RGBColor
	// private RGBColor back = null;
	// Object3D对象
	private Object3D plane = null;

	private float colors[] = { 
            1.0f, 0.0f, 0.0f, 1.0f, 
            0.0f, 1.0f, 0.0f, 1.0f, 
            0.0f, 0.0f, 1.0f, 1.0f };
	private FloatBuffer colorBuffer;
	
	public final static int BALL_NUM = 8;
	public final static int BALL_RADIUS = 32;
	public final static int TRACK_RADIUS = 180;
	private Object3D ballIndicator= null;
	private Object3D ballCenter = null;
	private Object3D ball[] = new Object3D[8];

	private Mesh mesh1 = null;
	final float TABLE_Y = -60;
	// SimpleVector
	// 通过设置组件的x,y,z向量来创建一个SimpleVector对象 ，表示小球的运动方向和速度
	private SimpleVector move = new SimpleVector(-4.0, 0.0, 4.0);
	private boolean fire = true; // 是否击球

	private boolean collsion = false;// 是否发生碰撞
	private SimpleVector tem;

	// FPS
	private int fps = 0;
	private long time = System.currentTimeMillis();
	private GestureDetector gestureDetector;
	
	private int  roateDegree = 0;
	Context mContext;
	// 默认构造
	// 对该项目的一些优化
	public SolarRenderer(Context context) {
		super(context);
		mContext = context;
		// 传入Resources方法
		LoadFile.loadb(context.getResources());
		new LoadFile(context.getResources());
		// 绘制的最多的Polygon数量,默认为4096,此处如果超过500，则不绘制
		Config.maxPolysVisible = 500;
		// 最远的合适的平面,默认为1000
		Config.farPlane = 1500;
		Config.glTransparencyMul = 0.1f;
		Config.glTransparencyOffset = 0.1f;
		// 使JPCT-AE这个引擎使用顶点而不是顶点数组缓冲对象，因为它可能会使某些硬件更快
		// 但在Samsung Galaxy,它并不能工作的很好，可能使之崩溃，这就是它默认为false的原因
		Config.useVBO = true;
		// back = new RGBColor(50, 50, 100);
		Texture.defaultToMipmapping(true);
		Texture.defaultTo4bpp(true);
		
		gestureDetector = new GestureDetector(new MyGestureDetector());
       
        
	}

	public void setFire(boolean fire) {
		this.fire = fire;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		try {
			if (true) {

				move();// 实现上下左右键
				if (fire) {
//					SimpleVector axis = new SimpleVector(1, 0, 1);// 球旋转时轴的方向
//					ball1.rotateAxis(axis, (float) Math.toRadians(10));// 实现球旋转
				}
				// 以定义好的RGBColor清屏
				// fb.clear(back);
				fb.clear();
				// 变换和灯光所有的多 边形
				world.renderScene(fb);
				// 绘制由renderScene产生的场景
				world.draw(fb);
				// 渲染显示图像
				fb.display();
				// fps加1
				fps += 1;
				// 打印输出fps
				if (System.currentTimeMillis() - time > 1000) {
					System.out.println(fps + "fps");
					fps = 0;
					time = System.currentTimeMillis();
				}
			} else {
				if (fb != null) {
					fb.dispose();
					fb = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 打印异常信息
			Logger.log("Drawing thread terminated!", Logger.MESSAGE);
		}
	}
   
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		if (fb != null) {
			fb = null;
		}
		// 新产生一个FrameBuffer对象
		fb = new FrameBuffer(gl, width, height);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		Logger.log("onCreate");
		colorBuffer = Util.createFloatBuffer(colors);
		// 混合渲染
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// 新建world对象
		world = new World();
		// 纹理
		//AndroidRuntime com.threed.jpct.Texture.loadTexture
		//That's because you are loading it as a Drawable. Don't do that. Drawables will be scaled by Android under some circumstances. Use either bitmaps or an inputstream instead.
		 TextureManager tm = TextureManager.getInstance();
		 Texture texture2 = new Texture(LoadFile.bitmap1);
		 Texture texture3 = new Texture(LoadFile.bitmap2);
		 Texture texture4 = new Texture(LoadFile.bitmap3);
		 tm.addTexture("texture2", texture2);
		 tm.addTexture("texture3", texture3);
		 tm.addTexture("texture4",texture4);
		// 初始化各3D元素
		plane = Primitives.getPlane(20, 10); // 得到平面
		// 也可以采用上面得到平面的方法，这里采用的是加载3ds模型
		plane = loadModel("table.3ds", 4f);
		plane.translate(0, -30, 20);
		plane.rotateX(-(float) Math.PI / 2); // 从jpct-ae的坐标旋转到正常坐标系
		plane.rotateY((float) Math.PI / 2);
		world.addObject(plane);
	

		// 设置纹理贴图方式
		ballIndicator = Primitives.getSphere(BALL_RADIUS); // 得到球体
		ballIndicator.translate(0,TABLE_Y,-TRACK_RADIUS);
		world.addObject(ballIndicator);
		ballIndicator.setVisibility(false);
		
		ballCenter = Primitives.getSphere(BALL_RADIUS); // 得到球体
		ballCenter.translate(0,TABLE_Y,0);
		ballCenter.calcTextureWrapSpherical();
		ballCenter.setTexture("texture4");
		world.addObject(ballCenter);
		ballCenter.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		
		for (int i = 0; i < BALL_NUM; i++) {
			ball[i] = Primitives.getSphere(BALL_RADIUS); // 得到球体

			ball[i].translate(((int) (TRACK_RADIUS * Math.sin(Math.toRadians(i * 45)))),
					TABLE_Y, ((int) (TRACK_RADIUS * Math.cos(Math.toRadians(i * 45)))));
			 ball[i].calcTextureWrapSpherical();
			 ball[i].setTexture("texture3");

			world.addObject(ball[i]);
			ball[i].setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		}
		// 设置碰撞模式
		
		plane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		// 设置环境光
		world.setAmbientLight(255, 255, 255);
		// 这里设置光照的地方不成功，不能显示光照，不知道为什么？？？？？？？？？？？？
		// 设置光照
		Light light = new Light(world);
		light.setPosition(new SimpleVector(ballIndicator.getTransformedCenter().x,
				ballIndicator.getTransformedCenter().y - 100, ballIndicator
						.getTransformedCenter().z - 50));
		light.setIntensity(255, 0, 0);

		// 以上3段代码没有效果
		// 编译所有对象
		world.buildAllObjects();

		// Camera相关
		Camera cam = world.getCamera();
		cam.setPositionToCenter(ballIndicator);
		cam.align(ballIndicator);// 将相机方向对着物体的Z轴正方向
		// 相机绕着X轴旋转20度
		cam.rotateCameraX((float) Math.toRadians(40));
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 500);
		// 向外以移动
		cam.moveCamera(Camera.CAMERA_MOVEUP, 60);

		// cam.lookAt(plane.getTransformedCenter());

		// 回收内存
		MemoryHelper.compact();

	}

	// 载入模型
	private Object3D loadModel(String filename, float scale) {
		// 将载入的3ds文件保存到model数组中
		Object3D[] model = Loader.load3DS(LoadFile.loadf(filename), scale);
		// 取第一个3ds文件
		Object3D o3d = new Object3D(0);
		// 临时变量temp
		Object3D temp = null;
		// 遍历model数组
		for (int i = 0; i < model.length; i++) {
			// 给temp赋予model数组中的某一个
			temp = model[i];
			// 设置temp的中心为 origin (0,0,0)
			temp.setCenter(SimpleVector.ORIGIN);
			// 沿x轴旋转坐标系到正常的坐标系(jpct-ae的坐标中的y,x是反的)
			temp.rotateX((float) (-.5 * Math.PI));
			// 使用旋转矩阵指定此对象旋转网格的原始数据
			temp.rotateMesh();
			// new 一个矩阵来作为旋转矩阵
			temp.setRotationMatrix(new Matrix());
			// 合并o3d与temp
			o3d = Object3D.mergeObjects(o3d, temp);
			// 主要是为了从桌面版JPCT到android版的移徝(桌面版此处为o3d.build())
			o3d.compile();
		}
		// 返回o3d对象
		return o3d;
	}
	@Override
	public void leftSwipe() {
		 Log.d(TAG, "---onTouchEvent action:ACTION_LEFT");
		 roateDegree+=10;
		 roateDegree%=360;
		for (int i = 0; i < BALL_NUM; i++) {
			ball[i].clearTranslation();
			ball[i].translate(((int) (TRACK_RADIUS* Math.sin(Math.toRadians(( i * 45 +roateDegree)%360)))),
					TABLE_Y, ((int) (TRACK_RADIUS * Math.cos(Math.toRadians((i * 45 +roateDegree)%360)))));

		}
		super.leftSwipe();
	}
	@Override
	public void rightSwipe() {
		 roateDegree-=10;
		 if(roateDegree<0){
			 roateDegree+=360;
		 }
		 
		 Log.d(TAG, "---onTouchEvent action:ACTION_RIGHT");
        for (int i = 0; i < BALL_NUM; i++) {
        	ball[i].clearTranslation();
			ball[i].translate(((int) (TRACK_RADIUS * Math.sin(Math.toRadians((i * 45 +roateDegree)%360)))),
					TABLE_Y, ((int) (TRACK_RADIUS * Math.cos(Math.toRadians((i * 45 +roateDegree)%360)))));

		}
		super.rightSwipe();
	}
	public void upSwipe(){}
	public void downSwipe(){}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	public final static String TAG="TAG";
	float downX = 0;
	float downY = 0;
	float upX = 0;
	float upY = 0;
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
                    Log.i(TAG, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + SWIPE_MIN_DISTANCE);
                    return false; // We don't consume the event
            }

            // swipe vertical?
            if(Math.abs(deltaY) > SWIPE_MIN_DISTANCE){
                // top or down
                if(deltaY < 0) { downSwipe(); }
                if(deltaY > 0) { upSwipe();}
            }
            else {
                    Log.i(TAG, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + SWIPE_MIN_DISTANCE);
                    return false; // We don't consume the event
            }

            break;

        case MotionEvent.ACTION_UP:

            //Log.d(TAG, "---onTouchEvent action:ACTION_UP");

            break;

        case MotionEvent.ACTION_CANCEL:

           // Log.d(TAG, "---onTouchEvent action:ACTION_CANCEL");

            break;

        }

    	return super.handleTouchEvent(event);
    }
	public void move() {
		Camera cam = world.getCamera();
		if (GLSurfaceViewActivity.up) { // 按向上方向键
			cam.moveCamera(cam.getDirection(), -2);// 摄像机向里面移动
		}
		if (GLSurfaceViewActivity.down) {
			cam.moveCamera(cam.getDirection(), 2);// 向外移动
		}
		if (GLSurfaceViewActivity.left) {
			plane.rotateY((float) Math.toRadians(-10));// 向左旋转
		}
		if (GLSurfaceViewActivity.right) {
			plane.rotateY((float) Math.toRadians(10)); // 向右旋转
		}

	}
}

// 载入文件
class LoadFile {
	public static Resources resource;
	public static Bitmap bitmap1;
	public static Bitmap bitmap2;
	public static Bitmap bitmap3;

	public LoadFile(Resources res) {
		resource = res;
	}

	// 载入模型
	public static InputStream loadf(String fileName) {
		AssetManager am = LoadFile.resource.getAssets();
		try {
			return am.open(fileName);
		} catch (IOException e) {
			return null;
		}
	}

	// 载入纹理图片
	public static void loadb(Resources res) {
		bitmap1 = BitmapFactory.decodeResource(res, R.raw.table);
		bitmap2 = BitmapFactory.decodeResource(res, R.raw.bool1);
		bitmap3 = BitmapFactory.decodeResource(res, R.raw.bool2);
	}
}
