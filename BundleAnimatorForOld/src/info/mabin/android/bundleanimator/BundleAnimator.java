package info.mabin.android.bundleanimator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import info.mabin.android.bundleanimator.ObjectAnimatorInfo;

public class BundleAnimator implements BundleAnimatorListener{
	private static final int FRAME_TERM = 16;	// 60 fps
	
	private BundleAnimator instance;
	
	private Handler handler = new Handler();
	
	private boolean isStarted = false;
	private boolean isCanceled = false;
	
	private float playSpeed = 1;
	
	private List<BundleAnimatorListener> listListener = new ArrayList<BundleAnimatorListener>();
	
	private long duration = 0;
	
	private long currentPlaytime = 0;
	
	private ObjectAnimatorInfo[] arrTargetAnimatorInfo;
	private android.animation.ObjectAnimator[] arrTargetAnimatorNew;
	private com.nineoldandroids.animation.ObjectAnimator[] arrTargetAnimatorOld;
	
	private Object target = null;

	private static Method methodSetTarget;
	private static Method methodSetRealCurrentPlayTime;
	
	static{
		String postFix = "Old";

		try {
			BundleAnimator.class.getClassLoader().loadClass("android.animation.ObjectAnimator");
			
			Log.d("ObjectAnimator Version", "Native (API 11)");
			postFix = "New";
		} catch (ClassNotFoundException e) {
			Log.d("ObjectAnimator Version", "NineOldAndroids");
			postFix = "Old";
		}
		
		try {
			methodSetTarget = BundleAnimator.class.getDeclaredMethod("setTarget" + postFix, Object.class);
			methodSetRealCurrentPlayTime = BundleAnimator.class.getDeclaredMethod("setRealCurrentPlayTime" + postFix, long.class);
		} catch (Exception e) {
			e.printStackTrace();
		}	

	}

	public BundleAnimator(){
		instance = this;
	}
	
	public void addListener(BundleAnimatorListener animatorListener){
		listListener.add(animatorListener);
	}
	
	public void setCurrentPlayTime(long targetMilliSec){
		this.setRealCurrentPlayTime((long) (targetMilliSec * (float) playSpeed));
	}
	
	protected void setRealCurrentPlayTime(long targetMilliSec){
		try {
			methodSetRealCurrentPlayTime.invoke(instance, targetMilliSec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setRealCurrentPlayTimeNew(long targetMilliSec){
		if(isStarted == false){
			this.onAnimationStart(this);
			
			isStarted = true;
		}
		
		this.currentPlaytime = targetMilliSec;
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			long startDelay = arrTargetAnimatorInfo[i].getStartDelay();
			if(startDelay > targetMilliSec){
				arrTargetAnimatorNew[i].setCurrentPlayTime(0);
			} else {
				long tmpTargetMilliSec = targetMilliSec;
				tmpTargetMilliSec = targetMilliSec - startDelay;
				arrTargetAnimatorNew[i].setCurrentPlayTime(tmpTargetMilliSec);
			}
		}
		
		this.onAnimationPlaying(this, (long) (currentPlaytime / (float)playSpeed));
	}
	
	protected void setRealCurrentPlayTimeOld(long targetMilliSec){
		if(isStarted == false){
			this.onAnimationStart(this);
			
			isStarted = true;
		}
		
		this.currentPlaytime = targetMilliSec;
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			long startDelay = arrTargetAnimatorInfo[i].getStartDelay();
			if(startDelay > targetMilliSec){
				arrTargetAnimatorOld[i].setCurrentPlayTime(0);
			} else {
				long tmpTargetMilliSec = targetMilliSec;
				tmpTargetMilliSec = targetMilliSec - startDelay;
				arrTargetAnimatorOld[i].setCurrentPlayTime(tmpTargetMilliSec);
			}
		}
		
		this.onAnimationPlaying(this, (long) (currentPlaytime / (float)playSpeed));
	}
	
	public long getCurrentPlayTime(){
		return (long) (this.currentPlaytime / (float)playSpeed);
	}
	
	public void start(){
		if(isStarted == false){
			this.onAnimationStart(this);
			
			isStarted = true;
		}
		
		currentPlaytime = 0;
		this.onAnimationStart(this);
		
		resume();
	}
	
	public void cancel(){
		isCanceled = true;
		this.onAnimationCancel(this);
	}
	
	public void resume(){
		if(isStarted == false){
			this.onAnimationStart(this);
			
			isStarted = true;
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if(currentPlaytime > duration){
					currentPlaytime = duration;
					setRealCurrentPlayTime(currentPlaytime);
					BundleAnimator.this.onAnimationEnd(BundleAnimator.this);
				} else {
					currentPlaytime += (FRAME_TERM * (float) playSpeed);
					setRealCurrentPlayTime(currentPlaytime);
					if(isCanceled){
						isCanceled = false;
					} else {
						resume();
					}
				}
			}
		};
		
		handler.postDelayed(runnable, FRAME_TERM);
	}
	
