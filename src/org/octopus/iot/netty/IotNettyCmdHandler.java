package org.octopus.iot.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import org.nutz.ioc.loader.annotation.IocBean;
import org.octopus.iot.IotKeys;
import org.octopus.iot.service.IotCmdHandler;

@IocBean
public class IotNettyCmdHandler extends IotCmdHandler {
	
    static final AttributeKey<Long> UID = AttributeKey.valueOf(IotKeys.UID);

	public Object resp(Object ctx, String stat, String msg) {
		return ((ChannelHandlerContext)ctx).write(stat + "," + msg + "\r\n");
	}

	public Long uid(Object ctx) {
		return ((ChannelHandlerContext)ctx).attr(UID).get();
	}

	public void uid(Object ctx, long uid) {
		((ChannelHandlerContext)ctx).attr(UID).set(uid);
	}

	public void close(Object ctx) {
		((ChannelFuture)ctx).addListener(ChannelFutureListener.CLOSE);
	}

}
