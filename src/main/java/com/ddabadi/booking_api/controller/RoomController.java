package com.ddabadi.booking_api.controller;

import com.ddabadi.booking_api.dto.RoomRequestDto;
import com.ddabadi.booking_api.entity.Room;
import com.ddabadi.booking_api.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/room")
@Slf4j
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/filter")
    public ResponseEntity<Object> filterBookings(@RequestBody RoomRequestDto request) {
        log.info("ROOM Filter : {} ", request);
        return ResponseEntity.ok(roomService.findByName(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public Object createRoom(@RequestBody Room room) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("CreteRoom: token valid");
        return roomService.createRoom(room, username);
    }

    @PutMapping
    public Object updateRoom(@RequestBody Room room) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Update Room: token valid");
        return roomService.updateRoom(room, username);
    }
}
