package org.jsalazar.fraud.service

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.BankValidationServiceStub
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.client.LocationServiceClientStub
import org.jsalazar.fraud.client.UserServiceClient
import org.jsalazar.fraud.client.UserServiceClientStub
import org.jsalazar.fraud.configuration.CheckFraudUserServiceConfiguration

class FraudValidationServiceFactory {

    static CheckFraudUserService getCheckFraudUserService (UserServiceClient userServiceClient, LocationServiceClient locationServiceClient, BankValidationService bankValidationService, CheckFraudUserServiceConfiguration checkFraudUserServiceConfiguration){
        new CheckFraudUserServiceImpl(userServiceClient: userServiceClient, locationServiceClient: locationServiceClient, bankValidationService: bankValidationService, checkFraudUserServiceConfiguration: checkFraudUserServiceConfiguration)
    }

    static CheckFraudUserService getDefaultCheckFraudUserService (CheckFraudUserServiceConfiguration checkFraudUserServiceConfiguration){
        new CheckFraudUserServiceImpl(userServiceClient: new UserServiceClientStub(),
                locationServiceClient: new LocationServiceClientStub(),
                bankValidationService: new BankValidationServiceStub(),
                checkFraudUserServiceConfiguration: checkFraudUserServiceConfiguration)
    }

    static ValidationServiceImpl getValidatorService (LocationServiceClientStub locationServiceClientStub, BankValidationServiceStub bankValidationServiceStub){
        new ValidationServiceImpl(locationServiceClient: locationServiceClientStub, bankValidationService: bankValidationServiceStub)
    }

    static ValidationServiceImpl getDefaultValidatorService (){
        new ValidationServiceImpl(locationServiceClient: new LocationServiceClientStub(), bankValidationService: new BankValidationServiceStub())
    }
}
