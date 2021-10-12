package com.example.ProfileService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static
        org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static
        org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static
        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProfilesShouldReturnEmptyArray() throws Exception {
        this.mockMvc.perform(get("/PS/profiles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void repeatedEMailRejectedNewProfile() throws Exception {
        String email = "test@example.com";
        Profile profile1 = new Profile(
                1, "hallo", email, "foo");
        Profile profile2 = new Profile(
                2, "welt", email, "bar");

        ObjectMapper objectMapper = new ObjectMapper();
        String user_json1 = objectMapper.writeValueAsString(profile1);
        String user_json2 = objectMapper.writeValueAsString(profile2);

        this.mockMvc.perform(put("/PS/profiles")
                        .content(user_json1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        this.mockMvc.perform(put("/PS/profiles")
                        .content(user_json2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isConflict());
    }


    @Test
    public void putProfileShouldSucceed() throws Exception {
        Profile profile = new
                Profile(1, "Hans",
                "test@example.com", "foo");
        ObjectMapper objectMapper = new ObjectMapper();
        String profile_json = objectMapper.writeValueAsString(profile);

        this.mockMvc.perform(put("/PS/profiles")
                        .content(profile_json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnErrorOnWrongProfile() throws Exception {
        this.mockMvc.perform(get("/PS/profiles/{id}", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void profileCanBeAdded() throws Exception {
        Profile profile = new Profile(1, "hallo", "test@example.com", "foo");

        ObjectMapper objectMapper = new ObjectMapper();
        String user_json = objectMapper.writeValueAsString(profile);

        this.mockMvc.perform(put("/PS/profiles")
                        .content(user_json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/PS/profiles/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(user_json));
    }

    @Test
    public void putNameShouldChangeName () throws Exception {
        Profile profile = new
                Profile(1, "Hans",
                "test@example.com", "foo");
        String new_name = "Jörg";
        Profile profile2 = new
                Profile(1, new_name,
                "test@example.com", "foo");
        ObjectMapper objectMapper = new ObjectMapper();
        String profile_json = objectMapper.writeValueAsString(profile);
        String profile2_json = objectMapper.writeValueAsString(profile2);

        this.mockMvc.perform(put("/PS/profiles")
                        .content(profile_json)
                        .contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(put("/PS/profiles/1/name")
                .content(new_name).contentType(MediaType.TEXT_PLAIN));
        this.mockMvc.perform(get("/PS/profiles/1/name"))
                .andExpect(status().isOk())
                .andExpect(content().string(new_name));
        this.mockMvc.perform(get("/PS/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(profile2_json));
    }


}
