package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.PaymentMethod

interface BankValidationService {

    boolean validatePaymentMethod (List<PaymentMethod> paymentMethods)

}