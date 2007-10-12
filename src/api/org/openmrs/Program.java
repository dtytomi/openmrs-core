package org.openmrs;

import java.util.Date;
import java.util.Set;

import org.openmrs.synchronization.Synchronizable;

public class Program implements java.io.Serializable, Synchronizable {
	
	private static final long serialVersionUID = 0L;

	private Integer programId;
	private Concept concept;
	private User creator; 
	private Date dateCreated; 
	private User changedBy;
	private Date dateChanged;
	private Boolean voided = false; 
	private User voidedBy;
	private Date dateVoided; 
	private String voidReason;
	private Set<ProgramWorkflow> workflows;
		private String guid;
	
  public String getGuid() {
      return guid;
  }

  public void setGuid(String guid) {
      this.guid = guid;
  }

	public Program() { }
	
	/**
	 * Convenience method to get a workflow by name.
	 * @param name the workflow's name, in any locale
	 * @return a workflow which has that name in any locale
	 */
	public ProgramWorkflow getWorkflowByName(String name) {
		for (ProgramWorkflow pw : workflows)
			if (pw.getConcept().isNamed(name))
				return pw;
		return null;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}

	public Date getDateVoided() {
		return dateVoided;
	}

	public void setDateVoided(Date dateVoided) {
		this.dateVoided = dateVoided;
	}

	public Boolean getVoided() {
		return isVoided();
	}
	
	public Boolean isVoided() {
		return voided;
	}

	public void setVoided(Boolean voided) {
		this.voided = voided;
	}

	public User getVoidedBy() {
		return voidedBy;
	}

	public void setVoidedBy(User voidedBy) {
		this.voidedBy = voidedBy;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	public Integer getProgramId() {
		return programId;
	}

	public void setProgramId(Integer programId) {
		this.programId = programId;
	}

	public String toString() {
		return "Program(id=" + programId + ", concept=" + concept + ", workflows=" + workflows + ")";
	}

	public Set<ProgramWorkflow> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(Set<ProgramWorkflow> workflows) {
		this.workflows = workflows;
	}
	
}
