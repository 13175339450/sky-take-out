package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址簿管理相关接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前用户的所有地址
     * @return
     */
    @GetMapping("list")
    @ApiOperation("查询当前用户的所有地址")
    public Result<List<AddressBook>> showAddressBook(){
        List<AddressBook> addressBook = addressBookService.showAddressBook();
        return Result.success(addressBook);
    }

    /**
     * 新增地址
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result saveAddress(@RequestBody AddressBook addressBook){
        addressBookService.insertAddress(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    @ApiOperation("设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("设置默认地址");
        addressBookService.setDefaultAddress(addressBook);
        return Result.success();
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> showDefaultAddress(){
        AddressBook addressBook = addressBookService.showDefaultAddress();
        return Result.success(addressBook);
    }

    /**
     * 根据id（地址id）查询地址 （修改数据时的数据回显）
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id（地址id）查询地址 （修改数据时的数据回显）")
    public Result<AddressBook> queryAddressById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.queryAddressById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id删除地址
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteAddress(Long id){
        addressBookService.deleteAddressById(id);
        return Result.success();
    }


    /**
     * 根据id修改地址
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result modifyAddress(@RequestBody AddressBook addressBook){
        //addressBook数据里缺少userId 和 isDefault
        addressBookService.updateAddressById(addressBook);
        return Result.success();
    }
}
