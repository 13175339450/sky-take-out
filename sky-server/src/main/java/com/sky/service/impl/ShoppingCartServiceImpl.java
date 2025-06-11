package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.*;
import com.sky.mapper.*;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //1.先查询购物车表（shopping_cart）里是否已经存在该信息
        //1.1封装对应数据
        ShoppingCart shoppingCart = new ShoppingCart();
        //1.2属性拷贝
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //1.3获取当前用户id ThreadLocal 获取用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //1.4根据结果 用动态sql去查询数据 (返回的数据要么为null 要么只有一个实体)
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //2.如果已经存在 则 数量+1 (不论菜品或者套餐 动态sql)
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            //数量 + 1
            shoppingCarts.get(0).setNumber(shoppingCarts.get(0).getNumber() + 1);
            int row1 = shoppingCartMapper.updateShoppingCart(shoppingCarts.get(0));
        } else {
            //3.不存在 -> (当前已经有 DTO了，继续加入数据)
            if (shoppingCart.getDishId() != null) {
                //3.1 如果新添加的是菜品
                //1.查询菜品的其他信息 name、image、amount 根据dishId
                Dish dish = dishMapper.queryById(shoppingCart.getDishId());
                //2.将相关信息赋值
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //3.2 如果添加的是套餐
                //1.查询菜品的其他信息 name、image、amount 根据setmealId
                Setmeal setmeal = setmealMapper.queryOneById(shoppingCart.getSetmealId());
                //2.将相关信息赋值
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            //4.统一赋值 number和createTime
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            //5.执行插入操作
            int row2 = shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //1.获取当前userId
        Long userId = BaseContext.getCurrentId();
        //2.封装数据
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        return shoppingCarts;

    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        //1.获取当前userId
        Long userId = BaseContext.getCurrentId();
        //2.根据userId 清空购物车
        shoppingCartMapper.cleanShoppingCart(userId);
    }

    /**
     * 删除购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        /* 当选择减少某个 购物车 菜品/套餐 的数量时 执行删除操作
         * 菜品number - 1 ： 当菜品数量已经为1时 删除该行数据 */
        //1.封装数据 注入userId
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId).build();
        //2.属性拷贝
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //3.判断当前number数量是否为1
        int number = shoppingCartMapper.getNumber(shoppingCart);
        if (number > 1) {
            //执行更新操作
            shoppingCart.setNumber(number - 1);
            int row = shoppingCartMapper.updateShoppingCart(shoppingCart);
        } else {
            //执行删除操作
            int row = shoppingCartMapper.deleteShoppingCart(shoppingCart);
        }
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

        //根据订单id 将订单和订单明细添加到购物车表里
        List<OrderDetail> orderDetailList = orderDetailMapper.queryDetailByOrderId(id);//查询订单明细
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            //属性拷贝
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            //设置其他属性
            shoppingCart.setCreateTime(LocalDateTime.now());//创建时间
            shoppingCart.setUserId(BaseContext.getCurrentId());//userId
            //加入批量
            shoppingCarts.add(shoppingCart);
        }

        //批量插入到购物车里
        int row = shoppingCartMapper.insertBatch(shoppingCarts);
    }
}
