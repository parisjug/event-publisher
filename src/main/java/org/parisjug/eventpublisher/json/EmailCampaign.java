package org.parisjug.eventpublisher.json;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class EmailCampaign {
    public String id;
    public String name;
    public String type;
    public String htmlContent;
}
