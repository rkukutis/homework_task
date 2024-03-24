package com.rhoopoe.myfashiontrunk.exception;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.rhoopoe.myfashiontrunk.entity.Category;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlers {

    @ExceptionHandler(ProhibitedItemException.class)
    public ProblemDetail handleProhibitedItemError(ProhibitedItemException exception) {
        log.warn(exception.getMessage());
        ProblemDetail res = ProblemDetail.forStatus(400);
        res.setTitle("PROHIBITED_ITEM_ERROR");
        res.setProperty("prohibitedCategories", exception.
                getMatchedProhibitedCategories().stream().map(Category::getName));
        res.setDetail(exception.getMessage());
        return res;
    }

    @ExceptionHandler(UnknownItemException.class)
    public ProblemDetail handleUnknownItemError(UnknownItemException exception) {
        log.warn(exception.getMessage());
        ProblemDetail res = ProblemDetail.forStatus(400);
        res.setTitle("UNKNOWN_ITEM_ERROR");
        res.setDetail(exception.getMessage());
        return res;
    }

    @ExceptionHandler(FileTypeException.class)
    public ProblemDetail handleFileTypeError(FileTypeException exception) {
        log.warn(exception.getMessage());
        ProblemDetail res = ProblemDetail.forStatus(400);
        res.setTitle("FILE_CONTENT_TYPE_ERROR");
        res.setDetail(exception.getMessage());
        return res;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ProblemDetail handleTooManyRequests(MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage());
        ProblemDetail res = ProblemDetail.forStatus(400);
        res.setTitle("REQUEST_VALIDATION_ERROR");
        res.setDetail(Objects.requireNonNull(exception.getDetailMessageArguments())[1].toString());
        return res;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ProblemDetail handleConstraintValidationError(ConstraintViolationException exception) {
        log.warn(exception.getMessage());
        ProblemDetail res = ProblemDetail.forStatus(400);
        res.setTitle("CONSTRAINT_VALIDATION_ERROR");
        res.setDetail(exception.getMessage());
        return res;
    }

    @ExceptionHandler({AmazonRekognitionException.class})
    public ProblemDetail handleRekognitionError(AmazonRekognitionException exception) {
        log.warn(exception.getMessage());
        exception.printStackTrace();
        ProblemDetail res = ProblemDetail.forStatus(500);
        res.setTitle("IMAGE_RECOGNITION_ERROR");
        res.setDetail(exception.getMessage());
        return res;
    }

    @ExceptionHandler({SdkClientException.class})
    public ProblemDetail handleAWSError(SdkClientException exception) {
        log.warn(exception.getMessage());
        exception.printStackTrace();
        ProblemDetail res = ProblemDetail.forStatus(500);
        res.setTitle("AWS_ERROR");
        res.setDetail(exception.getMessage());
        return res;
    }

    @ExceptionHandler({EntityExistsException.class})
    public ProblemDetail handleEntityExistsError(EntityExistsException exception) {
        log.warn(exception.getMessage());
        ProblemDetail res = ProblemDetail.forStatus(400);
        res.setTitle("ENTITY_EXISTS_ERROR");
        res.setDetail(exception.getMessage());
        return res;
    }
}