	public void reverse(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if(currentPlaytime < 0){
					currentPlaytime = 0;
					setRealCurrentPlayTime(currentPlaytime);
					BundleAnimator.this.onAnimationEndReverse(BundleAnimator.this);
					isStarted = false;
				} else {
					currentPlaytime -= FRAME_TERM * (float) playSpeed;
					setRealCurrentPlayTime(currentPlaytime);
					if(isCanceled){
						isCanceled = false;
					} else {
						reverse();
					}
				}
			}
		};
		
		handler.postDelayed(runnable, FRAME_TERM);
	}

	public void setTarget(Object target){
		try {
			methodSetTarget.invoke(instance, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setTargetNew(Object target){
		playSpeed = 1;
		isStarted = false;
		this.target = target;

		arrTargetAnimatorNew = new android.animation.ObjectAnimator[arrTargetAnimatorInfo.length];
		
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			Object currentTargetArgs;
			currentTargetArgs = arrTargetAnimatorInfo[i].getArgs();
			
			if(currentTargetArgs.getClass().getCanonicalName().equals("float[]")){
				arrTargetAnimatorNew[i] = android.animation.ObjectAnimator.ofFloat(
						target, 
						arrTargetAnimatorInfo[i].getProperty(), 
						(float[])currentTargetArgs);
			} else {
				arrTargetAnimatorNew[i] = android.animation.ObjectAnimator.ofInt(
						target, 
						arrTargetAnimatorInfo[i].getProperty(), 
						(int[])currentTargetArgs);				
			}
			
			arrTargetAnimatorNew[i].setDuration(arrTargetAnimatorInfo[i].getDuration());
			arrTargetAnimatorNew[i].setStartDelay(arrTargetAnimatorInfo[i].getStartDelay());
			arrTargetAnimatorNew[i].setInterpolator(arrTargetAnimatorInfo[i].getInterpolator());
		}
	}

	protected void setTargetOld(Object target){
		playSpeed = 1;
		isStarted = false;
		this.target = target;

		arrTargetAnimatorOld = new com.nineoldandroids.animation.ObjectAnimator[arrTargetAnimatorInfo.length];
		
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			Object currentTargetArgs;
			currentTargetArgs = arrTargetAnimatorInfo[i].getArgs();
			
			if(currentTargetArgs.getClass().getCanonicalName().equals("float[]")){
				arrTargetAnimatorOld[i] = com.nineoldandroids.animation.ObjectAnimator.ofFloat(
						target, 
						arrTargetAnimatorInfo[i].getProperty(), 
						(float[])currentTargetArgs);
			} else {
				arrTargetAnimatorOld[i] = com.nineoldandroids.animation.ObjectAnimator.ofInt(
						target, 
						arrTargetAnimatorInfo[i].getProperty(), 
						(int[])currentTargetArgs);				
			}
			
			arrTargetAnimatorOld[i].setDuration(arrTargetAnimatorInfo[i].getDuration());
			arrTargetAnimatorOld[i].setStartDelay(arrTargetAnimatorInfo[i].getStartDelay());
			arrTargetAnimatorOld[i].setInterpolator(arrTargetAnimatorInfo[i].getInterpolator());
		}
	}
	
	public long getPlayTime(){
		return (long) (currentPlaytime / (float) playSpeed);
	}

	public long getDuration() {
		return (long) (duration / (float) playSpeed);
	}
	
	public void setDuration(long newDuration){
		playSpeed = duration / (float) newDuration;
	}

	public void setArrAnimatorInfo(ObjectAnimatorInfo[] arrTargetAnimatorInfo) {
		this.arrTargetAnimatorInfo = arrTargetAnimatorInfo;
		
		duration = arrTargetAnimatorInfo[0].getDuration()
				+ arrTargetAnimatorInfo[0].getStartDelay();
		
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			long tmpDuration = arrTargetAnimatorInfo[i].getDuration()
					+ arrTargetAnimatorInfo[i].getStartDelay();
			if(tmpDuration > duration){
				duration = tmpDuration;
			}
		}
		
		if(target != null)
			setTarget(target);
	}
	
	public boolean isSettedTarget(){
		if(target != null)
			return true;
		
		return false;
	}

	@Override
	public void onAnimationStart(BundleAnimator animation) {
		for(BundleAnimatorListener listener: listListener){
			listener.onAnimationStart(animation);
		}
	}

	@Override
	public void onAnimationEnd(BundleAnimator animation) {
		for(BundleAnimatorListener listener: listListener){
			listener.onAnimationEnd(animation);
		}
		
		isStarted = false;
	}

	@Override
	public void onAnimationCancel(BundleAnimator animation) {
		for(BundleAnimatorListener listener: listListener){
			listener.onAnimationCancel(animation);
		}
	}

	@Override
	public void onAnimationRepeat(BundleAnimator animation) {
		for(BundleAnimatorListener listener: listListener){
			listener.onAnimationRepeat(animation);
		}
	}

	@Override
	public void onAnimationPlaying(BundleAnimator animation, long currentTime) {
		for(BundleAnimatorListener listener: listListener){
			listener.onAnimationPlaying(animation, currentTime);
		}
	}

	@Override
	public void onAnimationEndReverse(BundleAnimator animator) {
		for(BundleAnimatorListener listener: listListener){
			listener.onAnimationEndReverse(animator);
		}
	}
}
