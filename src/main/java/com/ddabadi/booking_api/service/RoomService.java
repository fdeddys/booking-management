package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.dto.RoomRequestDto;
import com.ddabadi.booking_api.dto.common.ResponseWithContentDTO;
import com.ddabadi.booking_api.entity.Room;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.RoomRepository;
import com.ddabadi.booking_api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ddabadi.booking_api.constant.ErrorCodeConstant.*;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.DATABASE.CODE_FAILED_SAVE_DB;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.DATABASE.CODE_FAILED_SAVE_DB_MESSAGE;

@Service
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UtilService utilService;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository, UtilService utilService) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.utilService = utilService;
    }

    public Page<Room> findByName(RoomRequestDto request, int page, int count) {

        log.info("request : {}, page : {} ",request.getName(), page);
        String nameKriteria = generateRequest(request.getName());
        Sort sort = utilService.generateSort("name", "description", "username" );
        Pageable pageable = PageRequest.of(page-1, count, sort);
        return roomRepository
                .findByNameIgnoreCaseLike(
                        nameKriteria,
                        pageable
                );


    }

    String generateRequest(String kriteria) {
        StringBuilder result = new StringBuilder("%");
        if (kriteria.equals("")){
            return result.toString();
        }
        return result.append(kriteria).append("%").toString();
    }

    public Optional<Room> getRoomById(Integer id) {
        return Optional.of(
                roomRepository
                .findById(id)
                .orElse(new Room()));
    }

    public Optional<List<Room>> getAllRoom() {
        return Optional.of(roomRepository
                .findAll()
        );
    }

    public ResponseWithContentDTO createRoom(Room room, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            log.info("User not found : {}", username);
            return generateResponse(
                    USER.CODE_USER_NOT_FOUND,
                    USER.CODE_USER_NOT_FOUND_MESSAGE,
                    null
            );
        }
        log.info("user found");

        Optional<Room> optionalRoom = roomRepository.findByNameIgnoreCase(room.getName());
        if (optionalRoom.isPresent()) {
            log.info("Room name already exist : {}", room.getName());
            return generateResponse(
                    ROOM.CODE_ROOM_NAME_EXIST,
                    ROOM.CODE_ROOM_NAME_EXIST_MESSAGE,
                    null
            );
        }

        try {
            room.setUpdated(LocalDateTime.now());
            room.setUsername(optionalUser.get().getUsername());
            roomRepository.save(room);
            log.info("Room success save : {}", room.toString());
        } catch (Exception exception) {
            log.error("Error save data to database : {}", exception.getMessage());
            exception.printStackTrace();
            return generateResponse(
                    CODE_FAILED_SAVE_DB,
                    CODE_FAILED_SAVE_DB_MESSAGE + " : " + exception.getMessage(),
                    null
            );
        }
        return generateResponse(
                CODE_SUCCESS,
                CODE_SUCCESS_MESSAGE,
                room
        );
    }

    ResponseWithContentDTO generateResponse(String errCode, String errDesc, Object object){
        ResponseWithContentDTO response = new ResponseWithContentDTO();
        response.setErrorCode(errCode);
        response.setErrorDescription(errDesc);
        response.setObject(object);
        return response;
    }

    public ResponseWithContentDTO updateRoom(Room room, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            log.info("User not found : {}", username);
            return generateResponse(
                    USER.CODE_USER_NOT_FOUND,
                    USER.CODE_USER_NOT_FOUND_MESSAGE,
                    null
            );
        }
        log.info("user found");

        Optional<Room> optionalRoom = roomRepository.findById(room.getId());
        if (optionalRoom.isEmpty()) {
            log.info("Room ID not found : {}", room.getName());
            return generateResponse(
                    ROOM.CODE_ROOM_ID_NOT_EXIST,
                    ROOM.CODE_ROOM_ID_NOT_EXIST_MESSAGE,
                    null
            );
        }
        if (!room.getName().equals(optionalRoom.get().getName())) {
            Optional<Room> validateNewRoom = roomRepository.findByNameIgnoreCase(room.getName());
            if (validateNewRoom.isPresent()) {
                log.info("Room name already exist : {}", room.getName());
                return generateResponse(
                        ROOM.CODE_ROOM_NAME_EXIST,
                        ROOM.CODE_ROOM_NAME_EXIST_MESSAGE,
                        null
                );
            }
        }


        Room updateRoom = optionalRoom.get();
        try {
            updateRoom.setDescription(room.getDescription());
            updateRoom.setName(room.getName());
            updateRoom.setUpdated(LocalDateTime.now());
            updateRoom.setUsername(optionalUser.get().getUsername());
            updateRoom.setStatus(room.getStatus());
            roomRepository.save(updateRoom);
            log.info("Room success save : {}", room.toString());
        } catch (Exception exception) {
            log.error("Error save data to database : {}", exception.getMessage());
            exception.printStackTrace();
            return generateResponse(
                    CODE_FAILED_SAVE_DB,
                    CODE_FAILED_SAVE_DB_MESSAGE + " : " + exception.getMessage(),
                    null
            );
        }
        return generateResponse(
                CODE_SUCCESS,
                CODE_SUCCESS_MESSAGE,
                updateRoom
        );
    }
}
