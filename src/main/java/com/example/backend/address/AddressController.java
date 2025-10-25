package com.example.backend.address;

import com.example.backend.address.dto.AddressRequest;
import com.example.backend.address.dto.AddressResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
//@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // Get Address
    @GetMapping("/{userId}")
    public List<AddressResponse> getAddresses(@PathVariable Long userId) {
        return addressService.getAddressesByUser(userId);
    }

    // Add Address
    @PostMapping("/{userId}")
    public AddressResponse addAddress(@PathVariable Long userId, @RequestBody AddressRequest request) {
        return addressService.addAddress(userId, request);
    }

    // Update Address
    @PutMapping("/{id}")
    public AddressResponse updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        return addressService.updateAddress(id, request);
    }

    // Delete Address
    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }
}
