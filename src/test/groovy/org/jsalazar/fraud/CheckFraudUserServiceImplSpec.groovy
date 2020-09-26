package org.jsalazar.fraud

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.client.UserServiceClient
import org.jsalazar.fraud.configuration.CheckFraudUserServiceConfiguration
import org.jsalazar.fraud.model.Address
import org.jsalazar.fraud.model.PaymentMethod
import org.jsalazar.fraud.model.ReportType
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.model.UserReport
import org.jsalazar.fraud.service.CheckFraudUserServiceImpl
import spock.lang.Shared
import spock.lang.Specification

class CheckFraudUserServiceImplSpec extends Specification{

    @Shared
    UserServiceClient mockUserServiceClient
    @Shared
    LocationServiceClient mockLocationServiceClient
    @Shared
    BankValidationService mockBankValidationService
    @Shared
    CheckFraudUserServiceConfiguration checkFraudUserServiceConfiguration
    @Shared
    CheckFraudUserServiceImpl checkFraudUserService

    def setup() {
        mockUserServiceClient = Mock()
        mockLocationServiceClient = Mock()
        mockBankValidationService = Mock()
        checkFraudUserServiceConfiguration = new CheckFraudUserServiceConfiguration(reportsLimit: 3, fraudReportsTypes: [ReportType.FRAUD])
        checkFraudUserService = new CheckFraudUserServiceImpl(
                userServiceClient: mockUserServiceClient,
                locationServiceClient: mockLocationServiceClient,
                bankValidationService: mockBankValidationService,
                checkFraudUserServiceConfiguration: checkFraudUserServiceConfiguration)
    }



    def "Test validate reports fraudulent user true"() {

        given: "a expected user and fraud user service configurations"
        User expectedUser = buildUser()
        checkFraudUserService.checkFraudUserServiceConfiguration.reportsLimit = 1
        checkFraudUserService.checkFraudUserServiceConfiguration.fraudReportsTypes = [ReportType.FRAUD]

        when: "call to checkFraudUserService.validateReportsFraudulentUser"
        boolean actualResponse = checkFraudUserService.validateReportsFraudulentUser(expectedUser.id)

        then: "validate expected calls and result should be true"
        2 * mockUserServiceClient.getUserById(expectedUser.id) >> expectedUser
        1 * mockUserServiceClient.getUserReportByType(expectedUser.id, checkFraudUserServiceConfiguration.fraudReportsTypes[0]) >> expectedUser.userReports
        1 * mockUserServiceClient.saveUser(expectedUser)

        actualResponse
    }

    def "Test validate reports fraudulent user false"() {

        given: "a expected user and fraud user service configurations"
        User expectedUser = buildUser()
        checkFraudUserService.checkFraudUserServiceConfiguration.reportsLimit = 3
        checkFraudUserService.checkFraudUserServiceConfiguration.fraudReportsTypes = [ReportType.FRAUD]

        when: "call to checkFraudUserService.validateReportsFraudulentUser"
        boolean actualResponse = checkFraudUserService.validateReportsFraudulentUser(expectedUser.id)

        then: "validate expected calls and result should be false"
        1 * mockUserServiceClient.getUserById(expectedUser.id) >> expectedUser
        1 * mockUserServiceClient.getUserReportByType(expectedUser.id, checkFraudUserServiceConfiguration.fraudReportsTypes[0]) >> expectedUser.userReports

        !actualResponse
    }


    def "Test validate multiple reports and reports types"() {

        given: "a expected user and fraud user service configurations"
        User expectedUser = buildUser()
        expectedUser.userReports.add(new UserReport(id: 2, userId: 1, reportType: ReportType.FAKE, reportDescription: "User seems fake"))
        expectedUser.userReports.add(new UserReport(id: 3, userId: 1, reportType: ReportType.FAKE, reportDescription: "User seems fake"))
        checkFraudUserService.checkFraudUserServiceConfiguration.reportsLimit = 2
        checkFraudUserService.checkFraudUserServiceConfiguration.fraudReportsTypes = [ReportType.FRAUD, ReportType.FAKE]

        when: "call to checkFraudUserService.validateReportsFraudulentUser"
        boolean actualResponse = checkFraudUserService.validateReportsFraudulentUser(expectedUser.id)

        then: "validate expected calls and result should be true"
        2 * mockUserServiceClient.getUserById(expectedUser.id) >> expectedUser
        2 * mockUserServiceClient.getUserReportByType(expectedUser.id, _) >> expectedUser.userReports
        1 * mockUserServiceClient.saveUser(expectedUser)

        actualResponse
    }



    def buildUser (){
        new User(
                id: 1,
                phone: "5555555555",
                email: "email.email.com",
                isFraud: false,
                userReports: [new UserReport(id: 1, userId: 1, reportType: ReportType.FRAUD, reportDescription: "User seems fake")],
                address: new Address(street: "street1", city: "city1", state: "state1", country: "country1"),
                paymentMethods: [new PaymentMethod(cardNumber: "1234123412341234", expiration: "01/01")]
        )
    }

}
