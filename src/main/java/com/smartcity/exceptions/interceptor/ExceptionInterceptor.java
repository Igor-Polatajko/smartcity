package com.smartcity.exceptions.interceptor;

import com.google.common.base.Preconditions;
import com.smartcity.exceptions.DbOperationException;
import com.smartcity.exceptions.InvalidJwtAuthenticationException;
import com.smartcity.exceptions.NotFoundException;
import com.smartcity.exceptions.RecordExistsException;
import com.smartcity.exceptions.json.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestControllerAdvice
public class ExceptionInterceptor {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ExceptionResponse notFoundException(@NonNull HttpServletRequest request,
                                        @NonNull NotFoundException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getLocalizedMessage());

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(ex.getLocalizedMessage())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    ExceptionResponse illegalArgumentException(@NonNull HttpServletRequest request,
                                        @NonNull IllegalArgumentException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getLocalizedMessage());

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(ex.getLocalizedMessage())
                .build();
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(RecordExistsException.class)
    ExceptionResponse recordExistsException(@NonNull HttpServletRequest request,
                                            @NonNull RecordExistsException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getLocalizedMessage());

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(ex.getLocalizedMessage())
                .build();
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DbOperationException.class)
    ExceptionResponse databaseOperationException(@NonNull HttpServletRequest request,
                                                 @NonNull DbOperationException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getLocalizedMessage());

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(ex.getLocalizedMessage())
                .build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ExceptionResponse methodArgumentNotValidException(@NonNull HttpServletRequest request,
                                                      @NonNull MethodArgumentNotValidException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getBindingResult().getFieldError());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        StringBuilder message = new StringBuilder();

        for (FieldError f:fieldErrors) {
            message.append(f.getField() + ": "+ f.getDefaultMessage() + "; ");
        }
        String errorMessage = message.toString();

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(errorMessage)
                .build();
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(BadCredentialsException.class)
    ExceptionResponse badCredentialsException(@NonNull HttpServletRequest request,
                                              @NonNull BadCredentialsException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getLocalizedMessage());

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(ex.getLocalizedMessage())
                .build();
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    ExceptionResponse invalidJwtAuthenticationException(@NonNull HttpServletRequest request,
                                              @NonNull InvalidJwtAuthenticationException ex) {
        Preconditions.checkNotNull(request.getRequestURI());
        Preconditions.checkNotNull(ex.getLocalizedMessage());

        return ExceptionResponse.builder()
                .url(request.getRequestURI())
                .message(ex.getLocalizedMessage())
                .build();
    }
}
