package org.jsalazar.fraud.exception

class UserNotFoundException extends Exception{

    UserNotFoundException(Long id) {
        super("Could not find user " + id)
    }
}
