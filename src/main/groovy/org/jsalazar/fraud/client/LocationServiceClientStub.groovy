package org.jsalazar.fraud.client

import org.jsalazar.fraud.model.Address

class LocationServiceClientStub implements LocationServiceClient{


    @Override
    boolean validateAddressMatchesIP(String ip, Address address) {
        true
    }
}
