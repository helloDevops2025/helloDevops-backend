package com.example.backend.address;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.address.dto.AddressRequest;
import com.example.backend.address.dto.AddressResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressResponse> getAddressesByUser(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AddressResponse addAddress(Long userId, AddressRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ถ้าต้องการ set เป็น default ให้ reset ของเก่าก่อน
        if (req.getStatus() == AddressStatus.DEFAULT) {
            addressRepository.findByUserIdAndStatus(userId, AddressStatus.DEFAULT)
                    .ifPresent(oldDefault -> {
                        oldDefault.setStatus(AddressStatus.NON_DEFAULT);
                        addressRepository.save(oldDefault);
                    });
        }

        Address address = new Address();
        address.setUser(user);
        address.setName(req.getName());
        address.setPhoneNumber(req.getPhoneNumber());
        address.setAddress(req.getAddress());
        address.setStreet(req.getStreet());
        address.setSubdistrict(req.getSubdistrict());
        address.setDistrict(req.getDistrict());
        address.setProvince(req.getProvince());
        address.setZipcode(req.getZipcode());
        address.setStatus(req.getStatus());

        addressRepository.save(address);
        return AddressResponse.fromEntity(address);
    }

    public AddressResponse updateAddress(Long id, AddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setName(request.getName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddress(request.getAddress());
        address.setStreet(request.getStreet());
        address.setSubdistrict(request.getSubdistrict());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setZipcode(request.getZipcode());
        // ถ้า request มี status ก็อัปเดตด้วย
        if (request.getStatus() != null) {
            address.setStatus(request.getStatus());
        }

        return AddressResponse.fromEntity(addressRepository.save(address));
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
