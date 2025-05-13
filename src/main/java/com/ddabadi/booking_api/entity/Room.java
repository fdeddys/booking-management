package com.ddabadi.booking_api.entity;

import com.ddabadi.booking_api.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition="Serial")
    private int id;
    private String name;
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @JsonProperty("isActive")
    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive;
    @Column
    private String username;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;
}
