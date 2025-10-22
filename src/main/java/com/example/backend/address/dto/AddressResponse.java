package com.example.backend.address.dto;

import com.example.backend.address.Address;
import com.example.backend.address.AddressStatus;

public class AddressResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    private String street;
    private String subdistrict;
    private String district;
    private String province;
    private String zipcode;
    private AddressStatus status;

    public static AddressResponse fromEntity(Address a) {
        AddressResponse res = new AddressResponse();
        res.id = a.getId();
        res.name = a.getName();
        res.phoneNumber = a.getPhoneNumber();
        res.address = a.getAddress();
        res.street = a.getStreet();
        res.subdistrict = a.getSubdistrict();
        res.district = a.getDistrict();
        res.province = a.getProvince();
        res.zipcode = a.getZipcode();
        res.status = a.getStatus();
        return res;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getStreet() { return street; }
    public String getSubdistrict() { return subdistrict; }
    public String getDistrict() { return district; }
    public String getProvince() { return province; }
    public String getZipcode() { return zipcode; }
    public AddressStatus getStatus() { return status; }
}
