package com.dsi.project.phoneBook.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private String id;
    private int amount;
    private String currency;
    private String receipt;
    private String status;
}
