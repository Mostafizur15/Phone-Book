package com.dsi.project.phoneBook.dao;

import com.dsi.project.phoneBook.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {

    @Query("from Contact as c where c.user.id=:userId")
    public List<Contact> getContactByUser(@Param("userId") Integer userId);
    @Query("from Contact as c where c.email IN :emails")
    public List<Contact> getContactByEmails(@Param("emails") List<String> emails);
}
