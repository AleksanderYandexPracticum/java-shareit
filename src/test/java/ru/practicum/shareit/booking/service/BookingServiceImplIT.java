package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIT {

    private final EntityManager em;
    private final BookingServiceImpl bookingServiceImpl;

    @Test
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void getById() {
    }

    @Test
    void getAllBookingsByUserId() {
    }

    @Test
    void getAllBookingsAllItemsByUserId() {
    }
}