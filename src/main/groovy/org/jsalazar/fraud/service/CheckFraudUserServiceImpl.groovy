package org.jsalazar.fraud.service

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.client.UserServiceClient
import org.jsalazar.fraud.configuration.CheckFraudUserServiceConfiguration
import org.jsalazar.fraud.exception.UserNotFoundException
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.rules.CustomRule

class CheckFraudUserServiceImpl implements CheckFraudUserService{

    UserServiceClient userServiceClient

    LocationServiceClient locationServiceClient

    BankValidationService bankValidationService

    CheckFraudUserServiceConfiguration checkFraudUserServiceConfiguration

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

    @Override
    boolean validateReportsFraudulentUser(Long userId) {
        if(isFraudulentUser(userId)){
            return true
        }

        int sum = 0
        checkFraudUserServiceConfiguration.fraudReportsTypes.each {
            sum += userServiceClient.getUserReportByType(userId, it).size()
        }

        if (sum >= checkFraudUserServiceConfiguration.reportsLimit){
            setFraudulentUser(userId, true)
            return true
        }

        false
    }

    /**
     * validates if user is fraudulent based on custom rules
     * @param userId user id of user to validate
     * @param customRules custom rules if one is true
     * @param landlineEnabled Landline status for the destination. If false, all landline call activity will be
     *                        rejected or disabled.
     * @return true if at least one rule is true
     */
    @Override
    boolean validateFraudulentUserCustomRule (Long userId, CustomRule<User> customRules, boolean markInBlackList) {
        User user = getUserById(userId)
        if (customRules.apply(user)){
            if (markInBlackList){
                setFraudulentUser(userId, true)
            }
            return true
        }
        false
    }
}
