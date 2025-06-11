package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;

import java.util.ArrayList;
import java.util.List;

public interface ShoppingCartMapper {

    /**
     * 动态查询购物车数据 id, dish_id, setmeal_id, dish_flavor
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    /**
     * 根据userId更新 购物车里物品的数量 （菜品或套餐）
     * @param shoppingCart
     * @return
     */
    int updateShoppingCart(ShoppingCart shoppingCart);

    /**
     * 新增一条 购物车数据
     * @param shoppingCart
     * @return
     */
    int insert(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void cleanShoppingCart(Long userId);

    /**
     * 获取菜品数量
     * @param shoppingCart
     * @return
     */
    int getNumber(ShoppingCart shoppingCart);

    /**
     * 删除指定菜品
     * @param shoppingCart
     * @return
     */
    int deleteShoppingCart(ShoppingCart shoppingCart);

    /**
     * 批量插入购物车
     * @param shoppingCarts
     * @return
     */
    int insertBatch(ArrayList<ShoppingCart> shoppingCarts);

}
