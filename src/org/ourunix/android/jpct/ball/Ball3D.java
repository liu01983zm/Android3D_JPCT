package org.ourunix.android.jpct.ball;
import java.util.Random;

import com.threed.jpct.Object3D;


/*
 * 功能说明:此类为球的对象类,包括了球的所有参数值,控制球移动的函数
 * 球的绘制方法等 
 * 
 */

class Ball3D
{	
	static final float	TOP_SPEED		= 6.0f;		//击球最高速
	static final float	MIN_SPEED		= 0.6f;		//击球最低速
	static final float	DEFAULT_SPEED	= 3.6f;		//
	
	static final float	WEIGHT			= 1.5f;		//重量
	static final float	RADIUS			= 1.25f;	//-球半径
	
	static final float	stopvi			= 0.1f;		//球子停止移动的速度
	static float		friction 		= 0.0338f;	//球子与台的磨擦力,因速度不同而改变,这里只用定值
	
	Object3D				model;				//--球子模型在m3g里的节点
	int 				index;				//--球子号码	
	static int			whiteballnum 	= 0;	//index为0的是白球	
	
	float[]				position		= new float[3];	//球的xyz位置
	float[]				velocity		= new float[2];	//球的速度向量
	float[]				extForce		= new float[2];	//外界的其它力的方向向量,回旋力与前旋力
	
	float				power			= 0;					//球前进的力量.	
	float				direction		= 0;	//球打出的移动方向角度0~360,默认为0,就是右
					
	//两个旋转参数的范围为-30到30,绝对值越大,打完后球的转速越高
	float					MAXCIR			= 50;	//最快速度的旋转为1贞60度	
	float					UDcir			= 0;	//上下旋转比例
	float					Zcir			= 0;	//左右旋转比例
	//	球当时对台的磨擦动力	
	float				nowUDPower		= 0;	//(float)(2*7*UDcir*3.1416f/180*RADIUS)
	float				UDpower			= 0;	//前后旋最终要达到的力大小
	float				UDsignx			= 0;	//标志球原来开始回旋的速度正负方向
	float				UDsigny			= 0;	//	
	
	//球自转的轴的角速度与角度变量
	//---前后旋有可能系缘X或Y转的,要看在哪个位置打,而左右旋则一定缘Z转转的.	
	float					xcir			= 0;	//球在台上进行上下滚动时自转角速度
	float					ycir			= 0;	//球在台上进行左右滚动时发生缘Y轴的自转角速度
	float					xyanglev		= 0;	//自由前滚的自转角速度
	float					udanglev		= 0;	//前后旋转的自转角速度
	float					zanglev			= 0;	//球在台上进行左右旋转时缘Z轴的自转角速度		

	
	boolean				willinPocket	= false;//球将要进的标志,用来做是否画球下袋动画的判断.
	float[]				willposi		= new float[2];	//球进洞的位置,进了的移到这显示,好看些.	
	//boolean				willColl		= false;		//判断球是否将会碰撞,因为有碰撞,此贞不再移动位置标志
	boolean				inPocket		= false;//球已进的标志	
	
	//-----游戏规则或其它里用到的,每打一手都重置参数一次的.
	boolean				istouchwall 	= false;	//是否碰到墙
	int					inwhichp		= -1;		//记录下球掉进的是第几号的球洞0 1 2 3 4 5		]
	boolean				isindirectIN	= false;	//是否间接的进球.	
	
	
	//---游戏里出杆前统计球在每一贞的移动路径的参数..
	//boolean	recordposition	= false;
	static final int	runsLength		=	200;
	float	runsPosition[][]			= new float[runsLength][3];	//参数为贞,X,Y 用来记录每杆所有球经过的位置	
	float	runsState[][] 				= new float[runsLength][6];	//udcir,z,direction记录当时球的旋转方向
	
	
	float tempud=0,tempxy=0,tempz=0;//用来记录球的旋转度数
	
	Ball3D(int _index)
	{
		try{
			index = _index;	//--球号	
			
//			if (index < Model.BALLNUM)//如果大于10个球则是临时用的球,不用与模型对应...
//				model = Model.BallModel[index];//--取得Model中已经从M3G中取得的车辆节点	
			
			resetBall();	

		}catch(Exception e){System.out.println("new ball error"+e);}
		//init();
	}
	
