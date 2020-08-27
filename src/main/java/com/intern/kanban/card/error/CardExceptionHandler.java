package com.intern.kanban.card.error;

import java.net.SocketException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.intern.kanban.card.error.exception.ConflictException;
import com.intern.kanban.card.model.dto.ConflictInfo;

@RestControllerAdvice
public class CardExceptionHandler {

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ConflictInfo> conflictErrorHandler(ConflictException ex) {
		return new ResponseEntity<>(ex.getConflictData(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(SocketException.class)
	public ResponseEntity<SocketException> socketExceptionHandler(SocketException ex) {
		return new ResponseEntity("SOCKET ERROR", HttpStatus.BAD_REQUEST);
	}
}
