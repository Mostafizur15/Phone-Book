package com.dsi.project.phoneBook.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cId;
    private String name;
    private String secondName;
    private String work;
   /* @Column(unique = true)*/
    private String email;
    private String phoneNumber;
    private String image;
    @Column(length = 50000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Contact(String name, String secondName, String email, String phone, String image, User user) {
        this.name = name;
        this.secondName = secondName;
        this.email = email;
        this.phoneNumber = phone;
        this.image = image;
        this.user = user;
    }
}