	//--初始化球的所有信息
	void resetBall()
	{
		tempud=0;
		tempxy=0;
		tempz=0;
		
		//torque = 0;		//不转弯
		power = 0;		//不动			
		direction = 0;//360 288 216 144 72 五个车的车头方向????		
		
		velocity[0] = 0;	//速度向量
		velocity[1] = 0;
		//angVelocity = 0;	//角速度
		
		extForce[0] =0;		//外在力向量
		extForce[1] =0;				
		
		//model = Model.BallModel[index];//--取得Model中已经从M3G中取得的车辆节点				
		
//		if (index < Model.BALLNUM)
//		{
//			position[0] = Model.BallPos[index][0];//---取得Model中模型的设定的坐标
//			position[1] = Model.BallPos[index][1];//---
//			position[2] = Model.BallPos[index][2];//---
//		}
		
		willinPocket	= false;
		inPocket		= false;
		inwhichp		= -1;
		isindirectIN    = false;
		willposi[0]		= 0;
		willposi[1]		= 0;	
		
		//LRcir			= 0;
		UDcir			= 0;
		UDpower			= 0;
		nowUDPower		= 0;
		Zcir			= 0;
		zanglev			= 0;
		udanglev		= 0;
		
		xyanglev		= 0;	
		xcir			= 0;
		ycir			= 0;				

		UDsigny			= 0;
		UDsignx			= 0;
		
		//willColl		= false;
		//collPosition[0]	= 0;
		//collPosition[1] = 0;
		
//		if (index < Model.BALLNUM)
//			resetRunsposition();
		
		//updateballModel();
	}
	
	
	//控制放置白球的位置
	float moveoffsetxy = 0.7f;
	public void moveUp()
	{
		position[1]+=moveoffsetxy;
		//Map.m_cuegroupPos[1]+=moveoffsetxy;
	}
	public void moveDown()
	{
		position[1]-=moveoffsetxy;
		//Map.m_cuegroupPos[1]-=moveoffsetxy;
	}
	public void moveLeft()
	{
		position[0]-=moveoffsetxy;
		//Map.m_cuegroupPos[0]-=moveoffsetxy;
	}
	public void moveRight()
	{
		position[0]+=moveoffsetxy;
		//Map.m_cuegroupPos[0]+=moveoffsetxy;
	}

