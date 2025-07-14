package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.constant.ErrorCodeConstant;
import com.ddabadi.booking_api.dto.RegisterRequest;
import com.ddabadi.booking_api.dto.common.ResponseWithContentDTO;
import com.ddabadi.booking_api.entity.Booking;
import com.ddabadi.booking_api.entity.Role;
import com.ddabadi.booking_api.entity.User;
import com.ddabadi.booking_api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.*;

import static com.ddabadi.booking_api.constant.ErrorCodeConstant.CODE_SUCCESS;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.CODE_SUCCESS_MESSAGE;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.DATABASE.CODE_FAILED_SAVE_DB;
import static com.ddabadi.booking_api.constant.ErrorCodeConstant.DATABASE.CODE_FAILED_SAVE_DB_MESSAGE;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UtilService utilService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UtilService utilService) {
        this.userRepository = userRepository;
        this.utilService = utilService;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found")); // bisa kita buat sendiri
    }


    public ResponseWithContentDTO createUser(RegisterRequest request) {

        Boolean isNewRecord = request.getId() == 0 ? true : false;
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        // new record, username already used
        if (isNewRecord && optionalUser.isPresent() ) {
            log.info("User name already exist : {}", request.getUsername());
            return generateResponse(
                    ErrorCodeConstant.USER.CODE_USER_ALREADY_EXIST,
                    ErrorCodeConstant.USER.CODE_USER_ALREADY_EXIST_MESSAGE,
                    null
            );
        }

        // not New Record, but username not found
        if (!isNewRecord && optionalUser.isEmpty()  ) {
            log.info("User name already exist : {}", request.getUsername());
            return generateResponse(
                    ErrorCodeConstant.USER.CODE_USER_NOT_FOUND,
                    ErrorCodeConstant.USER.CODE_USER_NOT_FOUND_MESSAGE,
                    null
            );
        }

        String hashPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        User user = null;
        try {
            if (isNewRecord) {
                user = new User();
                user.setUsername(request.getUsername());
            } else {
                user = optionalUser.get();
            }
            user.setPassword(hashPassword);
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            log.info("User success save : {}", request.toString());
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
                request.getUsername()
        );
    }

    public String generatePassword(String username) {
        MessageDigest digest = null;
        StringBuilder text = new StringBuilder();
        text.append(username);
        text.append(":");
        text.append("password123");
        return DigestUtils.sha256Hex(text.toString());
    }

    ResponseWithContentDTO generateResponse(String errCode, String errDesc, Object object){
        ResponseWithContentDTO response = new ResponseWithContentDTO();
        response.setErrorCode(errCode);
        response.setErrorDescription(errDesc);
        response.setObject(object);
        return response;
    }


    public Object findAll(int page, int count) {
        log.info("service : page : {} ", page);
        Sort sort = utilService.generateSort("username");
        Pageable pageable = PageRequest.of(page-1, count, sort);

        return userRepository.findAll(pageable);
    }


}
