package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.PaymentMethod

class BankValidationServiceStub implements BankValidationService{

    @Override
    boolean validatePaymentMethod(List<PaymentMethod> paymentMethods) {
        true
    }
}
