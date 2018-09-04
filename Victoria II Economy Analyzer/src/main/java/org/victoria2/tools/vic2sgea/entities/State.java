package org.victoria2.tools.vic2sgea.entities;

public class State extends EconomySubject implements Comparable<State> {

	
	protected long population;
	protected long employmentFactory;
    protected long employmentRGO;
    public long workforceFactory;
    public long workforceRGO;
	
	
	
	@Override
	public int compareTo(State arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void innerCalculations() {
		// TODO Auto-generated method stub
		
	}
	
	

}
