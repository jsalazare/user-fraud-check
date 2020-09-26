package org.jsalazar.fraud.service

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.client.UserServiceClient
import org.jsalazar.fraud.exception.UserNotFoundException
import org.jsalazar.fraud.model.User

class CheckFraudUserServiceImpl implements CheckFraudUserService{

    UserServiceClient userServiceClient

    LocationServiceClient locationServiceClient

    BankValidationService bankValidationService

    @Override
    User getUserById(Long userId) {
        User user = userServiceClient.getUserById(userId)
        if(!user) {
            throw new UserNotFoundException(userId)
        }

        user
    }

    @Override
    void setFraudulentUser(Long userId, boolean isFraud) {
        User user = getUserById(userId)
        user.isFraud = isFraud
        userServiceClient.saveUser(user)
    }


    @Override
    boolean isFraudulentUser(Long userId) {
        getUserById(userId).isFraud
    }


    @Override
    boolean validateUserLocation (Long userId, String ip){
        User user = getUserById(userId)
        locationServiceClient.validateAddressMatchesIP(ip, user.address)
    }

    @Override
    boolean validateUserPaymentMethods (Long userId){
        User user = getUserById(userId)
        bankValidationService.validatePaymentMethod(user.paymentMethods)
    }
}
