package eadl.productgestion.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiExportTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Exporter les spécifications OpenAPI dans docs/openapi.json")
    void exportOpenApiJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).isNotEmpty();

        Path docsDir = Paths.get("docs");
        if (!Files.exists(docsDir)) {
            Files.createDirectories(docsDir);
        }

        Files.writeString(docsDir.resolve("openapi.json"), content);
        assertThat(Files.exists(docsDir.resolve("openapi.json"))).isTrue();
    }
}