	//打球力度------------
	public void hitBall()//前进加速
	{
//		power = Map.m_hitpower;	//TOP_SPEED;	
//		Map.m_framepower = Map.m_hitpower;	//因应打球力度调整击球杆速度
//		
//		Map.m_hitpower = DEFAULT_SPEED;		//
//		
//		
//		Map.runscounter = 0;//---设一打球时球的移动贞数重置
//		Map.maxrunscounter = 0;
		
		if (UDcir < 0)//
		{
			//-----effectPower = -(float)(UDcir*3.1416/180*RADIUS);//当UDcir为60以内时,最大的值为1.3
			//effectPower = effectPower/2.0f*power;		//求出大约回力
			UDpower = (float)(-2f/7f*UDcir*3.1416f/180f*RADIUS)*power/TOP_SPEED;//求出旋转力最终要达到的大小0.374?  2/7
			nowUDPower = 0;
		}
		else if (UDcir > 0)//
		{
			//-----effectPower = -(float)(UDcir*3.1416/180*RADIUS);//当UDcir为60以内时,最大的值为1.3
			//effectPower = effectPower/2.0f*power;		//求出大约回力
			UDpower = (float)(2f/7f*UDcir*3.1416f/180f*RADIUS)*power/TOP_SPEED;//求出旋转力最终要达到的大小0.374?  2/7
			nowUDPower = 0;
		}
		
//		Map.m_playershotnum[Map.m_whoturn]++;//打球者的每局打的杆数增加
	}	
	
	
	public void turnLeft()//转左
	{	
		//如果进行了移动白球的打出角度,就要更新一次白球碰到第一个球后的路径		
//		direction += Map.turnangle;//--球的出发方向改变
		if (direction >=360)
			direction -= 360;
		//System.out.println(""+Map.turnangle);
	}
	public void turnRight()//转右
	{		
//		direction -= Map.turnangle;
		if (direction <0)
			direction += 360;
	}
	
	
	//控制击打球点位置-----------
	public void hitLeft()
	{		
		Zcir -= 3;
		if (!checkHitpoint()) 
		{
			///第二次判断,让球在边上移动
			if (UDcir>0) UDcir-=3;
			else if (UDcir<0) UDcir+=3;
			if (!checkHitpoint()) 
			{
				Zcir += 3;
				return;
			}
		} 
	}
	public void hitRight()
	{
		Zcir += 3;
		if (!checkHitpoint()) 
		{
			if (UDcir>0) UDcir-=3;
			else if (UDcir<0) UDcir+=3;
			if (!checkHitpoint()) 
			{
				Zcir -= 3;
				return;
			}
		}
	}
	public void hitUp()
	{
		UDcir += 3;
		if (!checkHitpoint()) 
		{
			if (Zcir>0) Zcir-=3;
			else if (Zcir<0) Zcir+=3;
			if (!checkHitpoint()) 
			{
				UDcir -= 3;
				return; 
			}
		}
	}
	public void hitDown()
	{
		UDcir -= 3;
		if (!checkHitpoint()) 
		{
			if (Zcir>0) Zcir-=3;
			else if (Zcir<0) Zcir+=3;
			if (!checkHitpoint()) 
			{
				UDcir += 3;
				return; 
			} 
		}
	}
	public boolean checkHitpoint()
	{
		int x1=0,y1=0;
//		x1-=Map.m_whiteball.Zcir;
//		y1-=Map.m_whiteball.UDcir;
//		
		//控制选择击点值的范围
		if ((float)Math.sqrt((double)(x1*x1+y1*y1)) > MAXCIR)
			return false;		
		return true;
	}
	//---------
	
	
	/*
	 *	算出球的未来的速度,与未来的方向. 
	 *	包括要计算球的回旋力或前旋力
	 */
	public void ballmove()
	{	
		//--toRadians  角度变弧度
		//1弧度=PI/180   弧度=角度*PI/180
		//--求出球的移动角度方向向量
		float[] dir = { 0, 0 };
		//牵引力 移动的方向向量*移动的速度=球要移动的x,y坐标
		float[] tractiveForce = {0,0};//只在打球的一瞬间有
		
		if (power!=0)//优化算法用....只有力量时才运算.
		{			
			dir[0]=(float) Math.cos(Math.toRadians(direction));
			dir[1]=(float) Math.sin(Math.toRadians(direction));
			tractiveForce[0]=dir[0] * power;
			tractiveForce[1]=dir[1] * power;   
			
			if (Tools.myabs(tractiveForce[0]) < stopvi) tractiveForce[0]=0;
			if (Tools.myabs(tractiveForce[1]) < stopvi) tractiveForce[1]=0;			
				
			power = 0;//--打完球后打的力量就消失了,之后就靠惯性或旋转力了.
		}	
		
		
		//让磨擦力随着球的速度不同而改变
		/*float xx,yy;
		xx=Tools.myabs(velocity[0]);		
		yy = Tools.myabs(velocity[1]);
		
		if (xx>=7f || yy>=7f)
			friction = 0.033f;		
		else if (xx>=6f || yy>=6f)
			friction = 0.033f;
		else if (xx>=5f || yy>=5f)
			friction = 0.033f;
		else if (xx>=4f || yy>=4f)
			friction = 0.033f;
		else if (xx>=3f || yy>=3f)
			friction = 0.033f;
		else if (xx>=2f || yy>=2f)
			friction = 0.033f;
		else if (xx>=1f || yy>=1f)
			friction = 0.033f;
		else if (xx>=0.4f || yy>=0.4f)
			friction = 0.033f;
		else*/
			//friction = 0.033f;////不用if ,这样就行优化算法...
		
		float[] frictionalForce = { -velocity[0] * friction, -velocity[1] * friction };		
		float[] netForce = { 0, 0 };	//球移动的净移动X,Y值,所有力之和	
		
		/*float[]	bv = {0,0};
		bv[0] = velocity[0];
		bv[1] = velocity[1];*/		
		//float nowpower = (float)Math.sqrt(velocity[0]*velocity[0]+velocity[1]*velocity[1]);
		
		if (UDcir < 0)//回旋球的
		{		
			nowUDPower += UDpower/20f;//回旋力加大				
			
			//System.out.println("nowUDPower:"+nowUDPower+" UDpower:"+UDpower);
			if (nowUDPower >= UDpower)//如果回旋力已经到了最高峰,则让速度由磨擦力停下
			{
				//System.out.println("end cir");
				UDcir = 0;
				nowUDPower = 0;
			}
			else//将速度加上回旋方向的速度
			{				
				//System.out.println(velocity[0]+" "+velocity[1]);
				//System.out.println("reduce v "+UDsignx+" "+UDsigny);
				float x1,y1;
				//此处球的direction值是球本来向前移动的方向,但回旋时,当移动球的速度突然变为0
				//则球就有可能向后移动,但球此时的direction仍是向前的..
				x1 =Tools.myabs(nowUDPower/2*(float)Math.cos(Math.toRadians(direction)));
				y1 =Tools.myabs(nowUDPower/2*(float)Math.sin(Math.toRadians(direction)));
				extForce[0] = -UDsignx*x1;				
				extForce[1] = -UDsigny*y1;				
			}	
			
		}
		else if (UDcir > 0)//前旋球的
		{
			nowUDPower += UDpower/20f;//前旋力加大	

			if (nowUDPower >= UDpower)//如果回旋力已经到了最高峰,则让速度由磨擦力停下
			{
				//System.out.println("end cir");
				UDcir = 0;
				nowUDPower = 0;
			}
			else//将速度加上回旋方向的速度
			{				
				//System.out.println(velocity[0]+" "+velocity[1]);
				//System.out.println("reduce v "+UDsignx+" "+UDsigny);
				float x1,y1;
				x1 =Tools.myabs(nowUDPower/2*(float)Math.cos(Math.toRadians(direction)));
				y1 =Tools.myabs(nowUDPower/2*(float)Math.sin(Math.toRadians(direction)));
				extForce[0] = UDsignx*x1;				
				extForce[1] = UDsigny*y1;				
			}	
		}		
		
		//总的方向向量为前进的加反现的摩擦力与外界的反弹力之和
		netForce[0] = frictionalForce[0] + tractiveForce[0] + extForce[0];
		netForce[1] = frictionalForce[1] + tractiveForce[1] + extForce[1];	
				
		/*if (Map.runscounter>2 && runsPosition[Map.runscounter-1][0] == runsPosition[Map.runscounter-2][0]&&
			runsPosition[Map.runscounter-1][1] == runsPosition[Map.runscounter-2][1]&&		
			runsPosition[Map.runscounter-1][2] == runsPosition[Map.runscounter-2][2])
		{
			//--如果球没有移动过就不减速
			
		}
		else
		{*/
			velocity[0] += netForce[0];//原来的速度向量加上 净增力向量来改变速度
			velocity[1] += netForce[1];	
		//}
		
		extForce[0] = 0;
		extForce[1] = 0;
		
		if (UDsignx == 0 && UDsigny == 0)//没记过的就记下之前移动的XY符号
		{
			UDsignx = numbersign(velocity[0]);
			UDsigny = numbersign(velocity[1]);
		}					
		
		///--如果速度太慢了,就停了他.因为前边的减速参数是以速度为单位的,如果不判断会一直动下去.
		if (UDcir==0 && Tools.myabs(velocity[0])<stopvi && Math.abs(velocity[1])<stopvi)
		{
			velocity[0] = 0;
			velocity[1] = 0;
			UDsignx = 0;
			UDsigny = 0;
			UDcir = 0;
			nowUDPower = 0;		
		}	

	}
	
