package org.ourunix.android.jpct.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.ourunix.android.R;
import org.ourunix.android.RenderBase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
/**
 * 
 * 
 * http://www.ourunix.org/android/post/72.html
 * */
public class ParticlesGLRenderer extends RenderBase {
	public Context context;
	// 随机数
	private Random random;
	// 定义最在的粒子数
	private static int maxParticles = 1000;
	// 减速粒子
	private float slowdown;
	// X方向的速度
	private float xSpeed;
	// Y方向的速度
	private float ySpeed;
	// 沿Z轴缩放
	private float zoom;
	// 循环变量
	private int loop;

	// 创建一个名为Particle的数组,存储MAX_PARTICLES个元素
	private Particles particles[] = new Particles[maxParticles];

	// 存储12种不同颜色
	private float colors[][] = { { 1.0f, 0.75f, 0.5f }, { 1.0f, 0.75f, 0.5f },
			{ 1.0f, 1.0f, 0.5f }, { 0.75f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.5f },
			{ 0.5f, 1.0f, 0.75f }, { 0.5f, 1.0f, 1.0f }, { 0.5f, 0.75f, 1.0f },
			{ 0.5f, 0.5f, 1.0f }, { 0.75f, 0.5f, 1.0f }, { 1.0f, 0.5f, 1.0f },
			{ 1.0f, 0.5f, 0.75f } };

	// vertexBuffer
	private FloatBuffer vertexBuffer;
	// texCoordBuffer
	private FloatBuffer texCoordBuffer;
	// vertex
	private float[] vertex = new float[12];
	// texCoord
	private float[] texCoord = new float[8];

	// LoadBuffer
	public void LoadBuffer(GL10 gl) {
		ByteBuffer vertexByteBuffer = ByteBuffer
				.allocateDirect(vertex.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = vertexByteBuffer.asFloatBuffer();
		vertexBuffer.put(vertex);
		vertexBuffer.position(0);

		ByteBuffer texCoordByteBuffer = ByteBuffer
				.allocateDirect(texCoord.length * 4);
		texCoordByteBuffer.order(ByteOrder.nativeOrder());
		texCoordBuffer = texCoordByteBuffer.asFloatBuffer();
		texCoordBuffer.put(texCoord);
		texCoordBuffer.position(0);
	}

	public ParticlesGLRenderer(Context ctx) {
		super(ctx);
		context = ctx;
		random = new Random();
		slowdown = 0.5f;
		xSpeed = 20;
		ySpeed = 20;
		zoom = -30.0f;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		LoadBuffer(gl);
		// 清除屏幕和颜色缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// 重置模型变换矩阵
		gl.glLoadIdentity();

		// 开启顶点纹理状态
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
		
		for (loop = 0; loop < maxParticles; loop++) {
			// 如果粒子为激活的
			if (particles[loop].active) {
				// 返回X轴的位置
				float x = particles[loop].x;
				// 返回Y轴的位置
				float y = particles[loop].y;
				// 返回Z轴的位置
				float z = particles[loop].z + zoom;
				// 设置粒子颜色
				gl.glColor4f(particles[loop].r, particles[loop].g,
						particles[loop].b, particles[loop].life);
				// 开始准备绘制"三角地带"(名字怪怪的)
				texCoordBuffer.clear();
				vertexBuffer.clear();
				texCoordBuffer.put(1.0f);
				texCoordBuffer.put(1.0f);
				vertexBuffer.put(x + 0.5f);
				vertexBuffer.put(y + 0.5f);
				vertexBuffer.put(z);
				texCoordBuffer.put(1.0f);
				texCoordBuffer.put(0.0f);
				vertexBuffer.put(x + 0.5f);
				vertexBuffer.put(y);
				vertexBuffer.put(z);
				texCoordBuffer.put(0.0f);
				texCoordBuffer.put(1.0f);
				vertexBuffer.put(x);
				vertexBuffer.put(y + 0.5f);
				vertexBuffer.put(z);
				texCoordBuffer.put(0.0f);
				texCoordBuffer.put(0.0f);
				vertexBuffer.put(x);
				vertexBuffer.put(y);
				vertexBuffer.put(z);
				// 绘制
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				// 更新X坐标的位置
				particles[loop].x += particles[loop].xi / (slowdown * 1000);
				// 更新Y坐标的位置
				particles[loop].y += particles[loop].yi / (slowdown * 1000);
				// 更新Z坐标的位置
				particles[loop].z += particles[loop].zi / (slowdown * 1000);

				// 更新X轴方向速度大小
				particles[loop].xi += particles[loop].xg;
				// 更新Y轴方向速度大小
				particles[loop].yi += particles[loop].yg;
				// 更新Z轴方向速度大小
				particles[loop].zi += particles[loop].zg;

				// 减少粒子的生命值
				particles[loop].life -= particles[loop].fade;

				// 如果粒子生命小于0
				if (particles[loop].life < 0.0f) {
					float xi, yi, zi;
					xi = xSpeed + (float) ((rand() % 60) - 32.0f);
					yi = ySpeed + (float) ((rand() % 60) - 30.0f);
					zi = (float) ((rand() % 60) - 30.0f);
					initParticles(loop, random.nextInt(12), xi, yi, zi);
				}

			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		float ratio = (float) width / height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);// 设置为投影矩阵模式
		gl.glLoadIdentity();// 重置
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);// 设置视角
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// 透视修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// 用黑色擦出背景
		gl.glClearColor(0, 0, 0, 0);
		// 阴影平滑
		gl.glShadeModel(GL10.GL_SMOOTH);
		// 关闭深度测试
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// 开启混合
		gl.glEnable(GL10.GL_BLEND);

		// 允许贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);

		IntBuffer intBuffer = IntBuffer.allocate(1);
		// 生成纹理
		gl.glGenTextures(1, intBuffer);

		int texture = intBuffer.get();

		// 绑定纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		// 生成纹理
		Bitmap bmp = LoadImag.loadI(context.getResources(), R.drawable.star);

		// 生成纹理
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

		// 线形滤波
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		
		for (loop = 0; loop < maxParticles; loop++) {
            float xi, yi, zi;
            xi = (float) ((rand() % 50) - 26.0f) * 10.0f;
            yi = zi = (float) ((rand() % 50) - 25.0f) * 10.0f;
            // 初始化粒子
            initParticles(loop, random.nextInt(12), xi, yi, zi);
        }
	}

	public int rand() {
		return Math.abs(random.nextInt(1000));
	}

	// initParticle初始化粒子
	public void initParticles(int num, int color, float xDir, float yDir,
			float zDir) {
		Particles par = new Particles();
		// 使所有粒子为激活状态
		par.active = true;
		// 所有粒子生命值为最大
		par.life = 1.0f;
		// 随机生成衰率(0~99)/1000+0.003f
		par.fade = rand() % 100 / 1000.0f + 0.003f;

		// 赋予粒子颜色分量r,g,b
		// r
		par.r = colors[color][0];
		// g
		par.g = colors[color][1];
		// b
		par.b = colors[color][2];

		// 设定粒子方向xi,yi,zi
		// xi
		par.xi = xDir;
		// yi
		par.yi = yDir;
		// zi
		par.zi = zDir;

		// x,y,z方向加速度
		// xg
		par.xg = 0.0f;
		// yg
		par.yg = -0.5f;
		// zg
		par.zg = 0.0f;

		particles[loop] = par;
	}

}
class LoadImag{
	public static Bitmap bitmap;
	public static Bitmap loadI(Resources res, int id){
		bitmap = BitmapFactory.decodeResource(res, id);
		return bitmap;
	}
}
