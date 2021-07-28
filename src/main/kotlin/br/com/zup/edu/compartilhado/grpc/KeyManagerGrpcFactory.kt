package br.com.zup.edu.compartilhado.grpc

import br.com.zup.edu.KeyManagerDadosGrpcServiceGrpc
import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerListaGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRemoveGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGrpcFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = KeyManagerGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deletaChave() = KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun buscaChave() = KeyManagerDadosGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChaves() = KeyManagerListaGrpcServiceGrpc.newBlockingStub(channel)


}