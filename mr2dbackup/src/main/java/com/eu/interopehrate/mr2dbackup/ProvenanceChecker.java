package com.eu.interopehrate.mr2dbackup;

import org.hl7.fhir.r4.model.Bundle;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import eu.interopehrate.mr2da.provenance.ProvenanceValidationResults;
import eu.interopehrate.mr2da.provenance.ProvenanceValidator;

public class ProvenanceChecker {
    IParser parser = FhirContext.forR4().newJsonParser();

    private Bundle stringToBundle (String resource){
        return (Bundle)parser.parseResource(resource);
    }

    public boolean checkProvenance (String resource) throws Exception {
        Bundle bundleResource = stringToBundle(resource);
        ProvenanceValidator validator = new ProvenanceValidator();
        ProvenanceValidationResults valRes = validator.validateBundle(bundleResource);
        return valRes.isSuccessful();
    }
}
