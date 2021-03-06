/*
 * Copyright 2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.javaps.description.impl;

import java.util.Objects;
import java.util.Set;

import org.n52.iceland.ogc.ows.OwsCode;
import org.n52.iceland.ogc.ows.OwsKeyword;
import org.n52.iceland.ogc.ows.OwsLanguageString;
import org.n52.iceland.ogc.ows.OwsMetadata;
import org.n52.iceland.ogc.wps.InputOccurence;
import org.n52.iceland.ogc.wps.description.LiteralDataDomain;
import org.n52.iceland.ogc.wps.description.impl.LiteralInputDescriptionImpl;
import org.n52.javaps.description.TypedLiteralInputDescription;
import org.n52.javaps.io.literal.LiteralType;

public class TypedLiteralInputDescriptionImpl
        extends LiteralInputDescriptionImpl
        implements TypedLiteralInputDescription {

    private final LiteralType<?> type;

    public TypedLiteralInputDescriptionImpl(OwsCode id,
                                            OwsLanguageString title,
                                            OwsLanguageString abstrakt,
                                            Set<OwsKeyword> keywords,
                                            Set<OwsMetadata> metadata,
                                            InputOccurence occurence,
                                            LiteralDataDomain defaultLiteralDataDomain,
                                            Set<LiteralDataDomain> supportedLiteralDataDomain,
                                            LiteralType<?> type) {
        super(id, title, abstrakt, keywords, metadata, occurence, defaultLiteralDataDomain, supportedLiteralDataDomain);
        this.type = Objects.requireNonNull(type, "type");
    }

    protected TypedLiteralInputDescriptionImpl(AbstractBuilder<?, ?> builder) {
        this(builder.getId(),
             builder.getTitle(),
             builder.getAbstract(),
             builder.getKeywords(),
             builder.getMetadata(),
             new InputOccurence(builder.getMinimalOccurence(),
                                builder.getMaximalOccurence()),
             builder.getDefaultLiteralDataDomain(),
             builder.getSupportedLiteralDataDomains(),
             builder.getType());
    }

    @Override
    public LiteralType<?> getType() {
        return this.type;
    }

    public static abstract class AbstractBuilder<T extends TypedLiteralInputDescription, B extends AbstractBuilder<T, B>>
            extends LiteralInputDescriptionImpl.AbstractBuilder<T, B>
            implements TypedLiteralInputDescription.Builder<T, B> {

        private LiteralType<?> type;

        @Override
        @SuppressWarnings("unchecked")
        public B withType(LiteralType<?> type) {
            this.type = Objects.requireNonNull(type, "type");
            return (B) this;
        }

        public LiteralType<?> getType() {
            return type;
        }

    }

    public static class Builder extends AbstractBuilder<TypedLiteralInputDescription, Builder> {
        @Override
        public TypedLiteralInputDescription build() {
            return new TypedLiteralInputDescriptionImpl(this);
        }
    }
}
