package org.jsalazar.fraud

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.client.UserServiceClient
import org.jsalazar.fraud.configuration.CheckFraudUserServiceConfiguration
import org.jsalazar.fraud.service.CheckFraudUserServiceFactory
import org.jsalazar.fraud.service.CheckFraudUserServiceImpl
import spock.lang.Shared
import spock.lang.Specification

class CheckFraudUserServiceFactorySpec extends Specification{

    @Shared
    UserServiceClient mockUserServiceClient
    @Shared
    LocationServiceClient mockLocationServiceClient
    @Shared
    BankValidationService mockBankValidationService
    @Shared
    CheckFraudUserServiceConfiguration checkFraudUserServiceConfiguration

    def setup() {
        mockUserServiceClient = Mock()
        mockLocationServiceClient = Mock()
        mockBankValidationService = Mock()
        checkFraudUserServiceConfiguration = new CheckFraudUserServiceConfiguration()

    }

    def "Test create CheckFraudUserServiceImpl with factory method"() {
        when: "calling factory method"
        CheckFraudUserServiceImpl checkFraudUserServiceImpl = CheckFraudUserServiceFactory.getCheckFraudUserService(mockUserServiceClient, mockLocationServiceClient, mockBankValidationService, checkFraudUserServiceConfiguration)

        then: "returned checkFraudUserServiceImpl should be created with methods param"
        checkFraudUserServiceImpl.userServiceClient == mockUserServiceClient
        checkFraudUserServiceImpl.locationServiceClient == mockLocationServiceClient
        checkFraudUserServiceImpl.bankValidationService == mockBankValidationService
        checkFraudUserServiceImpl.checkFraudUserServiceConfiguration == checkFraudUserServiceConfiguration

    }

    def "Test create CheckFraudUserServiceImpl with factory method default"() {
        when: "calling factory method"
        CheckFraudUserServiceImpl checkFraudUserServiceImpl = CheckFraudUserServiceFactory.getDefaultCheckFraudUserService(checkFraudUserServiceConfiguration)

        then: "returned checkFraudUserServiceImpl should be created with methods param"
        checkFraudUserServiceImpl.userServiceClient
        checkFraudUserServiceImpl.locationServiceClient
        checkFraudUserServiceImpl.bankValidationService
        checkFraudUserServiceImpl.checkFraudUserServiceConfiguration == checkFraudUserServiceConfiguration

    }


}
