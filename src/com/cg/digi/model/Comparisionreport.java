package com.cg.digi.model;

public class Comparisionreport {
	
private String comparisionreportid;
private String basedocument;
private String basepath;
private String actualdocument;
private String actualpath;
private String reportpath;
private String status;
private String createdby;
private String creationtime;
private String modifiedby;
private String modifiedtime;


public Comparisionreport() {
	super();
	// TODO Auto-generated constructor stub
}


public Comparisionreport(String comparisionreportid, String basedocument, String basepath, String actualdocument,
		String actualpath, String reportpath, String status, String createdby, String creationtime, String modifiedby,
		String modifiedtime) {
	super();
	this.comparisionreportid = comparisionreportid;
	this.basedocument = basedocument;
	this.basepath = basepath;
	this.actualdocument = actualdocument;
	this.actualpath = actualpath;
	this.reportpath = reportpath;
	this.status = status;
	this.createdby = createdby;
	this.creationtime = creationtime;
	this.modifiedby = modifiedby;
	this.modifiedtime = modifiedtime;
}


public String getComparisionreportid() {
	return comparisionreportid;
}


public void setComparisionreportid(String comparisionreportid) {
	this.comparisionreportid = comparisionreportid;
}


public String getBasedocument() {
	return basedocument;
}


public void setBasedocument(String basedocument) {
	this.basedocument = basedocument;
}


public String getBasepath() {
	return basepath;
}


public void setBasepath(String basepath) {
	this.basepath = basepath;
}


public String getActualdocument() {
	return actualdocument;
}


public void setActualdocument(String actualdocument) {
	this.actualdocument = actualdocument;
}


public String getActualpath() {
	return actualpath;
}


public void setActualpath(String actualpath) {
	this.actualpath = actualpath;
}


public String getReportpath() {
	return reportpath;
}


public void setReportpath(String reportpath) {
	this.reportpath = reportpath;
}


public String getStatus() {
	return status;
}


public void setStatus(String status) {
	this.status = status;
}


public String getCreatedby() {
	return createdby;
}


public void setCreatedby(String createdby) {
	this.createdby = createdby;
}


public String getCreationtime() {
	return creationtime;
}


public void setCreationtime(String creationtime) {
	this.creationtime = creationtime;
}


public String getModifiedby() {
	return modifiedby;
}


public void setModifiedby(String modifiedby) {
	this.modifiedby = modifiedby;
}


public String getModifiedtime() {
	return modifiedtime;
}


public void setModifiedtime(String modifiedtime) {
	this.modifiedtime = modifiedtime;
}


@Override
public String toString() {
	return "Comparisionreport [comparisionreportid=" + comparisionreportid + ", basedocument=" + basedocument
			+ ", basepath=" + basepath + ", actualdocument=" + actualdocument + ", actualpath=" + actualpath
			+ ", reportpath=" + reportpath + ", status=" + status + ", createdby=" + createdby + ", creationtime="
			+ creationtime + ", modifiedby=" + modifiedby + ", modifiedtime=" + modifiedtime + "]";
}




}
