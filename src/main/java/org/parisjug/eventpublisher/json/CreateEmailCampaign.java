package org.parisjug.eventpublisher.json;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CreateEmailCampaign {

    Sender sender;
    String name;
    private Long templateId;
    String subject;
    String htmlContent;
    private Map<String, String> params;
    Boolean inlineImageActivation = false;
    Boolean sendAtBestTime = false;
    Boolean abTesting = false;
    Boolean ipWarmupEnable = false;

    public CreateEmailCampaign(String name, Sender sender, String subject) {
        this.sender = sender;
        this.name = name;
        this.sender = sender;
        this.subject = subject;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

}