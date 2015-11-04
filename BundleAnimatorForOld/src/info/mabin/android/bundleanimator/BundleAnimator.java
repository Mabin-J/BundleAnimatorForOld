package info.mabin.android.bundleanimator;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.util.Log;
import info.mabin.android.bundleanimator.ObjectAnimatorInfo;

public abstract class BundleAnimator implements BundleAnimatorListener{
	protected static final int FRAME_TERM = 16;	// 60 fps
	protected static final String[] API11_TESTED_DEVICES = {
		"Nexus"
	};
	
	protected BundleAnimator instance;
	
	protected Handler handler = new Handler();
	
	protected boolean isStarted = false;
	protected boolean isCanceled = false;
	
	protected float playSpeed = 1;
	
	protected List<BundleAnimatorListener> listListener = new ArrayList<BundleAnimatorListener>();
	
	protected long duration = 0;
	
	protected long currentPlaytime = 0;
	
	protected ObjectAnimatorInfo[] arrTargetAnimatorInfo;
	
	protected Object target = null;

	static{
		
		
	}

	public static BundleAnimator newInstance(){
		String postFix = "Old";

		try {
			BundleAnimator.class.getClassLoader().loadClass("android.animation.ObjectAnimator");
			
			for(String search: API11_TESTED_DEVICES){
				if(android.os.Build.MODEL.contains(search)){
					postFix = "New";
					Log.d("ObjectAnimator Type", "Native (API 11)");
					
					return new BundleAnimatorOver9();
				}
			}
			
			if(postFix.equals("Old")){
				postFix = "Old";
				Log.d("ObjectAnimator Type", "NineOldAndroids");
				Log.d("ObjectAnimator Type", "(Device support API 11 but it maybe has bug of Animation Speed)");
			}
		} catch (ClassNotFoundException e) {
			Log.d("ObjectAnimator Type", "NineOldAndroids");
			postFix = "Old";
		}
		
		return new BundleAnimatorUnder9();
	}
	
	public BundleAnimator(){
		instance = this;
	}
	
	public void addListener(BundleAnimatorListener animatorListener){
		listListener.add(animatorListener);
	}
	
	public abstract void setCurrentPlayTime(long targetMilliSec);
	
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
					setCurrentPlayTime(currentPlaytime);
					BundleAnimator.this.onAnimationEnd(BundleAnimator.this);
				} else {
					currentPlaytime += (FRAME_TERM * (float) playSpeed);
					setCurrentPlayTime(currentPlaytime);
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
					setCurrentPlayTime(currentPlaytime);
					BundleAnimator.this.onAnimationEndReverse(BundleAnimator.this);
					isStarted = false;
				} else {
					currentPlaytime -= FRAME_TERM * (float) playSpeed;
					setCurrentPlayTime(currentPlaytime);
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

	public abstract void setTarget(Object target);
	
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
