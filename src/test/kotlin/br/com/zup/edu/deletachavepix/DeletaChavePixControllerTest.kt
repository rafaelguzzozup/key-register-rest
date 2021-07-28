package br.com.zup.edu.deletachavepix

import br.com.zup.edu.*
import br.com.zup.edu.cadastrachavepix.RegistraChavePixRequest
import br.com.zup.edu.cadastrachavepix.TipoChave
import br.com.zup.edu.cadastrachavepix.TipoConta
import br.com.zup.edu.compartilhado.grpc.KeyManagerGrpcFactory
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
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DeletaChavePixControllerTest {

    @field:Inject
    lateinit var removeGrpc: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve remover uma chave pix existente`() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val responseGrpc = DeletaChavePixResponse.newBuilder().setPixId(pixId).setIdCliente(clienteId).build()

        given(removeGrpc.deletarChavePix(Mockito.any())).willReturn(responseGrpc)


        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.NO_CONTENT.code, response.status.code)
    }

    @Test
    fun `nao deve remover chave quando pix inexistente`() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        given(removeGrpc.deletarChavePix(Mockito.any())).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clienteId/pix/$pixId")

        val exception = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND.code, exception.status.code)
    }


    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MokitoStubFactory {
        @Singleton
        fun stubMock() =
            Mockito.mock(KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub::class.java)
    }
}