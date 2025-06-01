package com.dsi.project.phoneBook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class EmailFormDTO {
    @Email(message = "Invalid email format!")
    @NotEmpty(message = "Email cannot be empty!")
    private String toEmail;
    @NotEmpty(message = "No contacts are selected!")
    private List<String> selectedContacts;
}
