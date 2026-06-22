package com.dakkho.android.data.api

import com.dakkho.android.data.api.ApiResult
import com.dakkho.android.domain.model.CreateTicketRequest
import com.dakkho.android.domain.model.FAQCategory
import com.dakkho.android.domain.model.HelpCategory
import com.dakkho.android.domain.model.SendMessageRequest
import com.dakkho.android.domain.model.SupportTicketDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Phase 28: Help & Support API Service #83-88
 * Covers: Help Hub, FAQ, Contact, Tickets, Report Issue
 */
interface SupportApiService {

    // ── #83: Help Hub ──

    @GET("api/help/categories")
    suspend fun getHelpCategories(): Response<ApiResult<List<HelpCategory>>>

    // ── #84: FAQ ──

    @GET("api/help/faq")
    suspend fun getFAQs(): Response<ApiResult<List<FAQCategory>>>

    // ── #85-86: Support Tickets ──

    @GET("api/support/tickets")
    suspend fun getTickets(): Response<ApiResult<List<SupportTicketDto>>>

    @POST("api/support/tickets")
    suspend fun createTicket(@Body request: CreateTicketRequest): Response<ApiResult<SupportTicketDto>>

    @GET("api/support/tickets/{id}")
    suspend fun getTicketDetail(@Path("id") ticketId: String): Response<ApiResult<SupportTicketDto>>

    @GET("api/support/tickets/{id}/messages")
    suspend fun getTicketMessages(@Path("id") ticketId: String): Response<ApiResult<List<com.dakkho.android.domain.model.TicketMessageDto>>>

    @POST("api/support/tickets/{id}/messages")
    suspend fun sendMessage(
        @Path("id") ticketId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResult<com.dakkho.android.domain.model.TicketMessageDto>>

    // ── #87: Report Issue ──

    @POST("api/support/bug-report")
    suspend fun submitBugReport(@Body report: Map<String, String>): Response<ApiResult<Unit>>
}
