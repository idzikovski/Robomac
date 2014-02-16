package ivan.dzikovski.psevi.robomac;

public class Time {
	private int hours;
	private int minutes;
	private int seconds;
	private int totalSeconds;
	
	public Time(long seconds){
		this.totalSeconds=(int) seconds;
		int ostatok;
		this.hours=(int) (seconds/3600);
		ostatok=(int) (seconds%3600);
		this.minutes=ostatok/60;
		this.seconds=ostatok%60;

	}
	
	public int getTotalSeconds() {
		return totalSeconds;
	}

	public void setTotalSeconds(int totalSeconds) {
		this.totalSeconds = totalSeconds;
	}

	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
}
