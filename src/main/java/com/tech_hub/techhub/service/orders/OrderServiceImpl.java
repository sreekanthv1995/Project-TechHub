package com.tech_hub.techhub.service.orders;

import com.tech_hub.techhub.entity.*;
import com.tech_hub.techhub.repository.OrderRepository;
import com.tech_hub.techhub.repository.VariantRepository;
import com.tech_hub.techhub.service.return_items.ReturnItemService;
import com.tech_hub.techhub.service.cancelled_items.CancelledItemService;
import com.tech_hub.techhub.service.wallet.WalletService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.tech_hub.techhub.entity.PaymentMode.*;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    VariantRepository variantRepository;
    @Autowired
    CancelledItemService cancelledItemService;
    @Autowired
    ReturnItemService returnItemService;
    @Autowired
    WalletService walletService;
    private final Random random = new Random();

    @Override
    public Order saveOrder(UserEntity user, List<CartItems> cartItems, Double totalPrice,
                           PaymentMode selectedPaymentMode, Address userAddress) {

        Order order = getOrder(user, totalPrice, selectedPaymentMode, userAddress);
        String orderId = generateOrderId();
        order.setOrderId(orderId);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = getOrderItem(cartItem);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .toList();
        order.setOrderItems(orderItems);
    order.setStatus(selectedPaymentMode == COD || selectedPaymentMode == WALLET ? Status.CONFIRMED : Status.PENDING);
        orderRepository.save(order);
        return order;
    }

    @NotNull
    private static OrderItem getOrderItem(CartItems cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setVariant(cartItem.getVariant());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setVariantPrice(cartItem.getVariant().getPrice());
        return orderItem;
    }

    @NotNull
    private static Order getOrder(UserEntity user, Double totalPrice, PaymentMode selectedPaymentMode,
                                  Address userAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(totalPrice);
        order.setPaymentMode(selectedPaymentMode);
        order.setAddress(userAddress);
        return order;
    }

    @Override
    public Optional<Cart> getOrderByUser(UserEntity user) {
        return orderRepository.getOrderByUser(user);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Optional<Order> findFirstByUserOrderByOrderDateAsc(UserEntity user) {
        return orderRepository.findFirstByUserOrderByOrderDateAsc(user);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByUser(UserEntity user) {
        return orderRepository.findByUser(user);
    }



    public String generateOrderId() {
        int orderIdLength = 11;
        String prefix = "OD";
        String allowedChars = "0123456789";

        StringBuilder orderId = new StringBuilder(prefix);
        for (int i = 0; i < orderIdLength; i++) {
            int index = random.nextInt(allowedChars.length());
            orderId.append(allowedChars.charAt(index));
        }
        return orderId.toString();
    }

    @Override
    public Integer calculateItemCount(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        int itemCount = 0;
        if (orderItems != null) {
            itemCount = orderItems.size();
        }
        return itemCount;
    }

    @Override
    public void updateOrderStatus(Long id, Status status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            LocalDate currentDate = LocalDate.now();
            switch (status) {
                case PENDING -> order.setStatus(Status.PENDING);

                case PACKED -> {
                    order.setStatus(Status.PACKED);
                    order.setPackingDate(currentDate);
                }
                case SHIPPED -> {
                    order.setStatus(Status.SHIPPED);
                    order.setShippingDate(currentDate);
                }
                case DELIVERED -> {
                    order.setStatus(Status.DELIVERED);
                    order.setDeliveryDate(currentDate);
                }
                case CANCELLED -> order.setStatus(Status.CANCELLED);
                case RETURN_PENDING -> order.setStatus(Status.RETURN_PENDING);
                case RETURN_CONFIRMED -> order.setStatus(Status.RETURN_CONFIRMED);
                case RETURNED -> order.setStatus(Status.RETURNED);
                default -> throw new IllegalArgumentException("invalid");


            }
            orderRepository.save(order);
        }
    }

    @Override
    public Status getOrderStatusById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            return order.getStatus();
        } else {
            return null;
        }
    }

    @Override
    public void cancelOrder(Long id, CancelReasons reason) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            UserEntity user = order.getUser();
            CancelledItems item = new CancelledItems();
            try {
                if (order.getStatus() != Status.CANCELLED && order.getPaymentMode() == COD) {
                    order.setStatus(Status.CANCELLED);
                } else if (order.getPaymentMode() == RAZORPAY) {
                    double orderPrice = order.getTotalPrice();
                    Optional<Wallet> optionalUserWallet = walletService.getWallet(user);
                    if (optionalUserWallet.isPresent()) {
                        Wallet userWallet = optionalUserWallet.get();
                        returnAmountToWallet(order, orderPrice, userWallet);
                    } else {
                        walletService.createWallet(user);
                    }
                }
                addToCancelledItems(reason, order, item);
                updateStock(order);

                orderRepository.save(order);
                cancelledItemService.save(item);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void returnAmountToWallet(Order order, double orderPrice, Wallet userWallet) {
        double currentWalletAmount = userWallet.getWalletAmount();
        order.setStatus(Status.CANCELLED);
        userWallet.setWalletAmount(orderPrice + currentWalletAmount);
        walletService.save(userWallet);
    }

    private static void addToCancelledItems(CancelReasons reason, Order order, CancelledItems item) {
        item.setOrder(order);
        item.setUser(order.getUser());
        item.setReason(reason);
        item.setCancellationDate(LocalDate.now());
        item.setStatus(Status.CANCELLED);
    }

    @Override
    public void returnOrder(Long id, CancelReasons reason) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            ReturnItems returnItem = new ReturnItems();
            addToReturnList(reason, order, returnItem);
            order.setStatus(Status.RETURN_PENDING);
            orderRepository.save(order);
            returnItemService.save(returnItem);
        }
    }

