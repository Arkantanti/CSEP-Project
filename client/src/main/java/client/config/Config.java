package client.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Config {

    private String serverUrl = "http://localhost:8080/"; //Default value

    /**
     * No argument constructor used by Jackson.
     */
    protected Config() {
        // for object mapper
    }

    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
