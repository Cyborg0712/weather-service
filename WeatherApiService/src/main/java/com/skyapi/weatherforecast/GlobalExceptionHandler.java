package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.hourlyweather.BadRequestBodyException;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex) {
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

       log.error(ex.getMessage(), ex);

        return error;
    }

    @ExceptionHandler({BadRequestBodyException.class, GeolocationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestBodyException(HttpServletRequest request, Exception ex) {
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        log.error(ex.getMessage());

        return error;
    }

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception e) {
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(e.getMessage());
        error.setPath(request.getServletPath());

        log.error(e.getMessage());

        return error;
    }



    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex) {
        ErrorDTO error = new ErrorDTO();

        ConstraintViolationException violationException = (ConstraintViolationException)ex;

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());

        error.setPath(request.getServletPath());

        var constraintViolation = violationException.getConstraintViolations();

        constraintViolation.forEach(violation ->  error.addError(violation.getPropertyPath() + ":" +violation.getMessage()));

        log.error(ex.getMessage());

        return error;

    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
       ErrorDTO error =  new ErrorDTO();
       error.setTimestamp(new Date());
       error.setStatus(HttpStatus.BAD_REQUEST.value());
       error.setPath(((ServletWebRequest)request).getRequest().getServletPath());

       List<FieldError> fieldErrors =  ex.getBindingResult().getFieldErrors();
       fieldErrors.forEach(fieldError -> {
           error.addError(fieldError.getDefaultMessage());
       });

       log.error(ex.getMessage());

       return new ResponseEntity<>(error, headers, status);
    }
}
