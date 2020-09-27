package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.Address

class LocationServiceClientStub implements LocationServiceClient{


    @Override
    boolean validateAddressMatchesIP(Address address, String ip) {
        true
    }
}
