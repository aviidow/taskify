package io.github.aviidow.taskify.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// Генерация traceId для каждого запроса (можно улучшить с MDC)
	private String getTraceId(WebRequest request) {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	// 404 - Resource not found
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.warn("Resource not found: {}", ex.getMessage());

		ErrorResponse error = ErrorResponse.of(
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				ex.getMessage(),
				request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	// 403 - Access denied
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(
			AccessDeniedException ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.warn("Access denied: {}", ex.getMessage());

		ErrorResponse error = ErrorResponse.of(
				HttpStatus.FORBIDDEN.value(),
				"Forbidden",
				ex.getMessage(),
				request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	// 403 - Spring Security Access Denied
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleSpringAccessDenied(
			AccessDeniedException ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.warn("Spring Security access denied: {}", ex.getMessage());

		ErrorResponse error = ErrorResponse.of(
				HttpStatus.FORBIDDEN.value(),
				"Forbidden",
				"You don't have permission to access this resource",
				request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	// 409 - Duplicate resource
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResource(
			DuplicateResourceException ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.warn("Duplicate resource: {}", ex.getMessage());

		ErrorResponse error = ErrorResponse.of(
				HttpStatus.CONFLICT.value(),
				"Conflict",
				ex.getMessage(),
				request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	// 400 - Validation errors (@Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(
			MethodArgumentNotValidException ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.warn("Validation failed: {}", ex.getMessage());

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ErrorResponse error = ErrorResponse.validationError(request.getRequestURI(), errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 400 - Method argument type mismatch
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handeTypeMismatch(
			MethodArgumentTypeMismatchException ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.warn("Type mismatch: {}", ex.getMessage());

		String message = String.format("Parameter '%s' should be of type %s",
				ex.getName(), ex.getRequiredType().getSimpleName());

		ErrorResponse error = ErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				message,
				request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// 500 - Все остальные ошибки
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex,
			HttpServletRequest request,
			WebRequest webRequest
	) {
		log.error("Unexpected error: ", ex);

		ErrorResponse error = ErrorResponse.of(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				"An unexpected error occured. Please try again later.",
				request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
