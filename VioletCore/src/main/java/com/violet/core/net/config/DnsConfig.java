package com.violet.core.net.config;

import java.util.ArrayList;

/**
 * Created by kan212 on 2018/7/27.
 */

public class DnsConfig {
    private String dnsSwitch;
    private ArrayList<String> hostIp;
    private String hostDomain;

    public String getDnsSwitch() {
        return dnsSwitch;
    }

    public void setDnsSwitch(String dnsSwitch) {
        this.dnsSwitch = dnsSwitch;
    }

    public ArrayList<String> getHostIp() {
        if (null == hostIp) {
            hostIp = new ArrayList<String>(0);
        }
        return hostIp;
    }

    public void setHostIp(ArrayList<String> hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostDomain() {
        return hostDomain;
    }

    public void setHostDomain(String hostDomain) {
        this.hostDomain = hostDomain;
    }
}
