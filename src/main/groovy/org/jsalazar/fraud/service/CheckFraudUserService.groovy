package org.jsalazar.fraud.service

import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.rules.CustomRule

interface CheckFraudUserService {


    User getUserById(Long userId)

    void setFraudulentUser(Long userId, boolean isFraud)

    boolean isFraudulentUser(Long userId)

    boolean validateUserLocation (Long userId, String ip)

    boolean validateUserPaymentMethods (Long userId)

    boolean validateReportsFraudulentUser(Long userId)

    boolean validateFraudulentUserCustomRule (Long userId, CustomRule<User> customRules, boolean markInBlackList)
}