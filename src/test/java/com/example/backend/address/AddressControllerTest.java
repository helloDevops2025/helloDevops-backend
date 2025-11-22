package com.example.backend.address;

import com.example.backend.address.dto.AddressRequest;
import com.example.backend.address.dto.AddressResponse;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AddressService (20 test cases)
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        setId(user1, 1L);

        user2 = new User();
        setId(user2, 2L);
    }

    // ===== Helper ใช้ reflection set id =====
    private static void setId(Object target, Long id) {
        try {
            Field f = target.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(target, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id via reflection", e);
        }
    }

    private Address createAddress(User user, Long id, AddressStatus status) {
        Address a = new Address();
        a.setUser(user);
        a.setName("Name " + id);
        a.setPhoneNumber("080000000" + id);
        a.setAddress("Address " + id);
        a.setStreet("Street " + id);
        a.setSubdistrict("Sub " + id);
        a.setDistrict("Dist " + id);
        a.setProvince("Prov " + id);
        a.setZipcode("1000" + id);
        a.setStatus(status);
        if (id != null) {
            setId(a, id);
        }
        return a;
    }

    private AddressRequest createRequest(AddressStatus status) {
        AddressRequest r = new AddressRequest();
        r.setName("New Name");
        r.setPhoneNumber("0899999999");
        r.setAddress("New Address");
        r.setStreet("New Street");
        r.setSubdistrict("New Sub");
        r.setDistrict("New Dist");
        r.setProvince("New Prov");
        r.setZipcode("99999");
        r.setStatus(status);
        return r;
    }

    // ========== 1. getAddressesByUser: ไม่มีที่อยู่ ==========
    @Test
    void getAddressesByUser_returnsEmptyList_whenNoAddresses() {
        when(addressRepository.findByUserId(1L))
                .thenReturn(Collections.emptyList());

        List<AddressResponse> result = addressService.getAddressesByUser(1L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(addressRepository, times(1)).findByUserId(1L);
    }

    // ========== 2. getAddressesByUser: มีหลาย address ==========
    @Test
    void getAddressesByUser_returnsMappedList_whenAddressesExist() {
        Address a1 = createAddress(user1, 10L, AddressStatus.NON_DEFAULT);
        Address a2 = createAddress(user1, 11L, AddressStatus.DEFAULT);

        when(addressRepository.findByUserId(1L))
                .thenReturn(Arrays.asList(a1, a2));

        List<AddressResponse> result = addressService.getAddressesByUser(1L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(11L, result.get(1).getId());
        assertEquals(AddressStatus.DEFAULT, result.get(1).getStatus());
    }

    // ========== 3. addAddress: user ไม่เจอ ==========
    @Test
    void addAddress_throwsException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        AddressRequest req = createRequest(AddressStatus.NON_DEFAULT);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> addressService.addAddress(99L, req));

        assertEquals("User not found", ex.getMessage());
    }

    // ========== 5. addAddress: ตั้ง DEFAULT เมื่อยังไม่มี default เดิม ==========
    @Test
    void addAddress_default_whenNoOldDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(addressRepository.findByUserIdAndStatus(1L, AddressStatus.DEFAULT))
                .thenReturn(Optional.empty());

        AddressRequest req = createRequest(AddressStatus.DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.addAddress(1L, req);

        assertEquals(AddressStatus.DEFAULT, res.getStatus());
        verify(addressRepository).findByUserIdAndStatus(1L, AddressStatus.DEFAULT);
    }

    // ========== 6. addAddress: ตั้ง DEFAULT แล้ว reset default เดิม ==========
    @Test
    void addAddress_default_resetsOldDefaultToNonDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        Address oldDefault = createAddress(user1, 100L, AddressStatus.DEFAULT);
        when(addressRepository.findByUserIdAndStatus(1L, AddressStatus.DEFAULT))
                .thenReturn(Optional.of(oldDefault));

        AddressRequest req = createRequest(AddressStatus.DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.addAddress(1L, req);

        assertEquals(AddressStatus.DEFAULT, res.getStatus());
        // old default ต้องถูกเซฟกลับด้วยสถานะ NON_DEFAULT
        assertEquals(AddressStatus.NON_DEFAULT, oldDefault.getStatus());
        verify(addressRepository, times(2)).save(any(Address.class));
    }

    // ========== 7. updateAddress: address ไม่เจอ ==========
    @Test
    void updateAddress_throwsException_whenAddressNotFound() {
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        AddressRequest req = createRequest(AddressStatus.NON_DEFAULT);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> addressService.updateAddress(999L, req));

        assertEquals("Address not found", ex.getMessage());
    }

    // ========== 8. updateAddress: อัปเดต field ทั่วไป ==========
    @Test
    void updateAddress_updatesBasicFieldsCorrectly() {
        Address existing = createAddress(user1, 10L, AddressStatus.NON_DEFAULT);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));

        AddressRequest req = createRequest(AddressStatus.NON_DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        assertEquals("New Name", res.getName());
        assertEquals("New Address", res.getAddress());
        assertEquals("New Prov", res.getProvince());
        assertEquals(AddressStatus.NON_DEFAULT, res.getStatus());
    }

    // ========== 9. updateAddress: status ใน request เป็น null → ไม่เปลี่ยน status ==========
    @Test
    void updateAddress_keepsStatus_whenRequestStatusIsNull() {
        Address existing = createAddress(user1, 10L, AddressStatus.DEFAULT);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));

        AddressRequest req = createRequest(null); // status = null

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        assertEquals(AddressStatus.DEFAULT, res.getStatus());
    }

    // ========== 10. updateAddress: ตั้ง DEFAULT แล้ว reset default เดิม ==========
    @Test
    void updateAddress_setDefault_resetsOldDefaultOfSameUser() {
        Address existing = createAddress(user1, 10L, AddressStatus.NON_DEFAULT);
        Address oldDefault = createAddress(user1, 20L, AddressStatus.DEFAULT);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(addressRepository.findByUserIdAndStatus(1L, AddressStatus.DEFAULT))
                .thenReturn(Optional.of(oldDefault));

        AddressRequest req = createRequest(AddressStatus.DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        assertEquals(AddressStatus.DEFAULT, res.getStatus());
        assertEquals(AddressStatus.NON_DEFAULT, oldDefault.getStatus());
        verify(addressRepository, times(2)).save(any(Address.class));
    }

    // ========== 11. updateAddress: ตั้ง DEFAULT แต่ไม่มี default เดิม ==========
    @Test
    void updateAddress_setDefault_whenNoOldDefault() {
        Address existing = createAddress(user1, 10L, AddressStatus.NON_DEFAULT);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(addressRepository.findByUserIdAndStatus(1L, AddressStatus.DEFAULT))
                .thenReturn(Optional.empty());

        AddressRequest req = createRequest(AddressStatus.DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        assertEquals(AddressStatus.DEFAULT, res.getStatus());
        verify(addressRepository, times(1))
                .findByUserIdAndStatus(1L, AddressStatus.DEFAULT);
    }

    // ========== 12. updateAddress: เป็น default อยู่แล้ว → ไม่ reset ตัวเอง ==========
    @Test
    void updateAddress_setDefault_doesNotResetSameAddressAsOldDefault() {
        Address existing = createAddress(user1, 10L, AddressStatus.DEFAULT);
        Address oldDefault = existing; // จำลองว่า default เดิมคือ address เดียวกัน

        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(addressRepository.findByUserIdAndStatus(1L, AddressStatus.DEFAULT))
                .thenReturn(Optional.of(oldDefault));

        AddressRequest req = createRequest(AddressStatus.DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        // status ยังเป็น DEFAULT เหมือนเดิม
        assertEquals(AddressStatus.DEFAULT, res.getStatus());
        // ไม่ควรถูก set เป็น NON_DEFAULT
        assertEquals(AddressStatus.DEFAULT, oldDefault.getStatus());
    }

    // ========== 13. deleteAddress: เรียก repository ถูกต้อง ==========
    @Test
    void deleteAddress_delegatesToRepository() {
        addressService.deleteAddress(10L);
        verify(addressRepository, times(1)).deleteById(10L);
    }

    // ========== 14. addAddress: ค่า status default ใน request เป็น NON_DEFAULT ==========
    @Test
    void addAddress_defaultStatusInRequestIsNonDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        AddressRequest req = new AddressRequest(); // ไม่ set status → default NON_DEFAULT

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.addAddress(1L, req);

        assertEquals(AddressStatus.NON_DEFAULT, res.getStatus());
    }

    // ========== 15. addAddress: สามารถมีหลาย NON_DEFAULT ใน user เดียวกัน ==========
    @Test
    void addAddress_allowsMultipleNonDefaultForSameUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        AddressRequest req1 = createRequest(AddressStatus.NON_DEFAULT);
        AddressRequest req2 = createRequest(AddressStatus.NON_DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse r1 = addressService.addAddress(1L, req1);
        AddressResponse r2 = addressService.addAddress(1L, req2);

        assertEquals(AddressStatus.NON_DEFAULT, r1.getStatus());
        assertEquals(AddressStatus.NON_DEFAULT, r2.getStatus());
    }

    // ========== 16. getAddressesByUser: เรียก repository ด้วย userId ถูกต้อง ==========
    @Test
    void getAddressesByUser_callsRepositoryWithCorrectUserId() {
        when(addressRepository.findByUserId(2L))
                .thenReturn(Collections.emptyList());

        addressService.getAddressesByUser(2L);

        verify(addressRepository, times(1)).findByUserId(2L);
    }

    // ========== 17. addAddress: ตรวจว่า user ถูก set ให้ address ==========
    @Test
    void addAddress_setsUserOnNewAddress() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        AddressRequest req = createRequest(AddressStatus.NON_DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.addAddress(1L, req);

        assertNotNull(res);
        // ตรวจจาก argument ว่า user ถูก set
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, atLeastOnce()).save(captor.capture());
        Address saved = captor.getValue();
        assertNotNull(saved.getUser());
        assertEquals(1L, saved.getUser().getId());
    }

    // ========== 18. updateAddress: เปลี่ยนจาก DEFAULT → NON_DEFAULT ==========
    @Test
    void updateAddress_changeDefaultToNonDefault() {
        Address existing = createAddress(user1, 10L, AddressStatus.DEFAULT);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));

        AddressRequest req = createRequest(AddressStatus.NON_DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        assertEquals(AddressStatus.NON_DEFAULT, res.getStatus());
    }

    // ========== 19. updateAddress: เปลี่ยนข้อมูลแต่ไม่แตะ status ==========
    @Test
    void updateAddress_updatesFieldsWithoutChangingStatusWhenStatusNull() {
        Address existing = createAddress(user1, 10L, AddressStatus.NON_DEFAULT);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));

        AddressRequest req = new AddressRequest();
        req.setName("Updated Name");
        req.setAddress("Updated Address");
        req.setStatus(null); // ไม่แตะ status

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse res = addressService.updateAddress(10L, req);

        assertEquals("Updated Name", res.getName());
        assertEquals("Updated Address", res.getAddress());
        assertEquals(AddressStatus.NON_DEFAULT, res.getStatus());
    }

    // ========== 20. addAddress: ตรวจ mapping Response ว่าข้อมูลถูกต้อง ==========
    @Test
    void addAddress_returnsResponseMappedFromEntity() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        AddressRequest req = new AddressRequest();
        req.setName("Ploy");
        req.setPhoneNumber("0812345678");
        req.setAddress("123/45");
        req.setStreet("Main");
        req.setSubdistrict("Sub");
        req.setDistrict("Dist");
        req.setProvince("BKK");
        req.setZipcode("10110");
        req.setStatus(AddressStatus.DEFAULT);

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> {
                    Address a = invocation.getArgument(0);
                    // จำลองว่าฐานข้อมูลสร้าง id ให้
                    setId(a, 999L);
                    return a;
                });

        AddressResponse res = addressService.addAddress(1L, req);

        assertEquals(999L, res.getId());
        assertEquals("Ploy", res.getName());
        assertEquals("0812345678", res.getPhoneNumber());
        assertEquals("BKK", res.getProvince());
        assertEquals(AddressStatus.DEFAULT, res.getStatus());
    }
}
