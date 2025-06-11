package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Override
    @Transactional //开启事务
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.基本校验 检查提交的地址是否为空 、 检查购买的商品是否为空

        //1.1校验用户地址
        AddressBook addressBook = new AddressBook();
        addressBook.setId(ordersSubmitDTO.getAddressBookId());
        //获取当前地址 只根据 地址id即可
        List<AddressBook> addressBookList = addressBookMapper.showAddressBook(addressBook);
        //地址簿没有 或者 地址簿为空
        if (addressBookList == null || addressBookList.size() == 0) {
            //抛出地址为空的异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查询的地址簿信息里有一些可用的 并且根据地址id查询的信息最多只有一条数据
        addressBook = addressBookList.get(0);

        //1.2校验用户购买的商品 只根据 用户id即可
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //购物车为空
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            //抛出购物车为空的异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //2.往订单表里插入一条数据
        Orders orders = new Orders();
        //属性拷贝
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setPhone(addressBook.getPhone());//电话
        orders.setAddress(addressBook.getDetail());//具体地址
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setUserId(userId);//下单的用户id
        orders.setNumber(String.valueOf(System.currentTimeMillis()));//订单号(当前时间戳)
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setStatus(Orders.PENDING_PAYMENT);//待付款
        orders.setPayStatus(Orders.UN_PAID);//未支付
        //插入数据 需要进行主键回显
        int row1 = orderMapper.insert(orders);

        //3.往订单明细表里插入n条数据
        //3.1遍历购物车
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);//属性拷贝
            orderDetail.setOrderId(orders.getId());//设置订单id
            //添加进List
            orderDetails.add(orderDetail);
        }
        //3.2执行批量插入 已修复bug:没有将商品数量插入
        int row2 = orderDetailMapper.insertBatch(orderDetails);

        //4.清空购物车表
        shoppingCartMapper.cleanShoppingCart(userId);

        //5.结果封装并返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 分页查询用户历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        ArrayList<OrderVO> records = new ArrayList<>();//封装分页结果

        //1.设置分页数据
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        //2.进行分页查询 根据当前用户的userId去查询 查询了Order表
        Long userId = BaseContext.getCurrentId();
        Page<Orders> page = orderMapper.pageHistoryOrders(userId, ordersPageQueryDTO.getStatus());

        //3.获取相关信息
        long total = page.getTotal();

        List<Orders> ordersList = page.getResult();

        //4.遍历用户的多个订单 并且进行封装
        for (Orders orders : ordersList) {
            //进行属性拷贝 根据set/get方法 可以拷贝子类和父类里所有可以拷贝的属性
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            //4.1查找每个订单的明细 根据订单id
            List<OrderDetail> orderDetailList = orderDetailMapper.queryDetailByOrderId(orders.getId());

            //4.2将每份订单的结果进行封装
            orderVO.setOrderDetailList(orderDetailList);

            //4.3将结果存储
            records.add(orderVO);
        }

        //5.结果封装
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);

        return pageResult;
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return
     */
    @Override
    public OrderVO showOrderDetail(Long id) {
        OrderVO orderVO = new OrderVO();
        //1.查询order表
        Orders orders = orderMapper.queryById(id);
        //2.属性拷贝
        BeanUtils.copyProperties(orders, orderVO);
        //3.查询order_detail表
        List<OrderDetail> orderDetailList = orderDetailMapper.queryDetailByOrderId(id);
        //4.赋值属性
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id 订单id
     */
    @Override
    public void cancelOrder(Long id) {
        int row = orderMapper.cancelOrder(id);
    }

    /**
     * 分页查询 + 订单搜索 （商家）
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageAndSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        ArrayList<OrderVO> records = new ArrayList<>();
        //2.进行分页查询
        Page<Orders> page = orderMapper.pageUserOrders(ordersPageQueryDTO);

        //3.获取数据
        long total = page.getTotal();
        List<Orders> ordersList = page.getResult();

        //4.赋值
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            //查询订单包含的菜品 拼接成字符串 根据订单号
            List<OrderDetail> orderDetailList = orderDetailMapper.queryDetailByOrderId(orders.getId());
            //将菜品名 和 菜品数量进行拼接
            String orderDishes = "";
            for (OrderDetail orderDetail : orderDetailList) {
                orderDishes += orderDetail.getName() + " * " + orderDetail.getNumber() + " ; ";
            }
            orderVO.setOrderDishes(orderDishes);//设置菜品名信息
            records.add(orderVO);//添加进去
        }
        //5.结果封装
        return new PageResult(total, records);
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return
     */
    @Override
    public OrderVO queryOrderDetail(Long id) {
        OrderVO orderVO = new OrderVO();
        //1.封装orders表里的信息
        Orders orders = orderMapper.queryById(id);
        BeanUtils.copyProperties(orders, orderVO);
        //2.封装order_detail表里的信息
        List<OrderDetail> orderDetailList = orderDetailMapper.queryDetailByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        //3.设置菜品名信息
        String orderDishes = "";
        for (OrderDetail orderDetail : orderDetailList) {
            orderDishes += orderDetail.getName() + " * " + orderDetail.getNumber() + " ; ";
        }
        orderVO.setOrderDishes(orderDishes);
        return orderVO;
    }

    /**
     * 统计各个状态的订单数量
     * @return
     */
    @Override
    public OrderStatisticsVO getOrderStatusAmount() {
        OrderStatisticsVO orderStatisticsVO = orderMapper.getOrderStatusAmount();
        return orderStatisticsVO;
    }

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void AdminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        //商家取消订单 并且 添加取消的理由
        orderMapper.AdminCancelReson(ordersCancelDTO);
    }


    /**
     * 用户催单
     * @param id
     */
    @Override
    public void reminderOrder(Long id) {

    }
}
