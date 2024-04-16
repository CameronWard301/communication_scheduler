package io.github.cameronward301.communication_scheduler.worker.communication_worker.config;

import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;

public interface SslContextWrapper {
    SslContext buildSslContext() throws SSLException;
}
