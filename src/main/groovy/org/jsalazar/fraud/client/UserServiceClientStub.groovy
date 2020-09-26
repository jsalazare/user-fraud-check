package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.Address
import org.jsalazar.fraud.model.PaymentMethod
import org.jsalazar.fraud.model.ReportType
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.model.UserReport

class UserServiceClientStub implements UserServiceClient{

    @Override
    User saveUser(User user) {
        user
    }

    @Override
    User getUserById(Long userId) {
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

    @Override
    List<UserReport> getUserReportByType(Long userId, ReportType type) {
        return [new UserReport(id: 1, userId: 1, reportType: ReportType.FRAUD, reportDescription: "User seems fake")]
    }
}