@Override
public void proceedRefund(Long id) {

    Optional<ReturnItems> optionalReturnItem = returnItemService.getReturnItemById(id);
    if (optionalReturnItem.isPresent()){
        ReturnItems returnItem = optionalReturnItem.get();
        Order order = returnItem.getOrder();
        UserEntity user = order.getUser();
        if (returnItem.getReturnReasons() == CancelReasons.WRONG_ITEM_RECEIVED){
            if (order.getPaymentMode() == RAZORPAY){
                handleRazorpayRefund(returnItem, order, user);
                updateStock(order);
            }else {
                handleNonRazorpayRefund(returnItem, order);
                updateStock(order);
            }
        }else {
            if (order.getPaymentMode() == RAZORPAY){
                handleRazorpayRefund(returnItem, order, user);
                updateReturnStock(order);
            }else {
                handleNonRazorpayRefund(returnItem, order);
                updateReturnStock(order);
            }
        }
    }
}

    private void handleNonRazorpayRefund(ReturnItems returnItem, Order order) {
        order.setStatus(Status.RETURN_CONFIRMED);
        returnItem.setStatus(Status.RETURN_CONFIRMED);
        orderRepository.save(order);
    }

    private void handleRazorpayRefund(ReturnItems returnItem, Order order, UserEntity user) {
        double totalPrice = order.getTotalPrice();
        Optional<Wallet> optionalWallet = walletService.getWallet(user);
        if (optionalWallet.isPresent()) {
            Wallet userWallet = optionalWallet.get();
            setAmountToWallet(returnItem, order, totalPrice, userWallet);
        }else {
            walletService.createWallet(user);
        }
    }

    private void setAmountToWallet(ReturnItems returnItem, Order order, double totalPrice, Wallet userWallet) {
        double currentWalletAmount = userWallet.getWalletAmount();
        userWallet.setWalletAmount(currentWalletAmount + totalPrice);
        order.setStatus(Status.RETURN_CONFIRMED);
        returnItem.setStatus(Status.RETURN_CONFIRMED);
        walletService.save(userWallet);
        orderRepository.save(order);
    }


    private static void addToReturnList(CancelReasons reason, Order order, ReturnItems returnItems) {
        returnItems.setOrder(order);
        returnItems.setUser(order.getUser());
        returnItems.setReturnReasons(reason);
        returnItems.setReturnedDate(LocalDate.now());
        returnItems.setOrder(order);
        returnItems.setOrderQuantity(order.getOrderItems().size());
        returnItems.setStatus(Status.RETURN_PENDING);
    }

    private void updateStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Variant variant = orderItem.getVariant();
            int cancelledQuantity = orderItem.getQuantity();
            variant.setStock(variant.getStock() + cancelledQuantity);
            variantRepository.save(variant);
        }
    }

    private void updateReturnStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Variant variant = orderItem.getVariant();
            int returnQuantity = orderItem.getQuantity();
            variant.setReturnedStock(variant.getReturnedStock()+returnQuantity);
            variantRepository.save(variant);
        }
    }



}









