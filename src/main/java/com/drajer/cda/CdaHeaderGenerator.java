package com.drajer.cda;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drajer.cda.utils.CdaGeneratorConstants;
import com.drajer.cda.utils.CdaGeneratorUtils;
import com.drajer.sof.model.Dstu2FhirData;
import com.drajer.sof.model.LaunchDetails;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierTypeCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ParticipantTypeEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class CdaHeaderGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(CdaHeaderGenerator.class);
	
	public static String createCdaHeader(Dstu2FhirData data, LaunchDetails details) {
		
		StringBuilder eICRHeader = new StringBuilder();

		if(data != null) {

			eICRHeader.append(CdaGeneratorUtils.getXmlHeaderForClinicalDocument());

			eICRHeader.append(CdaGeneratorUtils.getXmlForIIUsingGuid());

			eICRHeader.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.CODE_EL_NAME, CdaGeneratorConstants.PH_DOC_CODE, 
					CdaGeneratorConstants.LOINC_CODESYSTEM_OID, CdaGeneratorConstants.LOINC_CODESYSTEM_NAME, CdaGeneratorConstants.PH_DOC_DISPLAY_NAME));

			eICRHeader.append(CdaGeneratorUtils.getXmlForText(CdaGeneratorConstants.TITLE_EL_NAME, CdaGeneratorConstants.PH_REPORT_TITLE));

			eICRHeader.append(CdaGeneratorUtils.getXmlForEffectiveTime(CdaGeneratorConstants.EFF_TIME_EL_NAME, CdaGeneratorUtils.getCurrentDateTime()));

			eICRHeader.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.CONFIDENTIALITY_EL_NAME, CdaGeneratorConstants.CONFIDENTIALITY_CODE, CdaGeneratorConstants.CONFIDENTIALITY_CODE_SYTEM));

			eICRHeader.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.LANGUAGE_CODE_EL_NAME, CdaGeneratorConstants.LANGUAGE_CODE));

			eICRHeader.append(CdaGeneratorUtils.getXmlForIIWithElName(CdaGeneratorConstants.SET_ID_EL_NAME, details.getAssigningAuthorityId(), details.getSetId()));

			eICRHeader.append(CdaGeneratorUtils.getXmlForValue(CdaGeneratorConstants.VERSION_EL_NAME, details.getVersionNumber()));

			Bundle bundle = data.getData();
			if(bundle != null) {
				
				List<Entry> entries = bundle.getEntry();
				
				for(Entry ent : entries) {
					
					// Populate Patient 
					if(ent.getResource() instanceof Patient) {
						
						Patient p = (Patient)ent.getResource();
						eICRHeader.append(getPatientDetailsDstu2(data, p, details));
						
						break;
						
					}
				}
					
				// CdaFhirUtilities.populateEntriesForEncounter(bundle, details, en, pr, loc, org);
		
				eICRHeader.append(getAuthorXml(data.getPractitioner(), data.getEncounter()));
				
				eICRHeader.append(getCustodianXml(data.getOrganization(), details));
				
				eICRHeader.append(getEncompassingEncounter(data.getEncounter(), data.getPractitioner(), data.getLocation(), data.getOrganization(), details));
				
			}

		}

		return eICRHeader.toString();
	}
	
	public static String getPractitionerXml(Practitioner pr) {
		
		StringBuilder sb = new StringBuilder(500);
		
		if(pr != null) {
			
			IdentifierDt npi = CdaFhirUtilities.getIdentifierForSystem(pr.getIdentifier(), CdaGeneratorConstants.FHIR_NPI_URL);
			
			if(npi != null) {
				sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA, npi.getValue()));
			}
			else {
				sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA));
			}
				
			sb.append(CdaFhirUtilities.getAddressXml(pr.getAddress()));
			sb.append(CdaFhirUtilities.getTelecomXml(pr.getTelecom()));
			
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.ASSIGNED_PERSON_EL_NAME));
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.NAME_EL_NAME));
			
			List<HumanNameDt> hns = new ArrayList<HumanNameDt>();
			hns.add(pr.getName());
			sb.append(CdaFhirUtilities.getNameXml(hns));
			
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.NAME_EL_NAME));
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.ASSIGNED_PERSON_EL_NAME));
			
		}
		else {
			
			sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA));
			
			List<AddressDt> addrs = null; 
			sb.append(CdaFhirUtilities.getAddressXml(addrs));
			
			List<ContactPointDt> cps = null; 
			sb.append(CdaFhirUtilities.getTelecomXml(cps));
			
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.ASSIGNED_PERSON_EL_NAME));
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.NAME_EL_NAME));
			
			List<HumanNameDt> hns = null;
			sb.append(CdaFhirUtilities.getNameXml(hns));
			
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.NAME_EL_NAME));
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.ASSIGNED_PERSON_EL_NAME));
			
			
		}
		
		return sb.toString();
	}
	
	public static String getLocationXml(Location loc) {
		
		StringBuilder sb = new StringBuilder(500);
		
		if(loc != null) {
			
			IdentifierDt npi = CdaFhirUtilities.getIdentifierForSystem(loc.getIdentifier(), CdaGeneratorConstants.FHIR_NPI_URL);
			
			if(npi != null) {
				sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA, npi.getValue()));
			}
			else {
				sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA, loc.getId().getValue()));
			}
			
			if(loc.getType() != null &&
			   loc.getType().getCoding() != null) {
				
				sb.append(CdaFhirUtilities.getCodingXml(loc.getType().getCoding(), CdaGeneratorConstants.CODE_EL_NAME));
			}
			else {
				List<CodingDt> codes = null;
				sb.append(CdaFhirUtilities.getCodingXml(codes, CdaGeneratorConstants.CODE_EL_NAME));
			}
			
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.LOCATION_EL_NAME));	
			
			List<AddressDt> addrs = new ArrayList<AddressDt>();
			addrs.add(loc.getAddress());
			sb.append(CdaFhirUtilities.getAddressXml(addrs));		
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.LOCATION_EL_NAME));
			
			
		}
		else {
			
						// ***************
						// NOTE : THIS IS TEMPORARY --------
						// For Connectathon testing add defaults, this needs to be removed after connectathon and replaced with the commented out code.
						// ***************
			sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA, CdaGeneratorConstants.UNKNOWN_VALUE));
			sb.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.CODE_EL_NAME, "OF", "2.16.840.1.113883.5.111", "HL7RoleCode", "Outpatient Facility"));
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.LOCATION_EL_NAME));			
			
			List<AddressDt> addrs = new ArrayList<AddressDt>();
			AddressDt addr = new AddressDt();
			List<StringDt> addrLine = new ArrayList<StringDt>();
			addrLine.add(new StringDt("0987 Facility Drive"));
			addr.setLine(addrLine);
			addr.setCity("alt Lake City");
			addr.setState("UT");
			addr.setCountry("US");
			addr.setPostalCode("84101");
			addr.setUse(AddressUseEnum.WORK);
			addrs.add(addr);
			sb.append(CdaFhirUtilities.getAddressXml(addrs));
			
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.LOCATION_EL_NAME));
			
		/*	sb.append(CdaGeneratorUtils.getXmlForII(CdaGeneratorConstants.AUTHOR_NPI_AA, CdaGeneratorConstants.UNKNOWN_VALUE));
			sb.append(CdaGeneratorUtils.getXmlForNullCD(CdaGeneratorConstants.CODE_EL_NAME, CdaGeneratorConstants.NF_NI));
			
			sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.LOCATION_EL_NAME));				
			List<AddressDt> addrs = null;
			sb.append(CdaFhirUtilities.getAddressXml(addrs));		
			sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.LOCATION_EL_NAME)); */
			
		}
		
		return sb.toString();
	}
	
	public static String getAuthorXml(Practitioner pr, Encounter en) {
		
		StringBuilder sb = new StringBuilder(500);
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.AUTHOR_EL_NAME));
		
		if(en != null && 
		   en.getPeriod().getStart() != null) {
			sb.append(CdaGeneratorUtils.getXmlForEffectiveTime(CdaGeneratorConstants.TIME_EL_NAME, en.getPeriod().getStart()));
		}
		else {
			sb.append(CdaGeneratorUtils.getXmlForEffectiveTime(CdaGeneratorConstants.TIME_EL_NAME, CdaGeneratorUtils.getCurrentDateTime()));
		}
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.ASSIGNED_AUTHOR_EL_NAME));
		
		sb.append(getPractitionerXml(pr));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.ASSIGNED_AUTHOR_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.AUTHOR_EL_NAME));
		
		
		return sb.toString();
		
	}
	
	public static String getOrganizationXml(Organization org, LaunchDetails details) {
		
		StringBuilder sb = new StringBuilder(200);
		if(org != null) {
			
			IdentifierDt id = org.getIdentifierFirstRep();
			
			if(id != null &&
			   !id.isEmpty() ) {
				sb.append(CdaGeneratorUtils.getXmlForII(details.getAssigningAuthorityId(), id.getValue()));
			}
			else {
				sb.append(CdaGeneratorUtils.getXmlForII(details.getAssigningAuthorityId(), org.getId().getValue()));
			}
			
			sb.append(CdaGeneratorUtils.getXmlForText(CdaGeneratorConstants.NAME_EL_NAME, org.getName()));
			sb.append(CdaFhirUtilities.getTelecomXml(org.getTelecom()));
			sb.append(CdaFhirUtilities.getAddressXml(org.getAddress()));
 			
		}
		else {
			
			// ***************
			// NOTE : THIS IS TEMPORARY --------
			// For Connectathon testing add defaults, this needs to be removed after connectathon and replaced with the commented out code.
			// ***************
			sb.append(CdaGeneratorUtils.getXmlForII(details.getAssigningAuthorityId(), "UtahOutpatientClinic"));
			sb.append(CdaGeneratorUtils.getXmlForText(CdaGeneratorConstants.NAME_EL_NAME, "Utah Outpatient Clinic"));
			
			List<ContactPointDt> cps = new ArrayList<ContactPointDt>();
			ContactPointDt cp = new ContactPointDt();
			cp.setSystem(ContactPointSystemEnum.PHONE);
			cp.setUse(ContactPointUseEnum.HOME);
			cp.setValue("5557770123");
			cps.add(cp);
			sb.append(CdaFhirUtilities.getTelecomXml(cps));
			
			List<AddressDt> addrs = new ArrayList<AddressDt>();
			AddressDt addr = new AddressDt();
			List<StringDt> addrLine = new ArrayList<StringDt>();
			addrLine.add(new StringDt("0987 Facility Drive"));
			addr.setLine(addrLine);
			addr.setCity("alt Lake City");
			addr.setState("UT");
			addr.setCountry("US");
			addr.setPostalCode("84101");
			addr.setUse(AddressUseEnum.WORK);
			addrs.add(addr);
			sb.append(CdaFhirUtilities.getAddressXml(addrs));
			
			/* sb.append(CdaGeneratorUtils.getNFXMLForII(CdaGeneratorConstants.NF_NI));
			sb.append(CdaGeneratorUtils.getXmlForText(CdaGeneratorConstants.NAME_EL_NAME, CdaGeneratorConstants.UNKNOWN_VALUE));
					
			List<ContactPointDt> cps = null; 
			sb.append(CdaFhirUtilities.getTelecomXml(cps));
			
			List<AddressDt> addrs = null; 
			sb.append(CdaFhirUtilities.getAddressXml(addrs)); */
			
			
		}
		
		return sb.toString();
	}
	
	public static String getCustodianXml(Organization org, LaunchDetails details) {
		
		StringBuilder sb = new StringBuilder(500);
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.CUSTODIAN_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.ASSGND_CUST_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.REP_CUST_ORG_EL_NAME));
		
		sb.append(getOrganizationXml(org,details));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.REP_CUST_ORG_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.ASSGND_CUST_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.CUSTODIAN_EL_NAME));
		
		return sb.toString();
	}
	
	
	public static String getEncompassingEncounter(Encounter en, Practitioner pr, Location loc, Organization org, LaunchDetails details) {
		
		StringBuilder sb = new StringBuilder(2000);
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.COMPONENT_OF_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.ENCOMPASSING_ENC_EL_NAME));
		
		if(en != null) {
			sb.append(CdaGeneratorUtils.getXmlForII(details.getAssigningAuthorityId(), en.getId().getIdPart()));
			sb.append(CdaFhirUtilities.getCodeableConceptXml(en.getType(), CdaGeneratorConstants.CODE_EL_NAME, false));			
			sb.append(CdaFhirUtilities.getPeriodXml(en.getPeriod(), CdaGeneratorConstants.EFF_TIME_EL_NAME));
		}
		else {
			sb.append(CdaGeneratorUtils.getXmlForIIUsingGuid());
			sb.append(CdaGeneratorUtils.getXmlForNullCD(CdaGeneratorConstants.CODE_EL_NAME, CdaGeneratorConstants.NF_NI));
			sb.append(CdaGeneratorUtils.getXmlForNullEffectiveTime(CdaGeneratorConstants.EFF_TIME_EL_NAME, CdaGeneratorConstants.NF_NI));
		}
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.RESP_PARTY_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.ASSIGNED_ENTITY_EL_NAME));
		
		sb.append(getPractitionerXml(pr));
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.REP_ORG_EL_NAME));
		
		sb.append(getOrganizationXml(org,details));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.REP_ORG_EL_NAME));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.ASSIGNED_ENTITY_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.RESP_PARTY_EL_NAME));
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.LOCATION_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.HEALTHCARE_FACILITY_EL_NAME));
		
		sb.append(getLocationXml(loc));
		
		sb.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.SERVICE_PROVIDER_ORG_EL_NAME));
		
		sb.append(getOrganizationXml(org,details));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.SERVICE_PROVIDER_ORG_EL_NAME));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.HEALTHCARE_FACILITY_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.LOCATION_EL_NAME));
		
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.ENCOMPASSING_ENC_EL_NAME));
		sb.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.COMPONENT_OF_EL_NAME));
		
		return sb.toString();
	}
	
	
	
	public static String getPatientDetailsDstu2(Dstu2FhirData data, Patient p, LaunchDetails details) {

		StringBuilder patientDetails = new StringBuilder();

		patientDetails.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.RECORD_TARGET_EL_NAME));
		patientDetails.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.PATIENT_ROLE_EL_NAME));
		
		IdentifierDt id = CdaFhirUtilities.getIdentifierForType(p.getIdentifier(), IdentifierTypeCodesEnum.MR);
		
		if(id != null) {
			
			if(!StringUtils.isEmpty(id.getSystem()) && 
				!StringUtils.isEmpty(id.getValue())	) {
			
				logger.info(" Found Identifier with MR ");;
				patientDetails.append(CdaGeneratorUtils.getXmlForII(id.getSystem(), id.getValue()));
			}
			else {
				
				logger.info(" Using Resource Identifier as id ");;
				patientDetails.append(CdaGeneratorUtils.getXmlForII(details.getAssigningAuthorityId(), p.getId().toString()));
			}
			
		}
		else {			
			logger.info(" Using Resource Identifier as id ");;
			patientDetails.append(CdaGeneratorUtils.getXmlForII(details.getAssigningAuthorityId(), p.getId().toString()));
		}
		
		// Add Address.
		patientDetails.append(CdaFhirUtilities.getAddressXml(p.getAddress()));
		
		// Add Telecom
		patientDetails.append(CdaFhirUtilities.getTelecomXml(p.getTelecom()));
		
		// Add patient
		patientDetails.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.PATIENT_EL_NAME));
		
		patientDetails.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.NAME_EL_NAME));	
		patientDetails.append(CdaFhirUtilities.getNameXml(p.getName()));				
		patientDetails.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.NAME_EL_NAME));
		
		patientDetails.append(CdaFhirUtilities.getGenderXml(p.getGenderElement()));
		patientDetails.append(CdaGeneratorUtils.getXmlForEffectiveTime(CdaGeneratorConstants.BIRTH_TIME_EL_NAME, p.getBirthDate()));
		
		if(p.getDeceased() == null || 
		   (p.getDeceased() != null && 
		   p.getDeceased().isEmpty()) ) {
			patientDetails.append(CdaGeneratorUtils.getXmlForValue(CdaGeneratorConstants.SDTC_DECEASED_IND, CdaGeneratorConstants.CCDA_FALSE));
		}
		else {
			patientDetails.append(CdaGeneratorUtils.getXmlForValue(CdaGeneratorConstants.SDTC_DECEASED_IND, CdaGeneratorConstants.CCDA_TRUE));
			
			if(p.getDeceased() instanceof DateTimeDt) {
				DateTimeDt d = (DateTimeDt)p.getDeceased();
				patientDetails.append(CdaGeneratorUtils.getXmlForEffectiveTime(CdaGeneratorConstants.SDTC_DECEASED_TIME, d.getValue().toString()));
			}
			else {
				patientDetails.append(CdaGeneratorUtils.getXmlForNullEffectiveTime(CdaGeneratorConstants.SDTC_DECEASED_TIME, CdaGeneratorConstants.NF_NI));
			}
		}
		
		CodingDt race = CdaFhirUtilities.getCodingExtension(p.getUndeclaredExtensions(), 
				CdaGeneratorConstants.FHIR_ARGO_RACE_EXT_URL, CdaGeneratorConstants.OMB_RACE_CATEGORY_URL);
		
		if(race != null &&
		   race.getCode() != null) {			
			patientDetails.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.RACE_CODE_EL_NAME, race.getCode(), 
					CdaGeneratorConstants.RACE_CODE_SYSTEM, CdaGeneratorConstants.RACE_CODE_SYSTEM_NAME, race.getDisplay()));
		}
		else {
			patientDetails.append(CdaGeneratorUtils.getXmlForNullCD(CdaGeneratorConstants.RACE_CODE_EL_NAME, CdaGeneratorConstants.NF_NI));
		}
		
		CodingDt ethnicity = CdaFhirUtilities.getCodingExtension(p.getUndeclaredExtensions(), 
				CdaGeneratorConstants.FHIR_ARGO_ETHNICITY_EXT_URL, CdaGeneratorConstants.OMB_RACE_CATEGORY_URL);
		
		if(ethnicity != null && 
		   ethnicity.getCode() != null) {			
			patientDetails.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.ETHNIC_CODE_EL_NAME, race.getCode(), 
					CdaGeneratorConstants.RACE_CODE_SYSTEM, CdaGeneratorConstants.RACE_CODE_SYSTEM_NAME, race.getDisplay()));
		}
		else {
			patientDetails.append(CdaGeneratorUtils.getXmlForNullCD(CdaGeneratorConstants.ETHNIC_CODE_EL_NAME, CdaGeneratorConstants.NF_NI));
		}
		
		patientDetails.append(CdaGeneratorUtils.getXmlForStartElement(CdaGeneratorConstants.LANGUAGE_COMM_EL_NAME));		
		CodingDt language = CdaFhirUtilities.getLanguage(p.getCommunication());
		
		if(language != null && 
		   language.getCode() != null) {			
			patientDetails.append(CdaGeneratorUtils.getXmlForCD(CdaGeneratorConstants.LANGUAGE_CODE_EL_NAME, language.getCode()));
		}
		else {
			patientDetails.append(CdaGeneratorUtils.getXmlForNullCD(CdaGeneratorConstants.LANGUAGE_CODE_EL_NAME, CdaGeneratorConstants.NF_NI));
		}		
		patientDetails.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.LANGUAGE_COMM_EL_NAME));
				
		patientDetails.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.PATIENT_EL_NAME));		
		patientDetails.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.PATIENT_ROLE_EL_NAME));
		patientDetails.append(CdaGeneratorUtils.getXmlForEndElement(CdaGeneratorConstants.RECORD_TARGET_EL_NAME));
		
		return patientDetails.toString();

	}
		
}
