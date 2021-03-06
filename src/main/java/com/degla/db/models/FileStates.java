package com.degla.db.models;

public enum FileStates {

	CHECKED_IN(14),
	CHECKED_OUT(8)
	, MISSING(-1)
	, RECEPTIONIST_IN(12)
	, RECEPTIONIST_OUT(9)
	, KEEPER_IN(13)
	, COORDINATOR_IN(10)
	, COORDINATOR_OUT(12)
	, DISTRIBUTED(11),
	NEW(-2);

	private int step;

	private FileStates(int step)
	{
		this.setStep(step);
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
}