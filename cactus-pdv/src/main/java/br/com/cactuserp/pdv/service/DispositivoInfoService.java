package br.com.cactuserp.pdv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Service
public class DispositivoInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispositivoInfoService.class);

    private String hostnameCache;
    private String macCache;

    public String getHostname() {
        if (hostnameCache != null) return hostnameCache;
        try {
            hostnameCache = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            LOGGER.warn("Não conseguiu obter hostname, usando fallback", e);
            hostnameCache = "UNKNOWN-" + System.getProperty("user.name", "PDV");
        }
        return hostnameCache;
    }

    public String getMacAddress() {
        if (macCache != null) return macCache;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);

            if (ni == null || ni.getHardwareAddress() == null) {
                // Fallback: procura a primeira interface não-virtual com MAC
                Enumeration<NetworkInterface> all = NetworkInterface.getNetworkInterfaces();
                while (all.hasMoreElements()) {
                    NetworkInterface n = all.nextElement();
                    if (!n.isLoopback() && !n.isVirtual() && n.isUp()
                            && n.getHardwareAddress() != null) {
                        ni = n;
                        break;
                    }
                }
            }

            if (ni != null && ni.getHardwareAddress() != null) {
                byte[] mac = ni.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X", mac[i]));
                    if (i < mac.length - 1) sb.append(":");
                }
                macCache = sb.toString();
            }
        } catch (Exception e) {
            LOGGER.warn("Não conseguiu obter MAC address", e);
        }
        return macCache;
    }

    public String getSoNome() {
        return System.getProperty("os.name", "Desconhecido");
    }
}