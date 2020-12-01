package org.parisjug.eventpublisher.json;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Sender {
    String name;
    String email;

    public Sender(String name, String email) {
        this.name = name;
        this.email = email;
    }

    

}