	//--跟据已知的球的速度与方向移动球体
	public void recordruns() //更新球子位置等信息
	{			
		if (direction >= 360)//---球的移动方向角度
			direction -= 360;
		if (direction < 0)
			direction += 360;	
		
		//记录下每贞时每个球的位置
//		runsPosition[Map.runscounter][0] = position[0];
//		runsPosition[Map.runscounter][1] = position[1];		
//		runsPosition[Map.runscounter][2] = position[2];	
//		
//		runsState[Map.runscounter][0]	 = direction;
//		
//		float lv = (float)Math.sqrt(velocity[0]*velocity[0]+velocity[1]*velocity[1]);
//		float av = (float)(lv/(3.1416f/180f*RADIUS));//自然旋转时前滚的角速度,也就是Y轴旋转增加量
//		runsState[Map.runscounter][3]	 = av;
//		runsState[Map.runscounter][4]	 = -numbersign(velocity[0]);
//		runsState[Map.runscounter][5]	 = -numbersign(velocity[1]);
//		
//		if (runsState[Map.runscounter][3]!=0) udanglev = UDcir*2;//如果速度为0时不转动,因为此时转动轴为0
//		else	udanglev = 0;		
//		runsState[Map.runscounter][1]	 = udanglev;
//		zanglev = Zcir*2;
//		runsState[Map.runscounter][2]	 = zanglev;
		
		//updateballModel();//此处已不用再更新球的显示位置,但可以在这里用于测试查看
	}
	
