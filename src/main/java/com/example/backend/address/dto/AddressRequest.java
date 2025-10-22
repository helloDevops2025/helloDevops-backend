package com.example.backend.address.dto;

import com.example.backend.address.AddressStatus;

public class AddressRequest {
    private String name;
    private String phoneNumber;
    private String address;
    private String street;
    private String subdistrict;
    private String district;
    private String province;
    private String zipcode;
    private AddressStatus status = AddressStatus.NON_DEFAULT;

    // Getters/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getSubdistrict() { return subdistrict; }
    public void setSubdistrict(String subdistrict) { this.subdistrict = subdistrict; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    public AddressStatus getStatus() { return status; }
    public void setStatus(AddressStatus status) { this.status = status; }
}
