package br.com.zup.edu.cadastrachavepix


import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.NovaChavePixResponse
import br.com.zup.edu.TipoChavePix
import br.com.zup.edu.compartilhado.grpc.KeyManagerGrpcFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest
internal class CadastraChavePixControllerTest {

    @field:Inject
    lateinit var registraGrpc: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve registrar uma nova chave pix`() {
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val responseGrpc = NovaChavePixResponse.newBuilder().setPixId(pixId).build()

        given(registraGrpc.cadastrarChavePix(Mockito.any())).willReturn(responseGrpc)

        val registraChavePixRequest = RegistraChavePixRequest(
            tipoConta = TipoConta.CONTA_CORRENTE,
            tipoChave = TipoChave.EMAIL,
            valor = "teste@teste")

        val request = HttpRequest.POST("/api/v1/clientes/$clientId/pix", registraChavePixRequest)
        val response = client.toBlocking().exchange(request, RegistraChavePixRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId))
    }

    @Test
    fun `nao deve registrar uma nova chave pix quando parametros forem invalidos`() {
        val clientId = UUID.randomUUID().toString()

        val registraChavePixRequest = RegistraChavePixRequest(
            tipoConta = TipoConta.CONTA_CORRENTE,
            tipoChave = TipoChave.EMAIL,
            valor = "1023415646")

        val request = HttpRequest.POST("/api/v1/clientes/$clientId/pix", registraChavePixRequest)

        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, RegistraChavePixRequest::class.java)
        }

        assertThat(exception.message, CoreMatchers.containsString("Chave Pix invalida (EMAIL)"));
        assertEquals(HttpStatus.BAD_REQUEST.code, exception.status.code)

    }

    @Test
    fun `nao deve registrar uma nova chave pix chave pix já cadastrada`() {
        val clientId = UUID.randomUUID().toString()

        given(registraGrpc.cadastrarChavePix(Mockito.any())).willThrow(StatusRuntimeException(Status.ALREADY_EXISTS))

        val registraChavePixRequest = RegistraChavePixRequest(
            tipoConta = TipoConta.CONTA_CORRENTE,
            tipoChave = TipoChave.EMAIL,
            valor = "teste@teste")

        val request = HttpRequest.POST("/api/v1/clientes/$clientId/pix", registraChavePixRequest)

        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, RegistraChavePixRequest::class.java)
        }

        assertEquals("Chave Pix Já registrada", exception.message)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.code, exception.status.code)

    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MokitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub::class.java)
    }

}