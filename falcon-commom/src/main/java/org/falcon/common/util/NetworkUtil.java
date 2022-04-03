package org.falcon.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Slf4j
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
            log.warn("本机IP地址获取失败", e);
        }
        return "";
    }

    /**
     * 获取请求ip地址
     *
     * @return
     */
    public static String getRequestIp() {
        HttpServletRequest request = getHttpServletRequest();
        // 反向代理时request.getRemoteAddr()获取不到客户端的真实IP地址，但是在转发请求的HTTP头信息中增加了ip地址，所以优先从HTTP头信息中获取ip
        String ipAddress = getIpAddressForHeader(request);
        if (isEmptyIp(ipAddress)) {
            ipAddress = getIpAddressForRemoteAddr(request);
        }
        return getIp(ipAddress);
    }

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取ip
     *
     * @param ipAddress
     * @return
     */
    private static String getIp(String ipAddress) {
        if (isEmptyIp(ipAddress)) {
            return null;
        }
        String ipSeparate = ",";
        if (ipAddress.indexOf(ipSeparate) < 0) {
            return ipAddress;
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
        String[] ipArray = ipAddress.split(ipSeparate);
        for (String ip : ipArray) {
            if (!isEmptyIp(ip)) {
                return ip;
            }
        }
        return null;
    }

    /**
     * 判断ip地址是否为空
     *
     * @param ipAddress
     * @return
     */
    private static boolean isEmptyIp(String ipAddress) {
        return StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress);
    }

    /**
     * 从request的header中获取ip
     *
     * @param request 请求对象
     * @return
     */
    private static String getIpAddressForHeader(HttpServletRequest request) {
        List<String> nameList = Arrays.asList(
                "x-forwarded-for",
                "x-real-ip",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR");

        for (String name : nameList) {
            String ipAddress = request.getHeader(name);
            if (!isEmptyIp(ipAddress)) {
                return ipAddress;
            }
        }
        return null;
    }

    /**
     * 获取客户端的IP地址
     *
     * @param request
     * @return
     */
    private static String getIpAddressForRemoteAddr(HttpServletRequest request) {
        String localIp = "127.0.0.1";
        String localIpv6 = "0:0:0:0:0:0:0:1";
        String ipAddress = request.getRemoteAddr();
        if (ipAddress.equals(localIp) || ipAddress.equals(localIpv6)) {
            try {
                // 根据网卡取本机配置的IP
                InetAddress inet = InetAddress.getLocalHost();
                ipAddress = inet.getHostAddress();
            } catch (UnknownHostException e) {
                log.error("获取ip失败", e);
            }
        }
        return ipAddress;
    }

}
