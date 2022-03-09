package org.falconframework.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/8
 */
public class NetworkUtil {

    /**
     * 获取本机ip
     * @return
     */
    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("本机IP地址获取失败：" + e.getMessage());
        }
        return "";
    }

}
