package org.jsalazar.fraud.configuration

import groovy.transform.EqualsAndHashCode
import org.jsalazar.fraud.model.ReportType

@EqualsAndHashCode
class CheckFraudUserServiceConfiguration {
    int reportsLimit
    Set<ReportType> fraudReportsTypes
}
