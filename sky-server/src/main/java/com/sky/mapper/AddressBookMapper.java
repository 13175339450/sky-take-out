package com.sky.mapper;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookMapper {

    /**
     * 查询当前用户的所有地址
     * @param addressBook
     * @return
     */
    List<AddressBook> showAddressBook(AddressBook addressBook);

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    int insertAddress(AddressBook addressBook);

    /**
     * 清空所有地址的默认属性
     * @param userId
     */
    void cleanDefault(Long userId);

    /**
     * 设置具体地址为默认属性
     * @param id
     * @param id
     */
    void setDefaultAddress(Long id);

    /**
     * 查询默认地址
     * @param userId
     * @return
     */
    AddressBook showDefaultAddress(Long userId);

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    int deleteAddressById(Long id);

    /**
     * 获取修改前地址的默认状态
     * @param id
     * @return
     */
    Integer getDefaultStatus(Long id);
}
