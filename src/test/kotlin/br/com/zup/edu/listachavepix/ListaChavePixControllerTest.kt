package br.com.zup.edu.listachavepix

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.grpc.KeyManagerGrpcFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest

internal class ListaChavePixControllerTest {

    @field:Inject
    lateinit var listaChavesGrpc: KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve listar todas as chaves pix existente`() {

        val clienteId = UUID.randomUUID().toString()

        val respostaGrpc = listaChavePixResponse(clienteId)

        given(listaChavesGrpc.listaChavesPix(Mockito.any())).willReturn(respostaGrpc)


        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(response.body()!!.size, 2)
    }

    private fun listaChavePixResponse(clienteId: String): ListaChavesPixResponse {
        val chaveEmail = ListaChavesPixResponse.ChavePixResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoChave(TipoChavePix.EMAIL)
            .setValor("teste@teste")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadaEm(LocalDateTime.now().let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()

        val chaveAleatoria = ListaChavesPixResponse.ChavePixResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoChave(TipoChavePix.ALEATORIO)
            .setValor("")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadaEm(LocalDateTime.now().let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()

        return ListaChavesPixResponse.newBuilder()
            .setClienteId(clienteId)
            .addAllChavePix(listOf(chaveEmail, chaveAleatoria))
            .build()

    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MokitoStubFactory {
        @Singleton
        fun stubMock() =
            Mockito.mock(KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub::class.java)
    }
}