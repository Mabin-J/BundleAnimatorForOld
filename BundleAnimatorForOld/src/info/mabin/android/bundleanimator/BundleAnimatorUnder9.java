package info.mabin.android.bundleanimator;

public class BundleAnimatorUnder9 extends BundleAnimator{
	private com.nineoldandroids.animation.ObjectAnimator[] arrTargetAnimatorOld;

	@Override
	public void setCurrentPlayTime(long targetMilliSec){
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
	
	@Override
	public void setTarget(Object target){
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
}
