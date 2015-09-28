package com.degla.db.models;

import com.degla.utils.FileStateUtils;

public enum FileStates {

	CHECKED_IN(14),
	CHECKED_OUT(8)
	, MISSING(-1)
	, RECEPTIONIST_IN(12)
	, RECEPTIONIST_OUT(9)
	, KEEPER_IN(13)
	, COORDINATOR_IN(10)
	, COORDINATOR_OUT(12)
	,TRANSFERRED(15)
	,OUT_OF_CABIN(7)
	, DISTRIBUTED(11),
	NEW(-2),
	PROCESSING_COORDINATOR(16),
	ANALYSIS_COORDINATOR(17),
	CODING_COORDINATOR(18),
	INCOMPLETE_COORDINATOR(19);

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


	public String getReadableState()
	{
		FileStateUtils utils = new FileStateUtils(this);

		return utils.getReadableState();
	}


}