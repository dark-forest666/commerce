package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUserOrderByIsDefaultDescCreateTimeDesc(user);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    public Address getDefaultAddress(User user) {
        return addressRepository.findByUserAndIsDefaultTrue(user).orElse(null);
    }

    @Transactional
    public void addAddress(Address address) {
        // 如果当前用户还没有任何地址，则自动设为默认
        if (addressRepository.findByUserOrderByIsDefaultDescCreateTimeDesc(address.getUser()).isEmpty()) {
            address.setIsDefault(true);
        } else if (address.getIsDefault()) {
            // 如果新增的地址设为默认，则取消其他地址的默认状态
            cancelOtherDefault(address.getUser());
        }
        addressRepository.save(address);
    }

    @Transactional
    public void updateAddress(Address address) {
        if (address.getIsDefault()) {
            cancelOtherDefault(address.getUser());
        }
        addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
        // 如果删除的是默认地址，将最新的一条地址设为默认（可选）
        // 这里简单处理，不自动设置，让用户手动设置
    }

    @Transactional
    public void setDefaultAddress(Long addressId, User user) {
        cancelOtherDefault(user);
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("地址不存在"));
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private void cancelOtherDefault(User user) {
        Address defaultAddr = getDefaultAddress(user);
        if (defaultAddr != null) {
            defaultAddr.setIsDefault(false);
            addressRepository.save(defaultAddr);
        }
    }
}