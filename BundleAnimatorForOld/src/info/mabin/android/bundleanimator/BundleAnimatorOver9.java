package info.mabin.android.bundleanimator;

import android.annotation.TargetApi;
import android.os.Build;

public class BundleAnimatorOver9 extends BundleAnimator{

	private android.animation.ObjectAnimator[] arrTargetAnimatorNew;

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setCurrentPlayTime(long targetMilliSec){
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
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setTarget(Object target){
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
}
