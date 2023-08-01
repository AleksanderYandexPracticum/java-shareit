package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // если ошибка валидации: ValidationException
    public ErrorResponse handle(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   // для всех ситуаций, если искомый объект не найден
    public ErrorResponse handle(final NotFoundException e) {
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  //если возникло исключение.
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Возникло исключение", e.getMessage());
    }
}
