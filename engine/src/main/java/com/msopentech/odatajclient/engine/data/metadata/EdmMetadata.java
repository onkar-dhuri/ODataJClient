/*
 * Copyright 2013 MS OpenTech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msopentech.odatajclient.engine.data.metadata;

import com.msopentech.odatajclient.engine.data.metadata.edm.Schema;
import com.msopentech.odatajclient.engine.data.metadata.edmx.DataServices;
import com.msopentech.odatajclient.engine.data.metadata.edmx.Edmx;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;

/**
 * Entry point for access information about EDM metadata.
 */
public class EdmMetadata implements Serializable {

    private static final long serialVersionUID = -1214173426671503187L;

    private final DataServices dataservices;

    private final Map<String, Schema> schemaByNsOrAlias;

    /**
     * Constructor.
     *
     * @param inputStream source stream.
     */
    @SuppressWarnings("unchecked")
    public EdmMetadata(final InputStream inputStream) {
        final Edmx edmx;
        try {
            final JAXBContext context = JAXBContext.newInstance(Edmx.class);
            edmx = ((JAXBElement<Edmx>) context.createUnmarshaller().unmarshal(inputStream)).getValue();
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Could not parse as Edmx document", e);
        }

        DataServices ds = null;
        for (JAXBElement<?> edmxContent : edmx.getContent()) {
            if (DataServices.class.equals(edmxContent.getDeclaredType())) {
                ds = (DataServices) edmxContent.getValue();
            }
        }
        if (ds == null) {
            throw new IllegalArgumentException("No <DataServices/> element found");
        }
        this.dataservices = ds;

        this.schemaByNsOrAlias = new HashMap<String, Schema>();
        for (Schema schema : this.dataservices.getSchema()) {
            this.schemaByNsOrAlias.put(schema.getNamespace(), schema);
            if (StringUtils.isNotBlank(schema.getAlias())) {
                this.schemaByNsOrAlias.put(schema.getAlias(), schema);
            }
        }
    }

    /**
     * Checks whether the given key is a valid namespace or alias in the EdM metadata document.
     *
     * @param key namespace or alias
     * @return true if key is valid namespace or alias
     */
    public boolean isNsOrAlias(final String key) {
        return this.schemaByNsOrAlias.keySet().contains(key);
    }

    /**
     * Returns the Schema at the specified position in the EdM metadata document.
     *
     * @param index index of the Schema to return
     * @return the Schema at the specified position in the EdM metadata document
     */
    public Schema getSchema(final int index) {
        return this.dataservices.getSchema().get(index);
    }

    /**
     * Returns the Schema with the specified key (namespace or alias) in the EdM metadata document.
     *
     * @param key namespace or alias
     * @return the Schema with the specified key in the EdM metadata document
     */
    public Schema getSchema(final String key) {
        return this.schemaByNsOrAlias.get(key);
    }

    /**
     * Returns all Schema objects defined in the EdM metadata document.
     *
     * @return all Schema objects defined in the EdM metadata document
     */
    public List<Schema> getSchemas() {
        return this.dataservices.getSchema();
    }
}
