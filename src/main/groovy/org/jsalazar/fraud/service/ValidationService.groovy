package org.jsalazar.fraud.service

import org.jsalazar.fraud.model.Address
import org.jsalazar.fraud.model.PaymentMethod
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.rules.CustomRule

interface ValidationService {
    boolean validatePaymentMethods(List<PaymentMethod> paymentMethods)

    boolean validateAddressMatchesIP(Address address, String ip)

    boolean customValidation(CustomRule<User> customRule, User user)
}