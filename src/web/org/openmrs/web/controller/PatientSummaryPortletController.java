package org.openmrs.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.summary.web.PatientSummarySpecification;

public class PatientSummaryPortletController extends PortletController {

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		if ( !model.containsKey("patientSummarySpecification")) {
			model.put("patientSummarySpecification", PatientSummarySpecification.getInstance());			
		}
	}
	
}
