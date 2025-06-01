package com.dsi.project.phoneBook.service;

import com.dsi.project.phoneBook.customExceptions.GoogleConnectionException;
import com.dsi.project.phoneBook.dao.ContactRepository;
import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.entities.Contact;
import com.dsi.project.phoneBook.entities.User;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailContactService {

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;
    public List<Contact> loadContact(String code, String clientId, String redirectUri, String userName){
        List<Contact> allContacts = new ArrayList<>();
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "code=" + code +
                                    "&client_id=" + clientId +
                                    "&client_secret=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" +
                                    "&redirect_uri=" + redirectUri +
                                    "&grant_type=authorization_code"
                    ))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            String accessToken = json.getString("access_token");

            PeopleService peopleService = new PeopleService.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    request1 -> request1.getHeaders().setAuthorization("Bearer " + accessToken))
                    .setApplicationName("PhoneBook")
                    .build();

            ListConnectionsResponse connections = peopleService.people().connections()
                    .list("people/me")
                    .setPersonFields("names,emailAddresses,phoneNumbers,biographies")
                    .execute();

            Contact contact = new Contact();
            User user = this.userRepository.getUserByEmail(userName);
            for (Person person : connections.getConnections()) {
                String name = person.getNames() != null ? person.getNames().get(0).getDisplayName() : null;
                String secondName = person.getNicknames() != null ? person.getNicknames().get(0).getValue() : null;
                String email = person.getEmailAddresses() != null ? person.getEmailAddresses().get(0).getValue() : null;
                String phone = person.getPhoneNumbers() != null ? person.getPhoneNumbers().get(0).getValue() : null;
                String bio = person.getBiographies() != null ? person.getBiographies().get(0).getValue() : null;
                if (name != null) {
                    allContacts.add(new Contact(name,secondName,email,phone,"default.jpg",user));
                }
            }
        } catch (Exception ex) {
            throw new GoogleConnectionException("Something went wrong");
        }
        return allContacts;
    }

    public void saveContacts(List<Contact> selectedContacts) {
        try{
            this.contactRepository.saveAll(selectedContacts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