	public void updatePosition()
	{		
//		position[0] = runsPosition[Map.runscounter][0];
//		position[1] = runsPosition[Map.runscounter][1];
//		position[2] = runsPosition[Map.runscounter][2];	
//		
//		direction 	= runsState[Map.runscounter][0];
//		udanglev    = runsState[Map.runscounter][1];
//		zanglev 	= runsState[Map.runscounter][2];
//		xyanglev	= runsState[Map.runscounter][3];
//		xcir		= runsState[Map.runscounter][4];
//		ycir		= runsState[Map.runscounter][5];		
		
		
		updateballModel();
	}
	
	//--初始化球的移动路径
	public void resetRunsposition()
	{
		for (int i = 0; i <runsLength; i++)
		{
			runsPosition[i][0] = -99;
			runsPosition[i][1] = -99;
			runsPosition[i][2] = -99;
			runsState[i][0]	   = 0;
			runsState[i][1]	   = 0;
			runsState[i][2]	   = 0;
			runsState[i][3]	   = 0;
			runsState[i][4]	   = 0;
			runsState[i][5]	   = 0;
		//	Game.playsoundlist[i] = -1;
		}
	}
	

	
	public void updateballModel() 
	{				
		//--按扭矩算出显示出要显示的动态模型	
		try{
			//model.animate(0);
			
//			model.setTranslation(position[0], position[1], position[2]);//设置模型的平移到位置的坐标
//			
//
//			tempz += -zanglev;
//			tempud += udanglev;
//			tempxy += xyanglev;
//			if (zanglev!=0)
//			{
//				//model.preRotate(-zanglev,0,0,1);
//				model.setOrientation(tempz, 0, 0, 1);//--设置模型的顺时针旋转的角度与旋转轴
//			}
//			else if (udanglev!=0)
//			{
//				//model.preRotate(udanglev,ycir,-xcir, 0);//加速后旋
//				model.setOrientation(tempud, ycir, -xcir, 0);//--设置模型的顺时针旋转的角度与旋转轴
//			}
//			else if (xyanglev!=0)
//			{
//				//model.preRotate(xyanglev,ycir,-xcir, 0);
//				model.setOrientation(tempxy, ycir, -xcir, 0);//--设置模型的顺时针旋转的角度与旋转轴
//			}
			
		
			//设置新转动的角度击中球的位置的左右旋转参数,如果没有左右旋转的就以球的前后击中方向旋转,
			//如果是击中球心则以球移动方向来向前进的方向转动
			/*if (zanglev!=0)
			{
				model.preRotate(-zanglev,0,0,1);
			}
			else if (udanglev!=0)
			{
				//if (UDcir>0)
					model.preRotate(udanglev,ycir,-xcir, 0);//加速后旋
				//else
				//	model.preRotate(udanglev,-numbersign(ycir),numbersign(xcir), 0);//加速前旋
			}
			else
				model.preRotate(xyanglev,ycir,-xcir, 0);*/
			
			
			udanglev = 0;
			xyanglev = 0;xcir = 0;ycir = 0;//不播放动画时不转
		}
		catch(Exception e)
		{System.out.println("updateballModel error:"+e);
		}
	}
	
	//返回球是否在移动
	public boolean isballMove()
	{
		if((velocity[0] == 0 && velocity[1] == 0) || inPocket)
			return false;
		return true;
	}
	
	public int numbersign(float b)
	{
		if (b == 0.0f)
			return 0;
		return (int)(Tools.myabs(b)/(float)b);
	}
	
}








