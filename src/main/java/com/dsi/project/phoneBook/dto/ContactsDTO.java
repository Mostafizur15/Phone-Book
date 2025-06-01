package com.dsi.project.phoneBook.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class ContactsDTO {
    @NotEmpty(message = "Please select at least one contact")
    List<String> selectedContacts;
}
