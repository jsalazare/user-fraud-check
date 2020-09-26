package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.Address

interface LocationServiceClient {

    boolean validateAddressMatchesIP(String ip, Address address)
}