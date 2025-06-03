package com.dsi.project.phoneBook.entities;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order implements Serializable {
    private String id;
    private int amount;
    private String currency;
    private String receipt;
    private String status;
}
