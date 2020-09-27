package org.jsalazar.fraud

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.model.Address
import org.jsalazar.fraud.model.PaymentMethod
import org.jsalazar.fraud.model.PaymentType
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.rules.CustomRule
import org.jsalazar.fraud.service.ValidationServiceImpl
import spock.lang.Shared
import spock.lang.Specification

class ValidatorServiceImplSpec extends Specification {

    @Shared
    LocationServiceClient mockLocationServiceClient
    @Shared
    BankValidationService mockBankValidationService
    @Shared
    ValidationServiceImpl validationServiceImpl

    def setup() {
        mockLocationServiceClient = Mock()
        mockBankValidationService = Mock()
        validationServiceImpl = new ValidationServiceImpl(
                locationServiceClient: mockLocationServiceClient,
                bankValidationService: mockBankValidationService)

    }


    def "Test payment method"() {
        given: "a payment method"
        List<PaymentMethod> paymentMethods = [new PaymentMethod(cardNumber: "1234123412342134", expiration: "01/01", paymentType: PaymentType.CREDIT)]

        when: "validate payment methods"
        validationServiceImpl.validatePaymentMethods(paymentMethods)

        then: "mockBankValidationService.validatePaymentMethod should be called once"
        1 * mockBankValidationService.validatePaymentMethod(paymentMethods)
    }

    def "Test address matches ip"() {
        given: "an Address and ip"
        Address address = new Address()
        String ip = "255-255-255-255"

        when: "validate address matches ip"
        validationServiceImpl.validateAddressMatchesIP(address, ip)

        then: "mockLocationServiceClient.validateAddressMatchesIP should be called once"
        1 * mockLocationServiceClient.validateAddressMatchesIP(address, ip)
    }


    def "Test custom rule phone validation"() {
        given: "a custom rule for phone validation"
        CustomRule<User> customRule = { user -> user.phone ==~ /(\d{3})-(\d{3})-(\d{4})/ }
        User user = new User(phone: "123-123-1234")

        when: "custom validation execution"
        boolean actualResult = validationServiceImpl.customValidation(customRule, user)

        then: "result should be true"
        actualResult
    }
}
