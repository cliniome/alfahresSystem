package com.degla.db.models;

public enum RoleTypes {
	RECEPTIONIST, KEEPER, COORDINATOR,ADMIN , PROCESSING_COORDINATOR,CODING_COORDINATOR,ANALYSIS_COORDINATOR,INCOMPLETE_COORDINATOR;

	private static RoleTypes[] INPATIENT_ROLES = {
			PROCESSING_COORDINATOR,CODING_COORDINATOR,ANALYSIS_COORDINATOR,INCOMPLETE_COORDINATOR
	};


	public boolean isInPatientActor(){

		boolean result = false;

		for(RoleTypes type : INPATIENT_ROLES)
		{
			if(type == this)
			{
				result = true;
				break;
			}
		}

		return result;

	}
}