package org.integr8ly

class RegistryImage {

    RegistryImage(String url) {
        if (!url.startsWith("docker://")) {
            url = "docker://" + url
        }
        URI uri = new URI(url);
        def pathParts = uri.getPath().split("/|:") - null - ''
        this.host = uri.getHost()
        this.project = pathParts[0]
        this.name = pathParts[1]
        this.path = "${this.project}/${this.name}"
        this.tag = (pathParts.size() == 3) ? pathParts[2] : "latest"
        println "host: ${this.host}, project: ${this.project}, name: ${this.name}, path: ${this.path}, tag: ${this.tag}"
    }

    String getHost() {
        return host
    }

    void setHost(String host) {
        this.host = host
    }

    String getPath() {
        return path
    }

    void setPath(String path) {
        this.path = path
    }

    String getProject() {
        return project
    }

    void setProject(String project) {
        this.project = project
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getTag() {
        return tag
    }

    void setTag(String tag) {
        this.tag = tag
    }

    String host;
    String project;
    String name;
    String tag;
    String path;
}
