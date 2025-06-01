package com.dsi.project.phoneBook.service;

import com.dsi.project.phoneBook.customExceptions.*;
import com.dsi.project.phoneBook.dao.ContactRepository;
import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.entities.Contact;
import com.dsi.project.phoneBook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Service
public class ContactService {
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    @Autowired
    public ContactService(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }
    public List<Contact> getContacts(Principal principal) {
        try{
            return this.contactRepository.getContactByUser(this.userRepository.getUserByEmail(principal.getName()).getId());
        }catch (Exception e){
            throw e;
        }
    }
    public Contact getContactById(Principal principal, Integer cId) {
        try{
            return this.contactRepository.findById(cId).get();
        } catch (Exception e) {
            throw e;
        }
    }
    @Transactional
    public Boolean addContact(Contact contact, MultipartFile file, Principal principal)  throws AddFailedException, FileStorageException, DuplicateEntryException, UpdateContactException  {
        boolean checkUpdate=false;
        try {
            User user = this.userRepository.getUserByEmail(principal.getName());
            Contact managedContact;
            if (contact.getCId() != null) {
                checkUpdate = true;
                managedContact = this.contactRepository.findById(contact.getCId()).orElseThrow(() -> new contactNotFoundException("Something went wrong during update!"));
            } else {
                managedContact = new Contact();
            }
            managedContact.setName(contact.getName());
            managedContact.setSecondName(contact.getSecondName());
            managedContact.setWork(contact.getWork());
            managedContact.setEmail(contact.getEmail());
            managedContact.setPhoneNumber(contact.getPhoneNumber());
            managedContact.setDescription(contact.getDescription());

            if (!file.isEmpty()) {
                managedContact.setImage(file.getOriginalFilename());
                File saveFile = new File("uploads/contactImages/" + file.getOriginalFilename());
                Files.copy(file.getInputStream(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                if (contact.getCId() == null) {
                    managedContact.setImage("default.jpg");
                } else {
                    managedContact.setImage(managedContact.getImage());
                }
            }
            managedContact.setUser(user);
            this.contactRepository.save(managedContact);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw !checkUpdate ? new DuplicateEntryException("Email already exists") : new UpdateContactException("Email already exists!");
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename());
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void deleteContact(Principal principal, Integer cId) throws FileCanNotBeDeletedException {
        try {
            String userEmail = principal.getName();
            Contact contact = this.contactRepository.findById(cId).get();
            if (this.userRepository.getUserByEmail(userEmail).getId().equals(contact.getUser().getId())) {
                Path path = Paths.get("uploads/contactImages", contact.getImage());
                if (contact.getImage() != null && !contact.getImage().equals("default.jpg")) {
                    Files.deleteIfExists(path);
                }
                this.contactRepository.deleteById(cId);
            }
        } catch (IOException ex) {
            throw new FileCanNotBeDeletedException("File Can Not Be Deleted!");
        } catch (Exception ex) {
            throw ex;
        }
    }
}
