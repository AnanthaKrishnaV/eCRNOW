package com.drajer.cda.parser;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdaRrModel {

  private final Logger logger = LoggerFactory.getLogger(CdaRrModel.class);

  private String eicrDocId;

  private CdaIi rrDocId;

  private String reportableType;

  private String errors;

  public CdaRrModel() {}

  public String getEicrDocId() {
    return eicrDocId;
  }

  public void setEicrDocId(String eicrDocId) {
    this.eicrDocId = eicrDocId;
  }

  public String getReportableType() {
    return reportableType;
  }

  public void setReportableType(String reportableType) {
    this.reportableType = reportableType;
  }

  public String getErrors() {
    return errors;
  }

  public void setErrors(String errors) {
    this.errors = errors;
  }

  public CdaIi getRrDocId() {
    return rrDocId;
  }

  public void setRrDocId(CdaIi rrDocId) {
    this.rrDocId = rrDocId;
  }

  public void setRrDocId(ArrayList<CdaIi> rrdocids) {
    if (rrdocids != null && !rrdocids.isEmpty()) {
      this.rrDocId = rrdocids.get(0);
    }
  }
}
