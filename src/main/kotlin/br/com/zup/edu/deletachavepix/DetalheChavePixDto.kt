package br.com.zup.edu.deletachavepix

import br.com.zup.edu.DadosChavePixResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DetalheChavePixDto(response: DadosChavePixResponse) {

    val pixId = response.pixId
    val clienteId = response.clienteId
    val tipo = response.chave.tipo
    val chave = response.chave.chave
    val conta = Conta(response.chave.conta)

    val criadaEm = response.chave.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    class Conta(contaGrpc: DadosChavePixResponse.ChavePix.Conta) {
        val tipo = contaGrpc.tipo.name
        val instituicao = contaGrpc.instituicao
        val cpfDoTitular = contaGrpc.cpfDoTitular
        val nomeDoTitular = contaGrpc.nomeDoTitular
        val agencia = contaGrpc.agencia
        val numero = contaGrpc.numeroDaConta
    }
}