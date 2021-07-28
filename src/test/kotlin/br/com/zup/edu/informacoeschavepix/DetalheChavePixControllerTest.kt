package br.com.zup.edu.informacoeschavepix

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.grpc.KeyManagerGrpcFactory
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DetalheChavePixControllerTest {
    @field:Inject
    lateinit var dadosGrpc: KeyManagerDadosGrpcServiceGrpc.KeyManagerDadosGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve remover uma chave pix existente`() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        BDDMockito.given(dadosGrpc.dadosChavePix(Mockito.any()))
            .willReturn(chavePixResponse(pixId = pixId, clienteId = clienteId))


        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK.code, response.status.code)
        assertNotNull(response.body())
    }

    @Test
    fun `nao deve remover chave quando pix inexistente`() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        BDDMockito.given(dadosGrpc.dadosChavePix(Mockito.any())).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")

        val exception = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND.code, exception.status.code)
    }

    fun chavePixResponse(pixId: String, clienteId: String): DadosChavePixResponse? {
        val conta = DadosChavePixResponse.ChavePix.Conta.newBuilder()
            .setTipo(TipoConta.CONTA_CORRENTE)
            .setInstituicao("ITAÚ UNIBANCO S.A.")
            .setNomeDoTitular("Rafael Guzzo")
            .setCpfDoTitular("00000011120")
            .setAgencia("291900")
            .setNumeroDaConta("60701190")
            .build()

        val chave = DadosChavePixResponse.ChavePix.newBuilder()
            .setTipo(TipoChavePix.EMAIL)
            .setChave("teste@teste")
            .setConta(conta)
            .setCriadaEm(LocalDateTime.now().let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()

        return DadosChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .setChave(chave)
            .build()
    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MokitoStubFactory {
        @Singleton
        fun stubMock() =
            Mockito.mock(KeyManagerDadosGrpcServiceGrpc.KeyManagerDadosGrpcServiceBlockingStub::class.java)
    }
}