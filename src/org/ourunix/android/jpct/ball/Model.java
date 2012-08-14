package org.ourunix.android.jpct.ball;


import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.World;

public class Model {
	public static final String	m3gFile					= "/pool.3ds";
	static World				w;					//--世界节点
	//static Graphics3D			g3d;				//--用于描绘3D图像的对象

	static final int			BALLNUM				= 10;//--总共10个球
	static Object3D					BallModel[];		//--球模型节点
	static float				BallPos[][]			= new float[BALLNUM][3];	//球的坐标
	
	static Object3D					CueModel;			//球杆模型节点
	static float 				CuePos[]	= new float[3];	//球杆的位置
	static float 				CueGroupPos[] = new float[3];	//球杆组的位置
	
	static Object3D					TableModel;		//台的模型.
	static Object3D					PlaneSign[][];		//移动提示标志模型节点
	           //static Background			bgModel;
	//	static Sprite3D				handSprite;
		
	//	static Group				CueCamGroup;		//一个现场图表节点,保存一系列无序的子节点
		
		        //static Group				gameCameraGroup;	//--一个现场图表节点,保存一系列无序的子节点
		static Camera				gameCameraUPMID;	//正上方空中的
		static Camera				gameCameraUPFRO;	//台竖的--不要了
		
		static Camera				gameCameraCue;		//击球时的可视角度
		static Camera				gameCamera0;			//洞0处的镜头
		static Camera				gameCamera1;			//洞1处的镜头
		static Camera				gameCamera2;			//洞2处的镜头
		static Camera				gameCamera3;			//洞3处的镜头
		static Camera				gameCamera4;			//洞4处的镜头
		static Camera				gameCamera5;			//洞5处的镜头	
		
		static Camera				FPCamera;//frontpage的
		

		//----定义节点在M3G文件中的ID位置
		//0=134  1=58   2=133   3=136   4=135   5=137
		
		static final int			ID_TABLE0_CAMERA		= 126;
		static final int			ID_TABLE1_CAMERA		= 58;
		static final int			ID_TABLE2_CAMERA		= 125;
		static final int			ID_TABLE3_CAMERA		= 128;
		static final int			ID_TABLE4_CAMERA		= 127;
		static final int			ID_TABLE5_CAMERA		= 129;	
		static final int			ID_CUE_CAMERA			= 149;//杆的镜头?	
		static final int			ID_FP_CAMERA			= 155;
		
		static final int			ID_GAME_LIGHT2			= 156;
		static final int			ID_GAME_LIGHT1			= 2;
		
		static final int			ID_TABLE_MODEL			= 13;
		
		//--白球,1~9号球  彩球...,黑球
		//static final int[]			ID_BALL_MODELS		= {57,24,69,80,91,102,113,124,46,35};	//球的节点位
		//static final int[]			ID_BALL_MODELS			= {57,24,113,102,69,91,124,46,80,35};	//球的节点位
		static final int[]			ID_BALL_MODELS			= {57,24,69,80,91,102,113,124,46,35};	//球的节点位
		
		static final int			ID_CUE_MODEL			= 144;	//木杆
		static final int			ID_CUECAM_GROUP			= 150;
		static final int[]			ID_PLANES				= {172,164};//球的路径提示贴图
		static final int			ID_BG					= 173;	
		

		static Light l1,l2;
		
}
