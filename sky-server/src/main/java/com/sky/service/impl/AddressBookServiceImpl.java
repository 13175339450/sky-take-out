package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询当前用户的所有地址
     *
     * @return
     */
    @Override
    public List<AddressBook> showAddressBook() {
        //1.实体注入 调用动态sql查询方法
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());

        //2.调用动态查询方法
        List<AddressBook> addressBookList = addressBookMapper.showAddressBook(addressBook);

        //3.结果返回
        return addressBookList;
    }

    /**
     * TODO: 新增地址 当新增第一条数据时，默认为默认地址
     * @param addressBook
     */
    @Override
    public void insertAddress(AddressBook addressBook) {
        //1.赋值userId
        addressBook.setUserId(BaseContext.getCurrentId());

        //2.设置为非默认地址
        addressBook.setIsDefault(0);

        //3.新增数据
        int row = addressBookMapper.insertAddress(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        //获取当前用户的userId
        Long userId = BaseContext.getCurrentId();

        //1.先将该用户的所有地址改为非默认 (根据userId)
        addressBookMapper.cleanDefault(userId);

        //2.再指定某个地址为默认地址 只需要地址id即可 （唯一）
        addressBookMapper.setDefaultAddress(addressBook.getId());
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @Override
    public AddressBook showDefaultAddress() {
        Long userId = BaseContext.getCurrentId();

        AddressBook addressBook = addressBookMapper.showDefaultAddress(userId);

        return addressBook;
    }

    /**
     * 根据id（地址id）查询地址 （修改数据时的数据回显）
     * @param id
     * @return
     */
    @Override
    public AddressBook queryAddressById(Long id) {
        //调用通用的地址查询方法 只需要地址id即可（唯一）
        AddressBook addressBook = AddressBook.builder().id(id).build();

        //只返回一条数据
        List<AddressBook> addressBookList = addressBookMapper.showAddressBook(addressBook);

        return addressBookList.get(0);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void deleteAddressById(Long id) {
        int row = addressBookMapper.deleteAddressById(id);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    @Override
    public void updateAddressById(AddressBook addressBook) {
        //1.补充数据 获取之前的isDefault
        addressBook.setUserId(BaseContext.getCurrentId());
        Integer isDefault = addressBookMapper.getDefaultStatus(addressBook.getId());
        //设置地址默认状态
        addressBook.setIsDefault(isDefault);

        //2.删除原有的地址
        addressBookMapper.deleteAddressById(addressBook.getId());

        //3.插入数据 主键回显
        addressBookMapper.insertAddress(addressBook);
    }


}
