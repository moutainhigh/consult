package com.jkys.consult.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkys.consult.common.bean.Order;
import com.jkys.consult.infrastructure.db.mapper.OrderMapper;
import com.jkys.consult.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends
    ServiceImpl<OrderMapper, Order> implements
    OrderService {

  @Override
  public Order selectByConsultId(String consultId) {
    Order context = this.getOne(new QueryWrapper<Order>().lambda().eq(Order::getConsultId, consultId));
    return context;
  }

  @Override
  public void updateByConsultId(Order order) {
    order.insertOrUpdate();
  }

  @Override
  public Order selectByOrderId(String orderId) {
    Order context = this.getOne(new QueryWrapper<Order>().lambda().eq(Order::getOrderId, orderId));
    return context;
  }

  @Override
  public void updateByOrderId(Order order) {
    order.insertOrUpdate();
  }
}
