package ivan.dzikovski.psevi.robomac;

import android.app.Application;

public class Robomac extends Application {
	private int nivo;
	private Time times[];
	public Robomac(){
		nivo=1;
		times=new Time[5];
	}

	public int getNivo() {
		return nivo;
	}
	public void setNivo(int nivo) {
		this.nivo = nivo;
	}

	public Time getTimes(int index) {
		return times[index];
	}

	public void setTimes(int index, Time value) {
		this.times[index]=value;
	}
}
