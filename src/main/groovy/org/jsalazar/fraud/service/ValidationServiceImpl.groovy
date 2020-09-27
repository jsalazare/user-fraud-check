package org.jsalazar.fraud.service

import org.jsalazar.fraud.client.BankValidationService
import org.jsalazar.fraud.client.LocationServiceClient
import org.jsalazar.fraud.model.Address
import org.jsalazar.fraud.model.PaymentMethod
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.rules.CustomRule

class ValidationServiceImpl implements ValidationService{
    LocationServiceClient locationServiceClient

    BankValidationService bankValidationService


    @Override
    boolean validatePaymentMethods(List<PaymentMethod> paymentMethods) {
        bankValidationService.validatePaymentMethod(paymentMethods)
    }

    @Override
    boolean validateAddressMatchesIP(Address address, String ip) {
        locationServiceClient.validateAddressMatchesIP(address, ip)
    }

    @Override
    boolean customValidation(CustomRule<User> customRule, User user) {
        customRule.apply(user)
    }
}
