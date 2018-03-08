/*
 * Copyright 2015, 2016 Ether.Camp Inc. (US)
 * This file is part of Ethereum Harmony.
 *
 * Ethereum Harmony is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ethereum Harmony is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ethereum Harmony.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.blockshine;

import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.tomcat.util.net.Nio2Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;




@SpringBootApplication
@EnableScheduling
public class Application {

    //4 core + 8 giga-bytes configuration
    @Value("${tomcat.connector.maxthreadstimecpus}")
    private int maxThreads = 8;

    @Value("${tomcat.connector.maxconnections}")
    private int maxConnections = 800;

    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatEmbeddedServletContainerFactory.setProtocol("org.apache.coyote.http11.Http11Nio2Protocol");
        tomcatEmbeddedServletContainerFactory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            AbstractHttp11JsseProtocol<Nio2Channel> handler = (AbstractHttp11JsseProtocol)connector.getProtocolHandler();
            handler.setMaxThreads(Runtime.getRuntime().availableProcessors() * maxThreads);
            handler.setMaxConnections(maxConnections);
        });

        return tomcatEmbeddedServletContainerFactory;
    }

    /**
     * Does one of:
     * - start Harmony peer;
     * - perform action and exit on completion.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}


