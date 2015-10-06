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
	INCOMPLETE_COORDINATOR(19),
	INPATIENT_COMPLETED(20),
	TEMPORARY_STORED(21);

	private int step;


	private static FileStates[] inpatientStates = {PROCESSING_COORDINATOR,ANALYSIS_COORDINATOR,CODING_COORDINATOR,INCOMPLETE_COORDINATOR,TEMPORARY_STORED};

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


	public boolean isCurrentlyInPatient()
	{
		boolean result = false;

		for(FileStates step : inpatientStates)
		{
			if(step == this)
			{
				result = true;
				break;
			}
		}

		return result;
	}


	public String getReadableState()
	{
		FileStateUtils utils = new FileStateUtils(this);

		return utils.getReadableState();
	}


}