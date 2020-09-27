package org.jsalazar.fraud.examples

import org.jsalazar.fraud.model.*
import org.jsalazar.fraud.rules.CustomRule
import org.jsalazar.fraud.service.ValidationService
import org.jsalazar.fraud.service.ValidationServiceImpl

class ValidationServiceExample {
    static void main(String[] args) {
        ValidationService validationService = new ValidationServiceImpl()


        List<PaymentMethod> paymentMethods = [new PaymentMethod(cardNumber: "1234123412341234", expiration: "01/01", PaymentType.CREDIT)]
        //validates payment methods
        validationService.validatePaymentMethods(paymentMethods)


        Address address = new Address(street: "street1", city: "city1",state: "state1", country: "country1")
        String ip = "123-123-123-123"


        //validate if the ip address matches with the given address
        validationService.validateAddressMatchesIP(address, ip)


        CustomRule<User> customRule = { customRuleUser -> customRuleUser.phone ==~ /(\d{3})-(\d{3})-(\d{4})/}
        User user = new User(phone: "555-555-5555")
        //Validates using custom rule.
        validationService.customValidation(customRule, user)
    }
}
