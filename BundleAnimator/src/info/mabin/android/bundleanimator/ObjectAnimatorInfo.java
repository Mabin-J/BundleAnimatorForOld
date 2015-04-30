package info.mabin.android.bundleanimator;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class ObjectAnimatorInfo {
	private String property = "";
	private Object args = null;
	private long duration = 300;
	private long startDelay = 0;
	private Interpolator interpolator = new LinearInterpolator();
	
	public ObjectAnimatorInfo(String property){
		this.property = property;
	}
	
	public static ObjectAnimatorInfo ofFloat(String property, float... args){
		ObjectAnimatorInfo result = new ObjectAnimatorInfo(property);
		result.setArgs(args);
		return result;
	}
	
	public static ObjectAnimatorInfo ofInt(String property, int... args){
		ObjectAnimatorInfo result = new ObjectAnimatorInfo(property);
		result.setArgs(args);
		return result;
	}
	
	public void setArgs(Object args){
		this.args = args;
	}
	
	public ObjectAnimatorInfo setInterpolator(Interpolator interpolator){
		this.interpolator = interpolator;
		return this;
	}
	
	public Interpolator getInterpolator(){
		return this.interpolator;
	}
	
	/**
     * Sets the length of the animation. The default duration is 300 milliseconds.
     *
     * @param duration The length of the animation, in milliseconds.
     * @return ObjectAnimator The object called with setDuration(). This return
     * value makes it easier to compose statements together that construct and then set the
     * duration, as in
     * <code>ObjectAnimator.ofInt(target, propertyName, 0, 10).setDuration(500).setStartDelay(400)</code>.
     */
	public ObjectAnimatorInfo setDuration(long duration){
		this.duration = duration;
		return this;
	}
	
	/**
     * The amount of time, in milliseconds, to delay starting the animation after
     * {@link #start()} is called.

     * @param startDelay The amount of the delay, in milliseconds
     * @return ObjectAnimator The object called with setStartDelay(). This return
     * value makes it easier to compose statements together that construct and then set the
     * duration, as in
     * <code>ObjectAnimator.ofInt(target, propertyName, 0, 10).setStartDelay(500).setDuration(400)</code>.
     */
    public ObjectAnimatorInfo setStartDelay(long startDelay) {
        this.startDelay = startDelay;
        return this;
    }
    
    /**
     * The amount of time, in milliseconds, to delay starting the animation after
     * {@link #start()} is called.
     *
     * @return the number of milliseconds to delay running the animation
     */
    public long getStartDelay() {
        return startDelay;
    }
    
    /**
     * The amount of play time, in milliseconds, after 
     * {@link #start()} is called.
     *
     * @return the number of milliseconds to run the animation
     */
    public long getDuration(){
    	return duration;
    }
    
    public String getProperty(){
    	return property;
    }
    
    public Object getArgs(){
    	return args;
    }
}
