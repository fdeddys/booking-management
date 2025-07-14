
package com.ddabadi.booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booking")
public class Booking implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    @Column(columnDefinition="timestamp")
    private LocalDateTime startTime;

    @Column(columnDefinition="timestamp")
    private LocalDateTime endTime;

    private String phoneNumber;

    private Integer numberOfParticipants;

    private String eventName;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
