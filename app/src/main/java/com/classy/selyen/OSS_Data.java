package com.classy.selyen;

public class OSS_Data {
    private String license;
    private String name;
    private String addr;

    public OSS_Data(String license, String name, String addr){
        this.license = license;
        this.name = name;
        this.addr = addr;
    }

    public String getLicense()
    {
        return this.license;
    }

    public String getName()
    {
        return this.name;
    }

    public String getAddr()
    {
        return this.addr;
    }
}
