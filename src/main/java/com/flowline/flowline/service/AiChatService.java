package com.flowline.flowline.service;

import com.flowline.flowline.dto.ChatRequestDTO;
import com.flowline.flowline.dto.ChatResponseDTO;
import com.flowline.flowline.repository.OrderRepository;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service

public class AiChatService {

    private final ChatClient chatClient;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AiChatService(ChatClient.Builder builder,
                         OrderRepository orderRepository,
                         ProductRepository productRepository,
                         UserRepository userRepository) {
        this.chatClient = builder.build();
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private static final String SYSTEM_PROMPT = """
    You are an operational assistant for FlowLine, a B2B platform 
    for tracking material flow in factories and warehouses.
    Your role is to help managers and administrators query and 
    understand operational data from the system.
    STRICT RULES:
    - Only answer questions related to FlowLine data: warehouses, 
      sectors, users, products, and movement orders.
    - If asked anything outside this scope, politely refuse.
    - Never make up data. Only use the data provided to you.
    - Always respond in the same language the user writes in.
    - Be concise and professional.
    """;

    public ChatResponseDTO chat(ChatRequestDTO request) {

        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long totalUsers = userRepository.count();


        String context = """
           CURRENT SYSTEM DATA:
           - Total orders: %d
           - Total products: %d
           - Total users: %d
           """.formatted(totalOrders, totalProducts, totalUsers);

        String response = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "\n\n" + context)
                .user(request.ask())
                .call()
                .content();

        return new ChatResponseDTO(response);
    }
}
