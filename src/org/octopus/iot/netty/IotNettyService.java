package org.octopus.iot.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.iot.service.IotSensorService;
import org.octopus.iot.service.IotService;

@IocBean(create = "init", depose="close")
public class IotNettyService {
	
	private static final Log log = Logs.get();

	@Inject
	Dao dao;
	@Inject
	IotService iotService;
	@Inject
	IotSensorService iotSensorService;
	@Inject
	IotNettyTcpServerInitializer iotNettyTcpServerInitializer;
	@Inject
	IotNettyUdpHandler iotNettyUdpHandler;
	
	@Inject("java:$conf.get('netty.iot.tcp_port')")
	int tcpPort;
	
	@Inject("java:$conf.get('netty.iot.udp_port')")
	int udpPort;

	ServerBootstrap tcpBoot;
	Bootstrap udpBoot;
	
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	EventLoopGroup udpWorkGroup;

	ChannelFuture tcpcf;
	ChannelFuture udpcf;

	public void init() throws InterruptedException {
		if (tcpPort == 0 && udpPort == 0) {
			log.info("tcpPort=0 and udpPort=0, socket proctol disable");
			return;
		}
		bossGroup = new NioEventLoopGroup(2);
		workerGroup = new NioEventLoopGroup();
		udpWorkGroup = new NioEventLoopGroup();
		if (tcpPort > 0) {
			tcpBoot = new ServerBootstrap();
			tcpBoot.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(iotNettyTcpServerInitializer);
			tcpcf = tcpBoot.bind(tcpPort);
		}
		if (udpPort > 0) {
			udpBoot = new Bootstrap();
			udpBoot.group(udpWorkGroup)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(iotNettyUdpHandler);
			udpcf = udpBoot.bind(udpPort);
		}
	}
	
	public void close() throws InterruptedException {
		try {
			if (tcpcf != null)
				tcpcf.channel().close().sync();
			if (udpcf != null)
				udpcf.channel().close().sync();
		} finally {
			if (bossGroup != null)
				bossGroup.shutdownGracefully().await();
			if (workerGroup != null)
				workerGroup.shutdownGracefully().await();
			if (udpWorkGroup != null)
				udpWorkGroup.shutdownGracefully().await();
		}
	}

}
