package org.jsalazar.fraud.examples


import org.jsalazar.fraud.configuration.CheckFraudUserServiceConfiguration
import org.jsalazar.fraud.model.*
import org.jsalazar.fraud.rules.CustomRule
import org.jsalazar.fraud.service.CheckFraudUserService
import org.jsalazar.fraud.service.FraudValidationServiceFactory

class CheckFraudUserServiceExample {

     static void main(String[] args) {
        CheckFraudUserServiceConfiguration configurations = new CheckFraudUserServiceConfiguration()
        configurations.reportsLimit = 1
        configurations.fraudReportsTypes = [ReportType.FRAUD]
        CheckFraudUserService checkFraudUserService = FraudValidationServiceFactory.getDefaultCheckFraudUserService(configurations)


        User user = new User()

        user.id = 1
        user.phone = "555-555-5555"
        user.email = "email@email.com"
        user.isFraud = false
        user.address = new Address(street: "street1", city: "city1",state: "state1", country: "country1")
        user.paymentMethods = [new PaymentMethod(cardNumber: "1234123412341234", expiration: "01/01", PaymentType.CREDIT)]


        //Store user
        checkFraudUserService.saveUser(user)

        //Validates user payment methods
        checkFraudUserService.validateUserPaymentMethods(user.id)

        //validate if the ip address matches with the registered user address
        checkFraudUserService.validateUserAddressMatchesId(user.id, "123-123-123-123")


        UserReport report = new UserReport(id: 1, userId: 1, reportType: ReportType.FRAUD, reportDescription: "is fraudulent")
        //Add a report to one user
        checkFraudUserService.addUserReport(report)

        //Validates if the given user has equals or more than CheckFraudUserServiceConfiguration.reportsLimit reports
        // from the given CheckFraudUserServiceConfiguration.fraudReportsTypes
        checkFraudUserService.validateReportsFraudulentUser(user.id)


        CustomRule<User> customRule = { customRuleUser -> customRuleUser.phone ==~ /(\d{3})-(\d{3})-(\d{4})/}
        //Validates using custom rule.
        checkFraudUserService.validateFraudulentUserCustomRule(1, customRule, true)
    }
}
