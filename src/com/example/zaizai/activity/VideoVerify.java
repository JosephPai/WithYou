package com.example.zaizai.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.Accelerometer;
import com.example.zaizai.R;
import com.example.zaizai.util.FaceRect;
import com.example.zaizai.util.FaceUtil;
import com.example.zaizai.util.ParseResult;

/**
 * 离线视频流检测示例
 * 该业务仅支持离线人脸检测SDK，请开发者前往<a href="http://www.xfyun.cn/">讯飞语音云</a>SDK下载界面，下载对应离线SDK
 */
public class VideoVerify extends Activity {
	private final static String TAG = VideoVerify.class.getSimpleName();
	private SurfaceView mPreviewSurface;
	private SurfaceView mFaceSurface;
	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;
	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;
	// 预览帧数据存储数组和缓存数组
	private byte[] nv21;
	private byte[] buffer;
	// 缩放矩阵
	private Matrix mScaleMatrix = new Matrix();
	// 加速度感应器，用于获取手机的朝向
	private Accelerometer mAcc;
	// FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
	private FaceDetector mFaceDetector;
	private boolean mStopTrack;
	private Toast mToast;
	private long mLastClickTime;
	private int isAlign = 0;
	
	//登录账户设置
	//EditText authorid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.videover_layout);
		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
		initUI();
		mFaceRequest = new FaceRequest(this);
		nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		mAcc = new Accelerometer(VideoVerify.this);
		mFaceDetector = FaceDetector.createDetector(VideoVerify.this, null);
		
