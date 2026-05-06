package com.upc.gessi.qrapids.app.presentation.rest.services;

import com.upc.gessi.qrapids.app.domain.controllers.ProductsController;
import com.upc.gessi.qrapids.app.domain.models.DataSource;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOProduct;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOProject;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOProjectIdentity;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOStrategicIndicatorEvaluation;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import com.upc.gessi.qrapids.app.testHelpers.HelperFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductsTest {

    private MockMvc mockMvc;
    private DomainObjectsBuilder builder;

    @Mock
    private ProductsController productsController;

    @InjectMocks
    private Products productController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getProductsReturnsProductsWithProjects() throws Exception {
        DTOProduct product = buildProduct();
        when(productsController.getProducts()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(product.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(product.getName())))
                .andExpect(jsonPath("$[0].logo", is(nullValue())))
                .andExpect(jsonPath("$[0].projects[0].externalId",
                        is(product.getProjects().get(0).getExternalId())));

        verify(productsController).getProducts();
    }

    @Test
    public void getProductByIdReturnsSingleProduct() throws Exception {
        DTOProduct product = buildProduct();
        when(productsController.getProductById("1")).thenReturn(product);

        mockMvc.perform(get("/api/products/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productsController).getProductById("1");
    }

    @Test
    public void getProductEvaluationReturnsCurrentStrategicIndicators() throws Exception {
        DTOStrategicIndicatorEvaluation evaluation = builder.buildDTOStrategicIndicatorEvaluation();
        when(productsController.getProductEvaluation(1L)).thenReturn(Arrays.asList(evaluation));

        mockMvc.perform(get("/api/products/{id}/current", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(evaluation.getId())))
                .andExpect(jsonPath("$[0].value.first",
                        is(HelperFunctions.getFloatAsDouble(evaluation.getValue().getFirst()))));

        verify(productsController).getProductEvaluation(1L);
    }

    @Test
    public void getDetailedCurrentEvaluationReturnsProjectEvaluationPairs() throws Exception {
        DTOStrategicIndicatorEvaluation evaluation = builder.buildDTOStrategicIndicatorEvaluation();
        List<Pair<String, List<DTOStrategicIndicatorEvaluation>>> result =
                Arrays.asList(Pair.of("test", Arrays.asList(evaluation)));
        when(productsController.getDetailedProductEvaluation(1L)).thenReturn(result);

        mockMvc.perform(get("/api/products/{id}/projects/current", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].first", is("test")))
                .andExpect(jsonPath("$[0].second[0].id", is(evaluation.getId())));

        verify(productsController).getDetailedProductEvaluation(1L);
    }

    private DTOProduct buildProduct() {
        Map<DataSource, DTOProjectIdentity> identities = new HashMap<>();
        identities.put(DataSource.GITHUB, new DTOProjectIdentity(DataSource.GITHUB, "github-url"));
        DTOProject project = new DTOProject(1L, "test", "Test", "Test project", null,
                true, "backlog", false, identities, false, null);
        return new DTOProduct(1L, "Product", "Product description", null, Arrays.asList(project));
    }
}
