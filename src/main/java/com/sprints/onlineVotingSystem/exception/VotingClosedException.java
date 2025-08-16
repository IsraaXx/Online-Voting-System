package com.sprints.onlineVotingSystem.exception;

public class VotingClosedException extends RuntimeException {
    
    public VotingClosedException(String message) {
        super(message);
    }
    
    public VotingClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
