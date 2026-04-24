package com.flowline.flowline.service;

import com.flowline.flowline.dto.ChatRequestDTO;
import com.flowline.flowline.dto.ChatResponseDTO;
import com.flowline.flowline.dto.DashboardResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service

public class AiChatService {

    private final ChatClient chatClient;
    private final DashboardService dashboardService;

    public AiChatService(ChatClient.Builder builder,
                         DashboardService dashboardService) {
        this.chatClient = builder.build();
        this.dashboardService = dashboardService;
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

        DashboardResponseDTO dashboard = dashboardService.getDashboard();

        String context = """
           CURRENT SYSTEM DATA:
           - Total orders: %d
           - Total products: %d
           - Total users: %d
           """.formatted(dashboard.orders().total(), dashboard.totalProducts(), dashboard.totalUsers());

        String response = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "\n\n" + context)
                .user(request.ask())
                .call()
                .content();

        return new ChatResponseDTO(response);
    }
}
