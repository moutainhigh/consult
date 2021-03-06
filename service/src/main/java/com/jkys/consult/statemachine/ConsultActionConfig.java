package com.jkys.consult.statemachine;

import static com.jkys.consult.common.component.CodeMsg.SERVER_ERROR;
import static com.jkys.consult.statemachine.enums.ConsultEvents.CANCEL;
import static com.jkys.consult.statemachine.enums.ConsultEvents.START;

import com.jkys.consult.common.bean.Consult;
import com.jkys.consult.common.bean.ConsultDomainEvent;
import com.jkys.consult.common.bean.GeneralEvent;
import com.jkys.consult.common.bean.GeneralEventType;
import com.jkys.consult.common.bean.Order;
import com.jkys.consult.exception.ServerException;
import com.jkys.consult.infrastructure.event.GuavaDomainEventPublisher;
import com.jkys.consult.logic.OrderLogic;
import com.jkys.consult.statemachine.constant.Constants;
import com.jkys.consult.statemachine.enums.ConsultEvents;
import com.jkys.consult.statemachine.enums.ConsultStatus;
import com.jkys.consult.statemachine.enums.OrderEvents;
import com.jkys.consult.statemachine.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

@Configuration
@Slf4j
public class ConsultActionConfig {

  @Autowired
  GuavaDomainEventPublisher publisher;

  @Autowired
  OrderLogic orderLogic;

  /**
   * 异常处理Action
   *
   * @return action对象
   */
  @Bean(name = "consultErrorHandlerAction")
  public Action<ConsultStatus, ConsultEvents> consultErrorHandlerAction() {

    return context -> {
      RuntimeException exception = (RuntimeException) context.getException();
      log.error("stateMachine execute error = ", exception);
      context.getStateMachine()
          .getExtendedState().getVariables()
          .put(RuntimeException.class, exception);
      // TODO ---- 如何捕获状态机异常 ------> todoByliming
      throw new ServerException(SERVER_ERROR, "状态机异常");
    };
  }

  // TODO ---- 终了时给商城发消息 ------> todoByliming
  /**
   * 咨询单结束触发 发送IM消息
   */
  @Bean(name = "sendFinishMsgAction")
  public Action<ConsultStatus, ConsultEvents> sendFinishMsgAction() {

    log.info("咨询单结束触发 发送IM消息sendFinishMsgAction");

    return context -> {
//      String bizcode = (String) context.getMessageHeader(Constants.BIZ_CODE);
      Consult consult = (Consult) context.getMessageHeader(Constants.CONSULT);

//      Consult consult = new Consult();
//      consult.setConsultId(bizcode);

      // TODO ---- 根据完结条件不同发送不同的消息 ------> todoByliming
      // TODO 开启咨询单时发送消息谁来做, 如果问诊，需要添加action

      ConsultStatus source = context.getSource().getId();
      ConsultStatus target = context.getTarget().getId();

      String FINISH_TEXT = "test...";
      // Constants.FINISH_TEXT

      GeneralEvent event = GeneralEvent.builder()
          .object(consult)
          .message(FINISH_TEXT)
          .event(GeneralEventType.SEND_FINISH)
          .build();

      publisher.publish(event);
    };
  }

  // TODO ---- 支付成功时给IM发消息 ------> todoByliming

  /**
   * 咨询单开启触发 发送IM消息
   */
  @Bean(name = "sendStartMsgAction")
  public Action<ConsultStatus, ConsultEvents> sendStartMsgAction() {

    log.info("咨询单开启触发 发送IM消息 sendStartMsgAction");

    return context -> {
      Consult consult = context.getMessage().getHeaders().get(Constants.CONSULT, Consult.class);

      GeneralEvent event = GeneralEvent.builder()
          .object(consult)
          .event(GeneralEventType.SEND_START)
          .build();

      publisher.publish(event);
    };
  }

  // TODO ---- 咨询单开启后发送消息给延迟队列 ------> todoByliming

  /**
   * 咨询单结束触发 发送IM消息
   */
  @Bean(name = "sendCheckResponseMessageAction")
  public Action<ConsultStatus, ConsultEvents> sendCheckResponseMessageAction() {

    log.info("咨询单开启后发送消息给延迟队列 sendCheckResponseMessageAction");

    return context -> {
      Consult consult = (Consult) context.getMessageHeader(Constants.CONSULT);

      // TODO ---- 1. 先更新consult表的startTime字段 ------> todoByliming
      // TODO ---- 2. 准备消息体，发送给mq ------> todoByliming
      GeneralEvent event = GeneralEvent.builder()
//          .object(consult)
//          .message(FINISH_TEXT)
//          .event(GeneralEventType.SEND)
          .build();

      publisher.publish(event);
    };
  }

  /**
   * 订单支付触发咨询单开启
   */
  @Bean(name = "consultStartAction")
  public Action<OrderStatus, OrderEvents> consultRefundAction() {

    return context -> {
//      String bizcode = (String) context.getMessageHeader(Constants.BIZ_CODE);
      Order order = (Order) context.getMessageHeader(Constants.ORDER);

//      Consult consult = new Consult();
//      // TODO ---- 此处由于咨询单和订单ID都用同一个bizCode, 所以直接setConsultId ------> todoByliming
//      consult.setConsultId(order.getConsultId());

      ConsultDomainEvent event = ConsultDomainEvent.builder()
          .order(order)
          .event(START)
          .build();

      publisher.publish(event);
    };
  }

  /**
   * 订单取消触发咨询单取消
   */
  @Bean(name = "consultCancelAction")
  public Action<OrderStatus, OrderEvents> consultCancelAction() {

    return context -> {
//      String bizcode = (String) context.getMessageHeader(Constants.BIZ_CODE);
      Order order = (Order) context.getMessageHeader(Constants.ORDER);

//      Consult consult = new Consult();
//      // TODO ---- 此处由于咨询单和订单ID都用同一个bizCode, 所以直接setConsultId ------> todoByliming
//      consult.setConsultId(order.getConsultId());

      ConsultDomainEvent event = ConsultDomainEvent.builder()
          .order(order)
          .event(CANCEL)
          .build();

      publisher.publish(event);
    };
  }

}
