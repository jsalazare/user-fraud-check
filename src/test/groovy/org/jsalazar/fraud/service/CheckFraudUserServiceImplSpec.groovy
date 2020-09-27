package org.jsalazar.fraud.service

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.client.UserServiceClient
import org.jsalazar.fraud.configuration.CheckFraudUserServiceConfiguration
import org.jsalazar.fraud.exception.UserNotFoundException
import org.jsalazar.fraud.model.Address
import org.jsalazar.fraud.model.PaymentMethod
import org.jsalazar.fraud.model.ReportType
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.model.UserReport
import org.jsalazar.fraud.rules.CustomRule
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

    def "Test getting user by id"() {

        given: "a expected user"
        User expectedUser = buildUser()

        when: "call to checkFraudUserService.getUserById"
        User actualUser = checkFraudUserService.getUserById(1)

        then: "validate user return expected values and mockUserServiceClient.getUserById should be called once"
        1 * mockUserServiceClient.getUserById(1) >> expectedUser

        actualUser.id == expectedUser.id
        actualUser.phone == expectedUser.phone
        actualUser.email == expectedUser.email
        actualUser.isFraud == expectedUser.isFraud
        actualUser.userReports[0].id == expectedUser.userReports[0].id
        actualUser.userReports[0].userId == expectedUser.userReports[0].userId
        actualUser.userReports[0].reportType == expectedUser.userReports[0].reportType
        actualUser.userReports[0].reportDescription == expectedUser.userReports[0].reportDescription
        actualUser.address.street == expectedUser.address.street
        actualUser.address.city == expectedUser.address.city
        actualUser.address.state == expectedUser.address.state
        actualUser.address.country == expectedUser.address.country
        actualUser.paymentMethods[0].cardNumber == expectedUser.paymentMethods[0].cardNumber
        actualUser.paymentMethods[0].expiration == expectedUser.paymentMethods[0].expiration
    }

    def "Test save user"() {
        given: "a user"
        User expectedUser = buildUser()

        when: "saving the user"
        checkFraudUserService.saveUser(expectedUser)

        then: "user service save method should be called once"
        1 * mockUserServiceClient.saveUser(expectedUser)

    }


    def "Test getting user by id return null user"() {
        when: "getting user by id"
        checkFraudUserService.getUserById(1)

        then: "the service throws UserNotFoundException"
        1 * mockUserServiceClient.getUserById(1)
        thrown UserNotFoundException

    }

    def "Test setting fraudulent user"() {

        given: "a expected user"
        User expectedUser = buildUser()

        when: "set fraudulent user as false"
        checkFraudUserService.setFraudulentUser(1, false)

        then: "expected calls should be trigger and user should set isFraud value to false"
        1 * mockUserServiceClient.getUserById(1) >> expectedUser
        1 * mockUserServiceClient.saveUser(expectedUser)
        !expectedUser.isFraud

    }


    def "Test is fraudulent user"() {
        given: "a expected user"
        User expectedUser = buildUser()

        when: "call to checkFraudUserService.isFraudulentUser"
        boolean actualResult = checkFraudUserService.isFraudulentUser(expectedUser.id)

        then: "expected calls should be trigger and should return the same isFraud value"
        1 * mockUserServiceClient.getUserById(1) >> expectedUser

        expectedUser.isFraud == actualResult

    }

    def "Test validate user location matches ip"() {
        given: "a expected user and ip"
        User expectedUser = buildUser()
        String ip = "123-123-123-123"

        when: "validates user location"
        checkFraudUserService.validateUserAddressMatchesId(1, ip)

        then: "expected calls should be trigger"
        1 * mockUserServiceClient.getUserById(1) >> expectedUser
        1 * mockLocationServiceClient.validateAddressMatchesIP(expectedUser.address, ip)
    }

    def "Test validate user payment methods"() {
        given: "a expected user"
        User expectedUser = buildUser()

        when: "validates payment methods"
        checkFraudUserService.validateUserPaymentMethods(1)

        then: "expected calls should be trigger"
        1 * mockUserServiceClient.getUserById(1) >> expectedUser
        1 * mockBankValidationService.validatePaymentMethod(expectedUser.paymentMethods)
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


    def "Test validate fraudulent user custom rules phone validation add to black list true"() {

        given: "a expected user and a custom rule"
        User expectedUser = buildUser()
        expectedUser.phone = "111-111-1111"
        CustomRule<User> customRule = {user -> user.phone ==~ /(\d{3})-(\d{3})-(\d{4})/}

        when: "validate fraud user with custom rules"
        boolean  actualResult = checkFraudUserService.validateFraudulentUserCustomRule(expectedUser.id, customRule, true)

        then: "validation should pass and expected calls"
        2 * mockUserServiceClient.getUserById(expectedUser.id) >> expectedUser
        1 * mockUserServiceClient.saveUser(expectedUser)
        actualResult

    }

    def "Test validate fraudulent user custom rules phone validation add to black list false"() {

        given: "a expected user with invalid phone and a custom rule"
        User expectedUser = buildUser()
        expectedUser.phone = "111-1-111111"
        CustomRule<User> customRule = {user -> user.phone ==~ /(\d{3})-(\d{3})-(\d{4})/}

        when: "validate fraud user with custom rules"
        boolean  actualResult = checkFraudUserService.validateFraudulentUserCustomRule(expectedUser.id, customRule, false)

        then: "validation should not pass and expected calls"
        1 * mockUserServiceClient.getUserById(expectedUser.id) >> expectedUser
        !actualResult

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
