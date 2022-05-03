package com.eu.interopehrate.mr2dbackup.SEHRCloudProviders;

public enum SEHRCloudProviders {
    SEHR_CLOUD_1("http://213.249.46.253:5000"),
    SEHR_CLOUD_2("http://213.249.46.243:5000");

    private String cloudProviderUrl;

    SEHRCloudProviders(String cloudUrl){
        this.cloudProviderUrl = cloudUrl;
    }
    public String getCloudProviderUrl() {
        return cloudProviderUrl;
    }
}
