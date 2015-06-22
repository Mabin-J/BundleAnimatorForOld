package info.mabin.android.bundleanimator;

public interface BundleAnimatorListener{
	public void onAnimationStart(BundleAnimator animation);
	public void onAnimationEnd(BundleAnimator animation);
	public void onAnimationCancel(BundleAnimator animation);
	public void onAnimationRepeat(BundleAnimator animation);
	public void onAnimationPlaying(BundleAnimator animation, long currentTime);
	public void onAnimationEndReverse(BundleAnimator animator);
}
