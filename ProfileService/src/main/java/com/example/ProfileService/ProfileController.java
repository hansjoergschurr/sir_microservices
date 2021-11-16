package com.example.ProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ProfileController {
    private final AtomicLong counter =
            new AtomicLong();
    private final Map<Long, Profile> profiles =
            new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Value("${service.authentication}")
    private String auth_service_url;

    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    @GetMapping("/PS/profiles")
    @CrossOrigin
    public Collection<Profile> profiles() {
        logger.trace("GET /PS/profiles");
        return profiles.values();
    }
    @PutMapping("/PS/profiles")
    @CrossOrigin
    public Profile profiles_put(
            @RequestBody @Valid Profile profile) {
        logger.trace("PUT /PS/profiles");
        if (emails.contains(profile.getEmail()))
            throw new EmailInUseException(profile.getEmail());
        long new_id = counter.incrementAndGet();
        profile.setId(new_id);

       AuthServiceUser auth_service_user = new AuthServiceUser(new_id);
       RestTemplate restTemplate = new RestTemplate();
       restTemplate.put(
               auth_service_url + "/AS/users",
               auth_service_user);

        profiles.put(new_id, profile);
        emails.add(profile.getEmail());
        logger.info(String.format("Profile created: [%d] %s.",
                new_id, profile.getEmail()));
        return profile;
    }

    @GetMapping("/PS/profiles/{id}")
    @CrossOrigin
    public Profile profiles_get(
        @PathVariable(value = "id") Long id) {
        logger.trace(String.format("GET /PS/profiles/%d", id));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        return profiles.get(id);
    }

    @PutMapping("/PS/profiles/{id}")
    @CrossOrigin
    public Profile profiles_update_put(
            @RequestBody @Valid Profile profile,
            @PathVariable(value = "id") Long id) {
        logger.trace(String.format("PUT /PS/profiles/%d", id));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        Profile old_profile = profiles.get(id);
        if (!old_profile.getEmail().equals(profile.getEmail())) {
          if(emails.contains(profile.getEmail()))
            throw new EmailInUseException(profile.getEmail());
          // Exchange stored EMail
          emails.remove(old_profile.getEmail());
          emails.add(profile.getEmail());
        }
        profile.setId(id);
        profiles.put(id, profile);
        return profile;
    }

    @DeleteMapping("/PS/profiles/{id}")
    @CrossOrigin
    public void profile_delete(
            @PathVariable(value = "id") Long id,
            @RequestHeader(value = "X-Token") String token) {
        logger.trace(String.format("DELETE /PS/profiles/%d", id));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.add("X-Token", token);
        HttpEntity<String> entity = new HttpEntity<String>("", header);
        ResponseEntity<Long> response = restTemplate.exchange(
                auth_service_url + "/AS/token",
                HttpMethod.GET, entity, Long.class);
        Long token_user = response.getBody();
        if (!Objects.equals(token_user, id))
            throw new RuntimeException();

        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        Profile profile = profiles.get(id);
        emails.remove(profile.getEmail());
        profiles.remove(id);
        logger.info(String.format("Deleted profile [%d] %s.", profile.getId(), profile.getEmail()));
    }

    @GetMapping("/PS/profiles/{id}/name")
    @CrossOrigin
    public String profile_get_name(@PathVariable(value = "id") Long id) {
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        logger.trace(String.format("GET /PS/profiles/%d/name", id));
        return profiles.get(id).getName();
    }

    @GetMapping("/PS/profiles/{id}/email")
    @CrossOrigin
    public String profile_get_email(@PathVariable(value = "id") Long id) {
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        logger.trace(String.format("GET /PS/profiles/%d/email", id));
        return profiles.get(id).getEmail();
    }

    @GetMapping("/PS/profiles/{id}/description")
    @CrossOrigin
    public String profile_get_description(@PathVariable(value = "id") Long id) {
        logger.trace(String.format("GET /PS/profiles/%d/description", id));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        return profiles.get(id).getDescription();
    }

    @PutMapping("/PS/profiles/{id}/name")
    @CrossOrigin
    public Profile update_name(
            @PathVariable(value = "id") Long id,
            @RequestBody String name) {
        logger.trace(String.format("PUT /PS/profiles/%d/name", id));
        logger.trace(String.format("\tnew name: %s.", name));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        Profile profile = profiles.get(id);
        logger.trace(String.format("\told name: %s.", profile.getName()));
        profile.setName(name);
        return profile;
    }

    @PutMapping("/PS/profiles/{id}/description")
    @CrossOrigin
    public Profile update_description(
            @PathVariable(value = "id") Long id,
            @RequestBody String description) {
        logger.trace(String.format("PUT /PS/profiles/%d/description", id));
        logger.trace(String.format("\tnew description: %s.", description));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        Profile profile = profiles.get(id);
        logger.trace(String.format("\told description: %s.", profile.getDescription()));
        profile.setDescription(description);
        return profile;
    }

    @PutMapping("/PS/profiles/{id}/email")
    @CrossOrigin
    public Profile update_email(
            @PathVariable(value = "id") Long id,
            @RequestBody String email) {
        logger.trace(String.format("PUT /PS/profiles/%d/email", id));
        logger.trace(String.format("\tnew email: %s.", email));
        if (!profiles.containsKey(id))
            throw new ProfileNotFoundException(id);
        Profile profile = profiles.get(id);
        if (emails.contains(profile.getEmail()))
            throw new EmailInUseException(profile.getEmail());
        emails.remove(profile.getEmail());
        emails.add(email);
        logger.trace(String.format("\told email: %s.", profile.getEmail()));
        profile.setEmail(email);
        return profile;
    }
}