		//authorid = (EditText)findViewById(R.id.authorid);
		
	}
	
	
	private Callback mPreviewCallback = new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			mScaleMatrix.setScale(width/(float)PREVIEW_HEIGHT, height/(float)PREVIEW_WIDTH);
		}
	};
	
	private void setSurfaceSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int width = metrics.widthPixels;
		int height = (int) (width * PREVIEW_WIDTH / (float)PREVIEW_HEIGHT);
		RelativeLayout.LayoutParams params = new LayoutParams(width, height);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		mPreviewSurface.setLayoutParams(params);
		mFaceSurface.setLayoutParams(params);
	}

	@SuppressLint("ShowToast")
	@SuppressWarnings("deprecation")
	private void initUI() {
		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);
		
		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mFaceSurface.setZOrderOnTop(true);
		mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		
		// 点击SurfaceView，切换摄相头
		mFaceSurface.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 只有一个摄相头，不支持切换
				if (Camera.getNumberOfCameras() == 1) {
					showTip("只有后置摄像头，不能切换");
					return;
				}
				closeCamera();
				if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
					mCameraId = CameraInfo.CAMERA_FACING_BACK;
				} else {
					mCameraId = CameraInfo.CAMERA_FACING_FRONT;
				}
				openCamera();
			}
		});
		
		// 长按SurfaceView 500ms后松开，摄相头聚集
		mFaceSurface.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastClickTime = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_UP:
					if (System.currentTimeMillis() - mLastClickTime > 500) {
						mCamera.autoFocus(null);
						return true;
					}
					break;
					
				default:
					break;
				}
				return false;
			}
		});
		
		RadioGroup alignGruop = (RadioGroup) findViewById(R.id.align_mode);
		alignGruop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.detect:
					isAlign = 0;
					break;
				case R.id.align:
					isAlign = 1;
					break;
				default:
					break;
				}
			}
		});
		
		setSurfaceSize();
		mToast = Toast.makeText(VideoVerify.this, "", Toast.LENGTH_SHORT);
	}
	
	private void openCamera() {
		if (null != mCamera) {
			return;
		}
		
		if (!checkCameraPermission()) {
			showTip("摄像头权限未打开，请打开后再试");
			mStopTrack = true;
			return;
		}
		
		// 只有一个摄相头，打开后置
		if (Camera.getNumberOfCameras() == 1) {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}
		
		try {
			mCamera = Camera.open(mCameraId);
			if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
				showTip("前置摄像头已开启，点击可切换");
			} else {
				showTip("后置摄像头已开启，点击可切换");
			}
		} catch (Exception e) {
			e.printStackTrace();
			closeCamera();
			return;
		}
		
		Parameters params = mCamera.getParameters();
		params.setPreviewFormat(ImageFormat.NV21);
		params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		mCamera.setParameters(params);
		
		// 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(new PreviewCallback() {
			
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				System.arraycopy(data, 0, nv21, 0, data.length);
			}
		});
		
		try {
			mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	private boolean checkCameraPermission() {
		int status = checkPermission(permission.CAMERA, Process.myPid(), Process.myUid());
		if (PackageManager.PERMISSION_GRANTED == status) {
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (null != mAcc) {
			mAcc.start();
		}
		
		mStopTrack = false;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!mStopTrack) {
					if (null == nv21) {
						continue;
					}
					
					synchronized (nv21) {
						System.arraycopy(nv21, 0, buffer, 0, nv21.length);
					}
					
					// 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
					int direction = Accelerometer.getDirection();
					boolean frontCamera = (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId);
					// 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
					// 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
					if (frontCamera) {
						// SDK中使用0,1,2,3,4分别表示0,90,180,270和360度
						direction = (4 - direction)%4;
					}

					if(mFaceDetector == null) {
						/**
						 * 离线视频流检测功能需要单独下载支持离线人脸的SDK
						 * 请开发者前往语音云官网下载对应SDK
						 */
						showTip("本SDK不支持离线视频流检测");
						break;
					}
					
					String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
					Log.d(TAG, "result:"+result);
					
					FaceRect[] faces = ParseResult.parseResult(result);
					
					Canvas canvas = mFaceSurface.getHolder().lockCanvas();
					if (null == canvas) {
						continue;
					}
					
					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
					canvas.setMatrix(mScaleMatrix);

					if( faces.length <=0 ) {
						mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
						continue;
					}
					
					if (null != faces && frontCamera == (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {
						for (FaceRect face: faces) {
							face.bound = FaceUtil.RotateDeg90(face.bound, PREVIEW_WIDTH, PREVIEW_HEIGHT);
							if (face.point != null) {
								for (int i = 0; i < face.point.length; i++) {
									face.point[i] = FaceUtil.RotateDeg90(face.point[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
								}
							}
							FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH, PREVIEW_HEIGHT,  
									frontCamera, false);
						}
						
						if(!isRegd && faces.length == 1){
							isRegd = true;
							// 拷贝到临时数据中
							byte[] tmp = new byte[nv21.length];
							System.arraycopy(nv21, 0, tmp, 0, nv21.length);
							
							register(Bitmap2Bytes(RotateDeg90(decodeToBitMap(tmp))));
						}
						
						
					} else {
						Log.d(TAG, "faces:0");
					}
					
					mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}).start();
	}
	// FaceRequest对象，集成了人脸识别的各种功能
	private FaceRequest mFaceRequest;
	private boolean isVerified =  false;
	private boolean isRegd = false;
	private String idnum;
	
	private void register(byte[] mImageData){
		if (null != mImageData) {
			//读取登陆ID
			SharedPreferences pref = getSharedPreferences("idnumber",
					MODE_PRIVATE);		
			idnum = pref.getString("numb", "0");
			// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
			// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
			mFaceRequest.setParameter(SpeechConstant.AUTH_ID, idnum.toString());
			mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
			mFaceRequest.setParameter("property","del");	//覆盖注册
			mFaceRequest.sendRequest(mImageData, new RequestListener() {
				
				@Override
				public void onEvent(int arg0, Bundle arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onCompleted(SpeechError arg0) {
					if(arg0 != null)
						Log.e(TAG, "error:"+arg0.getErrorCode());
				}
				
				@Override
				public void onBufferReceived(byte[] arg0) {
					try {
						String result = new String(arg0, "utf-8");
						Log.d("FaceDemo", "test");
						Log.d("FaceDemo", result);
						
						JSONObject object = new JSONObject(result);
						int ret = object.getInt("ret");
						if (ret != 0) {
							showTip("注册失败");
							return;
						}
						if ("success".equals(object.get("rst"))) {
																					
								int num=Integer.parseInt(idnum);
								num++;
								showTip("注册成功，你是本设备"+num+"号使用者");
								String idtemp=Integer.toString(num);
								SharedPreferences.Editor editor=getSharedPreferences("idnumber", MODE_PRIVATE).edit(); 
								editor.putString("numb", idtemp);
								editor.commit();
										
								Intent intent2 = new Intent(VideoVerify. this,com.example.zaizai.MainActivity.class);
								startActivity(intent2);
								
								finish();								
							}else {
							showTip("注册失败");
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO: handle exception
					}
				}
			});
		} else {
			showTip("请选择图片后再注册");
		}
		
	}
	
	/*private void verify(byte[] mImageData){
		if (null != mImageData) {
			//读取登陆ID
			Intent intent=getIntent();
			String idtemp=intent.getStringExtra("id");
			// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
			// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
			mFaceRequest.setParameter(SpeechConstant.AUTH_ID, idtemp);
			mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
			mFaceRequest.sendRequest(mImageData, new RequestListener() {
				
				@Override
				public void onEvent(int arg0, Bundle arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onCompleted(SpeechError arg0) {
					if(arg0 != null)
						Log.e(TAG, "error:"+arg0.getErrorCode());
				}
				
				@Override
				public void onBufferReceived(byte[] arg0) {
					try {
						String result = new String(arg0, "utf-8");
						Log.d("FaceDemo", "test");
						Log.d("FaceDemo", result);
						
						JSONObject object = new JSONObject(result);
						int ret = object.getInt("ret");
						if (ret != 0) {
							showTip("验证失败");
							return;
						}
						if ("success".equals(object.get("rst"))) {
							if (object.getBoolean("verf")) {
								showTip("通过验证，欢迎回来！");
								//回传数据，登陆成功
								/*Intent intent = new Intent();
								intent.putExtra("launch_return", 1);
								setResult(RESULT_OK,intent);*/
								//finish();
								
								/*Intent intent2 = new Intent(VideoVerify. this,com.example.zaizai.MainActivity.class);
								startActivity(intent2);
								
								finish();
								
							} else {
								showTip("验证不通过");
							}
						} else {
							showTip("验证失败");
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO: handle exception
					}
				}
			});	//finish();
		} else {
			showTip("请选择图片后再验证");
		}
		
	}*/
	private Bitmap decodeToBitMap(byte[] data) {
		try {
			YuvImage image = new YuvImage(data, ImageFormat.NV21, PREVIEW_WIDTH,
					PREVIEW_HEIGHT, null);
			if (image != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT),
						80, stream);
				Bitmap bmp = BitmapFactory.decodeByteArray(
						stream.toByteArray(), 0, stream.size());
				stream.close();
				return bmp ;
			}
		} catch (Exception ex) {
			Log.e("Sys", "Error:" + ex.getMessage());
		}
		return null;
	}
	private Bitmap RotateDeg90(Bitmap bmp)
	{
		// 定义矩阵对象  
		Matrix matrix = new Matrix();  
	    // 缩放原图  
	    matrix.postScale(1f, 1f);  
	    // 向左旋转45度，参数为正则向右旋转  
	    matrix.postRotate(-90);  
	    //bmp.getWidth(), 500分别表示重绘后的位图宽高  
	    Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),  
	            matrix, true);
		return dstbmp;  
	}
    
	
	private byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	@Override
	protected void onPause() {
		super.onPause();
		closeCamera();
		if (null != mAcc) {
			mAcc.stop();
		}
		mStopTrack = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁对象
		mFaceDetector.destroy();
	}
	
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

}
