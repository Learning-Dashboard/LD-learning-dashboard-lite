package com.upc.gessi.qrapids.app.config;

import com.upc.gessi.qrapids.QrapidsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QrapidsApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "security.enable=false",
        "security.api.enable=false",
        "security.jwt.secret=0123456789012345678901234567890123456789012345678901234567890123",
        "database.encryption.key=0123456789abcdef",
        "database.encryption.initvector=abcdef9876543210",
        "database.encryption.algorithm=AES/CBC/PKCS5Padding",
        "qma.ip=localhost",
        "qma.port=27017",
        "qma.database.name=test",
        "qma.username=test",
        "qma.password=test",
        "backlog.newIssue.url=http://localhost/issues",
        "backlog.milestones.url=http://localhost/milestones",
        "backlog.phases.url=http://localhost/phases",
        "pabre.url=http://localhost/pabre",
        "server.url=http://localhost:8080"
})
public class OpenApiEndpointIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    public void exposesOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    public void exposesSwaggerUiRedirect() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void exposesShortSwaggerUiRedirect() throws Exception {
        mockMvc.perform(get("/swagger-ui"))
                .andExpect(status().is3xxRedirection());
    }
}
