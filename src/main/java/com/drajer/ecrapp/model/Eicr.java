package com.drajer.ecrapp.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "eicr")
@DynamicUpdate
public class Eicr {

  public static final String MDN_RESPONSE_TYPE = "MDN";
  public static final String RR_RESPONSE_TYPE = "RR";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  // Request , Response Data
  @Column(name = "x_req_id", nullable = true, columnDefinition = "TEXT")
  private String xRequestId;

  @Column(name = "x_coorrelation_id", nullable = true, columnDefinition = "TEXT")
  private String xCorrelationId;

  // Eicr Data
  @Column(name = "eicr_doc_id", nullable = true, columnDefinition = "TEXT")
  private String eicrDocId;

  @Column(name = "set_id", nullable = true, columnDefinition = "TEXT")
  private String setId;

  @Column(name = "doc_version", nullable = true, columnDefinition = "INTEGER")
  private Integer docVersion;

  @Column(name = "eicr_data", nullable = true, columnDefinition = "TEXT")
  private String eicrData;

  @Column(name = "initiating_action", nullable = true, columnDefinition = "TEXT")
  private String initiatingAction;

  // Response Data.
  @Column(name = "response_type", nullable = true, columnDefinition = "TEXT")
  private String responseType;

  @Column(name = "response_x_request_id", nullable = true, columnDefinition = "TEXT")
  private String responseXRequestId;

  @Column(name = "response_doc_id", nullable = true, columnDefinition = "TEXT")
  private String responseDocId;

  @Column(name = "rr_data", nullable = true, columnDefinition = "TEXT")
  private String responseData;

  // EHR Details for persisting the data to EHR
  @Column(name = "fhir_server_url", nullable = true, columnDefinition = "TEXT")
  private String fhirServerUrl;

  @Column(name = "launch_patient_id", nullable = true, columnDefinition = "TEXT")
  private String launchPatientId;

  @Column(name = "encounter_id", nullable = true, columnDefinition = "TEXT")
  private String encounterId;

  // Timestamps
  @Column(name = "last_updated_ts", nullable = false)
  @CreationTimestamp
  private Date lastUpdated;

  public String getInitiatingAction() {
    return initiatingAction;
  }

  public void setInitiatingAction(String initiatingAction) {
    this.initiatingAction = initiatingAction;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getxRequestId() {
    return xRequestId;
  }

  public void setxRequestId(String xRequestId) {
    this.xRequestId = xRequestId;
  }

  public String getEicrData() {
    return eicrData;
  }

  public void setEicrData(String eicrData) {
    this.eicrData = eicrData;
  }

  public String getResponseType() {
    return responseType;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }

  public String getxCoorrelationId() {
    return xCorrelationId;
  }

  public void setxCoorrelationId(String xCoorrelationId) {
    this.xCorrelationId = xCoorrelationId;
  }

  public String getEicrDocId() {
    return eicrDocId;
  }

  public void setEicrDocId(String eicrDocId) {
    this.eicrDocId = eicrDocId;
  }

  public Integer getDocVersion() {
    return docVersion;
  }

  public void setDocVersion(Integer docVersion) {
    this.docVersion = docVersion;
  }

  public String getResponseXRequestId() {
    return responseXRequestId;
  }

  public void setResponseXRequestId(String responseXRequestId) {
    this.responseXRequestId = responseXRequestId;
  }

  public String getResponseDocId() {
    return responseDocId;
  }

  public void setResponseDocId(String responseDocId) {
    this.responseDocId = responseDocId;
  }

  public String getResponseData() {
    return responseData;
  }

  public void setResponseData(String responseData) {
    this.responseData = responseData;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getFhirServerUrl() {
    return fhirServerUrl;
  }

  public void setFhirServerUrl(String fhirServerUrl) {
    this.fhirServerUrl = fhirServerUrl;
  }

  public String getLaunchPatientId() {
    return launchPatientId;
  }

  public void setLaunchPatientId(String launchPatientId) {
    this.launchPatientId = launchPatientId;
  }

  public String getEncounterId() {
    return encounterId;
  }

  public void setEncounterId(String encounterId) {
    this.encounterId = encounterId;
  }

  public String getSetId() {
    return setId;
  }

  public void setSetId(String setId) {
    this.setId = setId;
  }
}
