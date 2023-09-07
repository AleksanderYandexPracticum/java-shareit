package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Mock
    private UserServiceImpl userServiceimpl;

    @Mock
    private BookingServiceImpl bookingServiceImpl;

    @Test
    void handle() {
        when(bookingServiceImpl.add(any(), any(), any())).thenThrow(new ValidationException("The item is not available for booking"));
        ValidationException validationException = assertThrows(ValidationException.class, () -> bookingServiceImpl.add(any(), any(), any()));
        assertEquals("The item is not available for booking", validationException.getMessage());

    }

    @Test
    void testHandle() {
        when(userServiceimpl.add(any())).thenThrow(new NotFoundException("There is no such thing, it is not available for booking"));
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> userServiceimpl.add(any()));
        assertEquals("There is no such thing, it is not available for booking", notFoundException.getMessage());
    }

    @Test
    void handleThrowable() {
        when(userServiceimpl.add(any())).thenThrow(new DuplicateEmailException("Duplicate of the user's email address"));
        DuplicateEmailException duplicateEmailException = assertThrows(DuplicateEmailException.class, () -> userServiceimpl.add(any()));
        assertEquals("Duplicate of the user's email address", duplicateEmailException.getMessage());
    }

    @Test
    void testHandle1() {
        when(bookingServiceImpl.getAllBookingsByUserId(any(), any(), any(), any())).thenThrow(new StatusException("Unknown state: UNSUPPORTED_STATUS"));
        StatusException statusException = assertThrows(StatusException.class, () -> bookingServiceImpl.getAllBookingsByUserId(any(), any(), any(), any()));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", statusException.getMessage());

    }
}
