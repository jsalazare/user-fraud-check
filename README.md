# fraud-user-check

This groovy module enables user to implement fraud detection feature into their application 

Features of this module:
- IP address validation
- User Address validation
- Credit and debit card validation
- Blacklist validation
- User reports validation
- Custom validations

## Installation

Download the jar file in [libs/user-fraud-check.jar](https://github.com/jsalazare/user-fraud-check/raw/master/libs/user-fraud-check.jar)  

Add dependency to project:
```
implementation files('pathToFile/user-fraud-check.jar')
```

## Usage Example
#### ValidationService
Validate payment methods, location or custom fields
##### Example
```
package org.jsalazar.fraud.examples

import org.jsalazar.fraud.model.*
import org.jsalazar.fraud.rules.CustomRule
import org.jsalazar.fraud.service.ValidationService
import org.jsalazar.fraud.service.ValidationServiceImpl

class ValidationServiceExample {
    static void main(String[] args) {
        ValidationService validationService = FraudValidationServiceFactory.getDefaultValidatorService()


        List<PaymentMethod> paymentMethods = [new PaymentMethod(cardNumber: "1234123412341234", expiration: "01/01", paymentType: PaymentType.CREDIT)]
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

```

#### CheckFraudUserService
From a given and saved user validates payment methods, api matches address, report based fraud detection and black list
##### Example

```
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
        user.paymentMethods = [new PaymentMethod(cardNumber: "1234123412341234", expiration: "01/01", paymentType: PaymentType.CREDIT)]


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

```