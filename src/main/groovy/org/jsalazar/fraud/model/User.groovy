package org.jsalazar.fraud.model

class User {
    Long id
    String phone
    String email
    boolean isFraud
    List<UserReport> userReports
    Address address
    List<PaymentMethod> paymentMethods
}
