package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.ReportType
import org.jsalazar.fraud.model.User
import org.jsalazar.fraud.model.UserReport

interface UserServiceClient {

    User saveUser(User user)

    User getUserById(Long userId)

    List<UserReport> getUserReportByType(Long userId, ReportType type)
}