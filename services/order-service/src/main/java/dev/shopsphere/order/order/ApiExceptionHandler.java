package dev.shopsphere.order.order;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleNotFound(OrderNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problem.setTitle("Order not found");

        return problem;
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ProblemDetail handleInvalidOrder(InvalidOrderException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problem.setTitle("Invalid order");

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );

        problem.setTitle("Validation failed");

        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request violates one or more constraints"
        );

        problem.setTitle("Validation failed");

        return problem;
    }
}