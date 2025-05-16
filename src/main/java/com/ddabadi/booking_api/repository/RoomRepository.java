package com.ddabadi.booking_api.repository;

import com.ddabadi.booking_api.entity.Room;
import com.ddabadi.booking_api.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>, JpaSpecificationExecutor<Room> {

    Page<Room> findByNameIgnoreCaseLikeAndStatusAndIsActive(
            String name,
            Status status,
            Boolean isActive,
            Pageable pageable
    );

    Page<Room> findByNameIgnoreCaseLike(
            String name,
            Pageable pageable
    );


    Optional<Room> findByNameIgnoreCase(String name);

}
