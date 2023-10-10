package org.kie.kogito.explainability.api;

import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CounterfactualDomainSerialisationTest {

    private ObjectMapper mapper;
    private StringWriter writer;

    @BeforeEach
    public void setup() {
        this.mapper = new ObjectMapper();
        this.writer = new StringWriter();
    }

    @Test
    public void testCounterfactualSearchDomain_Range_RoundTrip() throws Exception {
        CounterfactualDomainRange domainRange = new CounterfactualDomainRange(new IntNode(18), new IntNode(65));
        CounterfactualSearchDomainUnitValue searchDomain = new CounterfactualSearchDomainUnitValue("integer",
                "integer",
                Boolean.TRUE,
                domainRange);

        mapper.writeValue(writer, searchDomain);
        String searchDomainJson = writer.toString();
        assertNotNull(searchDomainJson);

        CounterfactualSearchDomainValue roundTrippedSearchDomain = mapper.readValue(searchDomainJson, CounterfactualSearchDomainValue.class);
        assertTrue(roundTrippedSearchDomain instanceof CounterfactualSearchDomainUnitValue);
        CounterfactualSearchDomainUnitValue roundTrippedSearchDomainUnit = (CounterfactualSearchDomainUnitValue) roundTrippedSearchDomain;

        assertEquals(searchDomain.getKind(), roundTrippedSearchDomainUnit.getKind());
        assertEquals(searchDomain.getType(), roundTrippedSearchDomainUnit.getType());
        assertEquals(searchDomain.isFixed(), roundTrippedSearchDomainUnit.isFixed());
        assertTrue(roundTrippedSearchDomainUnit.getDomain() instanceof CounterfactualDomainRange);

        CounterfactualDomainRange roundTrippedDomainRange = (CounterfactualDomainRange) roundTrippedSearchDomainUnit.getDomain();
        assertEquals(domainRange.getLowerBound(), roundTrippedDomainRange.getLowerBound());
        assertEquals(domainRange.getUpperBound(), roundTrippedDomainRange.getUpperBound());
    }

    @Test
    public void testCounterfactualSearchDomain_Categorical_RoundTrip() throws Exception {
        CounterfactualDomainCategorical domainCategorical = new CounterfactualDomainCategorical(List.of(new TextNode("A"), new TextNode("B")));
        CounterfactualSearchDomainUnitValue searchDomain = new CounterfactualSearchDomainUnitValue("integer",
                "integer",
                Boolean.TRUE,
                domainCategorical);

        mapper.writeValue(writer, searchDomain);
        String searchDomainJson = writer.toString();
        assertNotNull(searchDomainJson);

        CounterfactualSearchDomainValue roundTrippedSearchDomain = mapper.readValue(searchDomainJson, CounterfactualSearchDomainValue.class);
        assertTrue(roundTrippedSearchDomain instanceof CounterfactualSearchDomainUnitValue);
        CounterfactualSearchDomainUnitValue roundTrippedSearchDomainUnit = (CounterfactualSearchDomainUnitValue) roundTrippedSearchDomain;

        assertEquals(searchDomain.getKind(), roundTrippedSearchDomainUnit.getKind());
        assertEquals(searchDomain.getType(), roundTrippedSearchDomainUnit.getType());
        assertEquals(searchDomain.isFixed(), roundTrippedSearchDomainUnit.isFixed());
        assertTrue(roundTrippedSearchDomainUnit.getDomain() instanceof CounterfactualDomainCategorical);

        CounterfactualDomainCategorical roundTrippedDomainCategorical = (CounterfactualDomainCategorical) roundTrippedSearchDomainUnit.getDomain();
        assertEquals(domainCategorical.getCategories().size(), roundTrippedDomainCategorical.getCategories().size());
        assertTrue(roundTrippedDomainCategorical.getCategories().containsAll(domainCategorical.getCategories()));
    }

}
