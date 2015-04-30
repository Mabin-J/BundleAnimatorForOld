package info.mabin.android.bundleanimator;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import android.animation.ObjectAnimator;

import info.mabin.android.bundleanimator.ObjectAnimatorInfo;

public class BundleAnimator implements BundleAnimatorListener{
	private static final int FRAME_TERM = 10;
	
	private Handler handler = new Handler();
	
	private boolean isStarted = false;
	private boolean isCanceled = false;
	
	private float playSpeed = 1;
	
	private List<BundleAnimatorListener> listListener = new ArrayList<BundleAnimatorListener>();
	
	private long duration = 0;
	
	private long currentPlaytime = 0;
	
	private ObjectAnimatorInfo[] arrTargetAnimatorInfo;
	private ObjectAnimator[] arrTargetAnimator;
	
	private Object target = null;

	public void addListener(BundleAnimatorListener animatorListener){
		listListener.add(animatorListener);
	}
	
	public void setCurrentPlayTime(long targetMilliSec){
		this.setRealCurrentPlayTime((long) (targetMilliSec * (float) playSpeed));
	}
	
	private void setRealCurrentPlayTime(long targetMilliSec){
		if(isStarted == false){
			this.onAnimationStart(this);
			
			isStarted = true;
		}
		
		this.currentPlaytime = targetMilliSec;
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			long startDelay = arrTargetAnimatorInfo[i].getStartDelay();
			if(startDelay > targetMilliSec){
				arrTargetAnimator[i].setCurrentPlayTime(0);
			} else {
				long tmpTargetMilliSec = targetMilliSec;
				tmpTargetMilliSec = targetMilliSec - startDelay;
				arrTargetAnimator[i].setCurrentPlayTime(tmpTargetMilliSec);
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
		playSpeed = 1;
		isStarted = false;
		this.target = target;

		arrTargetAnimator = new ObjectAnimator[arrTargetAnimatorInfo.length];
		
		for(int i = 0; i < arrTargetAnimatorInfo.length; i++){
			Object currentTargetArgs;
			currentTargetArgs = arrTargetAnimatorInfo[i].getArgs();
			
			if(currentTargetArgs.getClass().getCanonicalName().equals("float[]")){
				arrTargetAnimator[i] = ObjectAnimator.ofFloat(
						target, 
						arrTargetAnimatorInfo[i].getProperty(), 
						(float[])currentTargetArgs);
			} else {
				arrTargetAnimator[i] = ObjectAnimator.ofInt(
						target, 
						arrTargetAnimatorInfo[i].getProperty(), 
						(int[])currentTargetArgs);				
			}
			
			arrTargetAnimator[i].setDuration(arrTargetAnimatorInfo[i].getDuration());
			arrTargetAnimator[i].setStartDelay(arrTargetAnimatorInfo[i].getStartDelay());
			arrTargetAnimator[i].setInterpolator(arrTargetAnimatorInfo[i].getInterpolator());
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
}
