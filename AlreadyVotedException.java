package com.example.votingsystem.exception;

public class AlreadyVotedException extends RuntimeException {
    public AlreadyVotedException(String message) {
        super(message);
    }
}
