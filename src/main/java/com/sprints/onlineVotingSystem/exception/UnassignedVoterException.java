package com.sprints.onlineVotingSystem.exception;

public class UnassignedVoterException extends RuntimeException {
    
    public UnassignedVoterException(String message) {
        super(message);
    }
    
    public UnassignedVoterException(String message, Throwable cause) {
        super(message, cause);
    }
}
