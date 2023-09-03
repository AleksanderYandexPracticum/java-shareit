package ru.practicum.shareit.request.Service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplIT {

    private final RequestServiceImpl requestServiceImpl;

    @Test
    void add() {
    }

    @Test
    void getYourRequestsWithResponse() {
    }

    @Test
    void listOfRequestsFromOtherUsers() {
    }

    @Test
    void getRequestsWithResponse() {
    }
}