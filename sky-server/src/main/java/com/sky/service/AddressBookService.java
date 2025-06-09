package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 查询当前用户的所有地址
     * @return
     */
    List<AddressBook> showAddressBook();

    /**
     * 新增地址
     * @param addressBook
     */
    void insertAddress(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefaultAddress(AddressBook addressBook);

    /**
     * 查询默认地址
     * @return
     */
    AddressBook showDefaultAddress();

    /**
     * 根据id（地址id）查询地址 （修改数据时的数据回显）
     * @param id
     * @return
     */
    AddressBook queryAddressById(Long id);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteAddressById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void updateAddressById(AddressBook addressBook);
}
