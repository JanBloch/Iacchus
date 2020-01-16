package org.iacchus.audio;

public class Gate{
	public double attack;
	public double release;
	public double min;
	public Gate(double attack, double release, double min){
		this.attack = attack;
		this.release = release;
		this.min = min;
	}


	public int[] getValues(int[] values, double rate){
		double[] levels = new double[values.length];
		levels[0] = values[0];
		for(int i=1; i<values.length; i++){
			if(values[i]>values[i-1]){
				levels[i] = levels[i-1]+(values[i]-levels[i-1])*(1000/rate)/attack;
			}else{
				levels[i] = levels[i-1]-(values[i]-levels[i-1])*(1000/rate)/release;
			}
		}
		for(int i=0; i<values.length; i++){
			if(levels[i]<min){
				values[i] = 0;
			}
		}
		return values;
	}
}