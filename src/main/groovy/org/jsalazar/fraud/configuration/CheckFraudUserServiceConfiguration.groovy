package org.jsalazar.fraud.configuration

import org.jsalazar.fraud.model.ReportType

class CheckFraudUserServiceConfiguration {
    int reportsLimit
    List<ReportType> fraudReportsTypes
}
