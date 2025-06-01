package com.dsi.project.phoneBook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmailDTO {
    @Email
    @NotBlank
    private String email;
}
