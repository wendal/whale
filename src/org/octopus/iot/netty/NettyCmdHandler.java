package org.octopus.iot.netty;

import org.octopus.Zs;
import org.octopus.iot.service.CmdHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class NettyCmdHandler extends CmdHandler {
	
    static final AttributeKey<Long> UID = AttributeKey.valueOf(Zs.UID);

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
