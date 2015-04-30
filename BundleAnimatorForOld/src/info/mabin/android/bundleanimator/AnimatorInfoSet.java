package info.mabin.android.bundleanimator;

public class AnimatorInfoSet {
	private ObjectAnimatorInfo[] infos;
	private boolean statusPlayTogether;
	
	public void playTogether(ObjectAnimatorInfo...infos){
		statusPlayTogether = true;
		this.infos = infos;
	}
	
	public void playSequentially(ObjectAnimatorInfo...infos){
		statusPlayTogether = false;
		this.infos = infos;
	}

	public boolean isPlayTogether(){
		return statusPlayTogether;
	}
	
	public ObjectAnimatorInfo[] getInfos(){
		return infos;
	}
}
