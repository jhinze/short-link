package dev.hinze.shortlink.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = HealthController.class)
@ContextConfiguration(
        classes = {
                HealthController.class
        })
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthShouldReturnOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
