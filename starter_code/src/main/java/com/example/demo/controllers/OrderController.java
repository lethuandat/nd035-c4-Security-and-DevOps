package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			final ResponseEntity<UserOrder> response = ResponseEntity.notFound().build();
			log.info("Order request failure! Username not found!", response);
			return response;
		}

		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		final ResponseEntity<UserOrder> response = ResponseEntity.ok(order);
		log.info("Order request successful.", response);

		return response;
	}

	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}