package org.jsalazar.fraud.service

import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.model.UserReport
import org.jsalazar.fraud.rules.CustomRule

interface CheckFraudUserService {


    User saveUser (User user)

    User getUserById(Long userId)

    void setFraudulentUser(Long userId, boolean isFraud)

    boolean isFraudulentUser(Long userId)

    UserReport addUserReport(UserReport report)

    boolean validateUserAddressMatchesId (Long userId, String ip)

    boolean validateUserPaymentMethods (Long userId)

    boolean validateReportsFraudulentUser(Long userId)

    boolean validateFraudulentUserCustomRule (Long userId, CustomRule<User> customRules, boolean markInBlackList)
}