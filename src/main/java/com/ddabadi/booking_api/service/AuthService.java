package com.ddabadi.booking_api.service;

import com.ddabadi.booking_api.constant.BookingConstant;
import com.ddabadi.booking_api.dto.AuthRequest;
import com.ddabadi.booking_api.dto.LoginResponseDTO;
import com.ddabadi.booking_api.dto.LoginResponseUserDTO;
import com.ddabadi.booking_api.entity.AESKeyPair;
import com.ddabadi.booking_api.entity.Role;
import com.ddabadi.booking_api.repository.UserRepository;
import com.ddabadi.booking_api.security.CustomUserDetailsService;
import com.ddabadi.booking_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.ddabadi.booking_api.constant.BookingConstant.SECURITY.CIPHER_ALGORITHM;

@Service
@RequiredArgsConstructor
public class AuthService  {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final Map<String, AESKeyPair> sessionKeyCache = new ConcurrentHashMap<>();


//    public String register(RegisterRequest request) {
//        var user = User.builder()
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.USER)
//                .build();
//
//        userRepository.save(user);
//
//        return jwtService.generateToken(user);
//    }

    public Object authenticate(AuthRequest request) {

        AESKeyPair keyPair = sessionKeyCache.remove(request.getSessionToken());
        String password="";
        if (keyPair == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        byte[] encryptedPassword = Base64.getDecoder().decode(request.getEncryptedPassword());
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, keyPair.getIv());
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getKey(), gcmParameterSpec);
            byte[] decryptBytes = cipher.doFinal(encryptedPassword);

            password = new String(decryptBytes, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            throw new RuntimeException("Error : "+ ex.getMessage().toString());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        password
                )
        );
        if (!authentication.isAuthenticated()) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        UserDetails user = (UserDetails) authentication.getPrincipal();

//        UserDetails user = customUserDetailsService.loadUserByUsername(request.getUsername());

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setServiceToken(jwtService.generateToken(user));

        LoginResponseUserDTO userDto = new LoginResponseUserDTO();
        userDto.setId(userDto.getId());
        userDto.setName(request.getUsername());
        userDto.setRole(Role.ADMIN);

        loginResponseDTO.setUser(userDto);

        System.out.println("Login succeessss...");
        return loginResponseDTO;
    }



    private byte[] generateIV() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private SecretKey generateAESKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(BookingConstant.SECURITY.AES_KEY);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

   public  Object challangeProses(String username) {
        String sessionToken = UUID.randomUUID().toString().replace("-","");
        SecretKey secretKey = generateAESKey();
        byte[] iv = generateIV();

        sessionKeyCache.put(sessionToken, new AESKeyPair(secretKey, iv));
        Map<String, String> response = new HashMap<>();
        response.put("sessionToken", sessionToken);
        response.put("key", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        response.put("nonce",  Base64.getEncoder().encodeToString(iv));
       System.out.println(response.toString());
        return ResponseEntity.ok(response);
    }

}
