package com.example.ProfileService;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
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
@AutoConfigureMockRestServiceServer
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceApplicationTests {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProfilesShouldReturnEmptyArray() throws Exception {
        this.mockMvc.perform(get("/PS/profiles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /*
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
    }*/

    @Test
    public void shouldReturnErrorOnWrongProfile() throws Exception {
        this.mockMvc.perform(get("/PS/profiles/{id}", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /*@Test
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
    }*/

    @Test
    public void putNameShouldCheckToken () throws  Exception {
        Profile profile = new
                Profile(1, "Hans",
                "test@example.com", "foo");
        String new_name = "Jörg";
        ObjectMapper objectMapper = new ObjectMapper();
        String profile_json = objectMapper.writeValueAsString(profile);

        server.expect(once(),
                        requestTo("http://localhost:8081/AS/users")).andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("1",
                        MediaType.APPLICATION_JSON));

        server.expect(once(),
                        requestTo("http://localhost:8081/AS/token")).andExpect(method(HttpMethod.GET))
                .andExpect(
                        header("X-Token", "falsetoken"))
                .andRespond(withUnauthorizedRequest());

        server.expect(once(), requestTo("http://localhost:8081/AS/token")).andExpect(method(HttpMethod.GET))
                .andExpect(
                        header("X-Token", "truetoken"))
                .andRespond(withSuccess("1", MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/PS/profiles")
                .content(profile_json)
                .contentType(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/PS/profiles/1/name")
                .header("X-Token", "falsetoken")
                .content(new_name).contentType(MediaType.TEXT_PLAIN))
                        .andExpect(status().isUnauthorized());

        this.mockMvc.perform(get("/PS/profiles/1/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hans"));

        this.mockMvc.perform(put("/PS/profiles/1/name")
                        .header("X-Token", "truetoken")
                        .content(new_name).contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    public void putNameShouldChangeName () throws Exception {
        server.expect(once(),
                        requestTo("http://localhost:8081/AS/users"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("1", MediaType.APPLICATION_JSON));

        server.expect(manyTimes(),
                        requestTo("http://localhost:8081/AS/token")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("1", MediaType.APPLICATION_JSON));

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
                        .header("X-Token", "testtoken")
                        .contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(put("/PS/profiles/1/name")
                .header("X-Token", "testtoken")
                .content(new_name).contentType(MediaType.TEXT_PLAIN));
        this.mockMvc.perform(get("/PS/profiles/1/name"))
                .andExpect(status().isOk())
                .andExpect(content().string(new_name));
        this.mockMvc.perform(get("/PS/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(profile2_json));
    }


}
