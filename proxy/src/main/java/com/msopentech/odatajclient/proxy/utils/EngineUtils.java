/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy.utils;

import static com.msopentech.odatajclient.engine.data.ODataLinkType.ENTITY_NAVIGATION;
import static com.msopentech.odatajclient.engine.data.ODataLinkType.ENTITY_SET_NAVIGATION;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataGeospatialValue;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataLinkType;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.data.metadata.EdmMetadata;
import com.msopentech.odatajclient.engine.data.metadata.EdmType;
import com.msopentech.odatajclient.engine.data.metadata.edm.Association;
import com.msopentech.odatajclient.engine.data.metadata.edm.AssociationSet;
import com.msopentech.odatajclient.engine.data.metadata.edm.AssociationSetEnd;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.data.metadata.edm.EntityContainer;
import com.msopentech.odatajclient.engine.data.metadata.edm.Schema;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.proxy.api.AbstractComplexType;
import com.msopentech.odatajclient.proxy.api.annotations.ComplexType;
import com.msopentech.odatajclient.proxy.api.annotations.CompoundKeyElement;
import com.msopentech.odatajclient.proxy.api.annotations.Key;
import com.msopentech.odatajclient.proxy.api.annotations.NavigationProperty;
import com.msopentech.odatajclient.proxy.api.annotations.Property;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EngineUtils {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(EngineUtils.class);

    private EngineUtils() {
        // Empty private constructor for static utility classes
    }

    public static Map.Entry<EntityContainer, AssociationSet> getAssociationSet(
            final Association association, final String associationNamespace, final EdmMetadata metadata) {

        final StringBuilder associationName = new StringBuilder();
        associationName.append(associationNamespace).append('.').append(association.getName());

        for (Schema schema : metadata.getSchemas()) {
            for (EntityContainer container : schema.getEntityContainers()) {
                final AssociationSet associationSet = getAssociationSet(associationName.toString(),
                        container);
                if (associationSet != null) {
                    return new AbstractMap.SimpleEntry<EntityContainer, AssociationSet>(container, associationSet);
                }
            }
        }

        throw new IllegalStateException("Association set not found");
    }

    public static Association getAssociation(final Schema schema, final String relationship) {
        return schema.getAssociation(relationship.substring(relationship.lastIndexOf('.') + 1));
    }

    public static AssociationSet getAssociationSet(final String association,
            final EntityContainer container) {
        LOG.debug("Search for association set {}", association);

        for (AssociationSet associationSet : container.getAssociationSets()) {
            LOG.debug("Retrieved association set '{}:{}'", associationSet.getName(), associationSet.getAssociation());
            if (associationSet.getAssociation().equals(association)) {
                return associationSet;
            }
        }

        return null;
    }

    public static String getEntitySetName(final AssociationSet associationSet, final String role) {
        for (AssociationSetEnd end : associationSet.getEnds()) {
            if (end.getRole().equals(role)) {
                return end.getEntitySet();
            }
        }
        return null;
    }

    public static NavigationProperty getNavigationProperty(final Class<?> entityTypeRef, final String relationship) {
        NavigationProperty res = null;
        final Method[] methods = entityTypeRef.getClass().getDeclaredMethods();

        for (int i = 0; i < methods.length && res == null; i++) {
            final Annotation ann = methods[i].getAnnotation(NavigationProperty.class);
            if ((ann instanceof NavigationProperty)
                    && ((NavigationProperty) ann).relationship().equalsIgnoreCase(relationship)) {
                res = (NavigationProperty) ann;
            }
        }

        return res;
    }

    public static ODataLink getNavigationLink(final String name, final ODataEntity entity) {
        ODataLink res = null;
        final List<ODataLink> links = entity.getNavigationLinks();

        for (int i = 0; i < links.size() && res == null; i++) {
            if (links.get(i).getName().equalsIgnoreCase(name)) {
                res = links.get(i);
            }
        }
        return res;
    }

    public static ODataLink getNavigationLink(final String name, final URI uri, final ODataLinkType type) {
        switch (type) {
            case ENTITY_NAVIGATION:
                return ODataFactory.newEntityNavigationLink(name, uri);

            case ENTITY_SET_NAVIGATION:
                return ODataFactory.newFeedNavigationLink(name, uri);

            default:
                throw new IllegalArgumentException("Invalid link type " + type.name());
        }
    }

    public static ODataValue getODataValue(final EdmMetadata metadata, final EdmType type, final Object obj) {
        final ODataValue value;

        if (type.isCollection()) {
            value = new ODataCollectionValue(type.getTypeExpression());
            final EdmType intType = new EdmType(metadata, type.getBaseType());
            for (Object collectionItem : (Collection) obj) {
                if (intType.isSimpleType()) {
                    ((ODataCollectionValue) value).add(getODataValue(metadata, intType, collectionItem).asPrimitive());
                } else if (intType.isComplexType()) {
                    ((ODataCollectionValue) value).add(getODataValue(metadata, intType, collectionItem).asComplex());
                } else if (intType.isEnumType()) {
                    // TODO: manage enum types
                    throw new UnsupportedOperationException("Usupported enum type " + intType.getTypeExpression());
                } else {
                    throw new UnsupportedOperationException("Usupported object type " + intType.getTypeExpression());
                }
            }
        } else if (type.isComplexType()) {
            value = new ODataComplexValue(type.getBaseType());
            if (obj.getClass().isAnnotationPresent(ComplexType.class)) {
                for (Method method : obj.getClass().getMethods()) {
                    final Property complexPropertyAnn = method.getAnnotation(Property.class);
                    try {
                        if (complexPropertyAnn != null) {
                            value.asComplex().add(
                                    getODataProperty(metadata, complexPropertyAnn.name(), method.invoke(obj)));
                        }
                    } catch (Exception ignore) {
                        // ignore value
                        LOG.warn("Error attaching complex field '{}'", complexPropertyAnn.name(), ignore);
                    }
                }
            } else {
                throw new IllegalArgumentException(
                        "Object '" + obj.getClass().getSimpleName() + "' is not a complex value");
            }
        } else if (type.isEnumType()) {
            // TODO: manage enum types
            throw new UnsupportedOperationException("Usupported enum type " + type.getTypeExpression());
        } else {
            final EdmSimpleType simpleType = EdmSimpleType.fromValue(type.getTypeExpression());
            if (simpleType.isGeospatial()) {
                value = new ODataGeospatialValue.Builder().setValue((Geospatial) obj).setType(simpleType).build();
            } else {
                value = new ODataPrimitiveValue.Builder().setValue(obj).setType(simpleType).build();
            }
        }

        return value;
    }

    private static ODataProperty getODataProperty(final EdmMetadata metadata, final String name, final Object obj) {
        final ODataProperty oprop;

        final EdmType type = getEdmType(metadata, obj);
        try {
            if (type == null || obj == null) {
                oprop = ODataFactory.newPrimitiveProperty(name, null);
            } else if (type.isCollection()) {
                // create collection property
                oprop = ODataFactory.newCollectionProperty(
                        name, getODataValue(metadata, type, obj).asCollection());
            } else if (type.isSimpleType()) {
                // create a primitive property
                oprop = ODataFactory.newPrimitiveProperty(
                        name, getODataValue(metadata, type, obj).asPrimitive());
            } else if (type.isComplexType()) {
                // create a complex property
                oprop = ODataFactory.newComplexProperty(
                        name, getODataValue(metadata, type, obj).asComplex());
            } else if (type.isEnumType()) {
                // TODO: manage enum types
                throw new UnsupportedOperationException("Usupported enum type " + type.getTypeExpression());
            } else {
                throw new UnsupportedOperationException("Usupported object type " + type.getTypeExpression());
            }

            return oprop;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void addProperties(
            final EdmMetadata metadata, final Map<String, Object> changes, final ODataEntity entity) {

        for (Map.Entry<String, Object> property : changes.entrySet()) {
            // if the getter exists and it is annotated as expected then get value/value and add a new property
            final ODataProperty odataProperty = entity.getProperty(property.getKey());
            if (odataProperty != null) {
                entity.removeProperty(odataProperty);
            }

            entity.addProperty(getODataProperty(metadata, property.getKey(), property.getValue()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setPropertyValue(final Object bean, final Method getter, final Object value)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // Assumption: setter is always prefixed by 'set' word
        final String setterName = getter.getName().replaceFirst("get", "set");
        bean.getClass().getMethod(setterName, getter.getReturnType()).invoke(bean, value);
    }

    public static Object getKey(
            final EdmMetadata metadata, final Class<?> entityTypeRef, final ODataEntity entity) {
        final Object res;

        if (entity.getProperties().isEmpty()) {
            res = null;
        } else {
            final Class<?> keyRef = ClassUtils.getCompoundKeyRef(entityTypeRef);
            if (keyRef == null) {
                final ODataProperty property = entity.getProperty(firstValidEntityKey(entityTypeRef));
                res = property == null || !property.hasPrimitiveValue()
                        ? null
                        : property.getPrimitiveValue().toValue();

            } else {
                try {
                    res = keyRef.newInstance();
                    populate(metadata, res, CompoundKeyElement.class, entity.getProperties().iterator());
                } catch (Exception e) {
                    LOG.error("Error population compound key {}", keyRef.getSimpleName(), e);
                    throw new IllegalArgumentException("Cannot populate compound key");
                }
            }
        }

        return res;
    }

    @SuppressWarnings("unchecked")
    public static void populate(
            final EdmMetadata metadata,
            final Object bean,
            final Class<? extends Annotation> getterAnn,
            final Iterator<ODataProperty> propItor) {

        if (bean != null) {
            while (propItor.hasNext()) {
                final ODataProperty property = propItor.next();

                final Method getter =
                        ClassUtils.findGetterByAnnotatedName(bean.getClass(), getterAnn, property.getName());

                if (getter == null) {
                    LOG.warn("Could not find any property annotated as {} in {}",
                            property.getName(), bean.getClass().getName());
                } else {
                    try {
                        if (property.hasNullValue()) {
                            setPropertyValue(bean, getter, null);
                        }
                        if (property.hasPrimitiveValue()) {
                            setPropertyValue(bean, getter, property.getPrimitiveValue().toValue());
                        }
                        if (property.hasComplexValue()) {
                            final Object complex = getter.getReturnType().newInstance();
                            populate(metadata, complex, Property.class, property.getComplexValue().iterator());
                            setPropertyValue(bean, getter, complex);
                        }
                        if (property.hasCollectionValue()) {
                            final ParameterizedType collType = (ParameterizedType) getter.getGenericReturnType();
                            final Class<?> collItemClass = (Class<?>) collType.getActualTypeArguments()[0];

                            Collection collection = (Collection) getter.invoke(bean);
                            if (collection == null) {
                                collection = new ArrayList();
                                setPropertyValue(bean, getter, collection);
                            }

                            final Iterator<ODataValue> collPropItor = property.getCollectionValue().iterator();
                            while (collPropItor.hasNext()) {
                                final ODataValue value = collPropItor.next();
                                if (value.isPrimitive()) {
                                    collection.add(value.asPrimitive().toValue());
                                }
                                if (value.isComplex()) {
                                    final Object collItem = collItemClass.newInstance();
                                    populate(metadata, collItem, Property.class, value.asComplex().iterator());
                                    collection.add(collItem);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Could not set property {} on {}", getter, bean, e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getValueFromProperty(final EdmMetadata metadata, final ODataProperty property)
            throws InstantiationException, IllegalAccessException {

        final Object value;

        if (property == null || property.hasNullValue()) {
            value = null;
        } else if (property.hasCollectionValue()) {
            value = new ArrayList();

            final Iterator<ODataValue> collPropItor = property.getCollectionValue().iterator();
            while (collPropItor.hasNext()) {
                final ODataValue odataValue = collPropItor.next();
                if (odataValue.isPrimitive()) {
                    ((Collection) value).add(odataValue.asPrimitive().toValue());
                }
                if (odataValue.isComplex()) {
                    final Object collItem =
                            buildComplexInstance(metadata, property.getName(), odataValue.asComplex().iterator());
                    ((Collection) value).add(collItem);
                }
            }
        } else if (property.hasPrimitiveValue()) {
            value = property.getPrimitiveValue().toValue();
        } else if (property.hasComplexValue()) {
            value = buildComplexInstance(metadata, property.getComplexValue().getTypeName(), property.getComplexValue().
                    iterator());
        } else {
            throw new IllegalArgumentException("Invalid property " + property);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private static <C extends AbstractComplexType> C buildComplexInstance(
            final EdmMetadata metadata, final String name, final Iterator<ODataProperty> properties) {

        for (C complex : (Iterable<C>) ServiceLoader.load(AbstractComplexType.class)) {
            final ComplexType ann = complex.getClass().getAnnotation(ComplexType.class);
            final String fn = ann == null ? null : ClassUtils.getNamespace(complex.getClass()) + "." + ann.value();

            if (name.equals(fn)) {
                populate(metadata, complex, Property.class, properties);
                return complex;
            }
        }

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getValueFromProperty(
            final EdmMetadata metadata, final ODataProperty property, final Type type)
            throws InstantiationException, IllegalAccessException {

        final Object value;

        if (property == null || property.hasNullValue()) {
            value = null;
        } else if (property.hasCollectionValue()) {
            value = new ArrayList();

            final ParameterizedType collType = (ParameterizedType) type;
            final Class<?> collItemClass = (Class<?>) collType.getActualTypeArguments()[0];

            final Iterator<ODataValue> collPropItor = property.getCollectionValue().iterator();
            while (collPropItor.hasNext()) {
                final ODataValue odataValue = collPropItor.next();
                if (odataValue.isPrimitive()) {
                    ((Collection) value).add(odataValue.asPrimitive().toValue());
                }
                if (odataValue.isComplex()) {
                    final Object collItem = collItemClass.newInstance();
                    populate(metadata, collItem, Property.class, odataValue.asComplex().iterator());
                    ((Collection) value).add(collItem);
                }
            }
        } else if (property.hasPrimitiveValue()) {
            value = property.getPrimitiveValue().toValue();
        } else if (property.hasComplexValue()) {
            value = ((Class<?>) type).newInstance();
            populate(metadata, value, Property.class, property.getComplexValue().iterator());
        } else {
            throw new IllegalArgumentException("Invalid property " + property);
        }

        return value;
    }

    private static String firstValidEntityKey(final Class<?> entityTypeRef) {
        for (Method method : entityTypeRef.getDeclaredMethods()) {
            if (method.getAnnotation(Key.class) != null) {
                final Annotation ann = method.getAnnotation(Property.class);
                if (ann != null) {
                    return ((Property) ann).name();
                }
            }
        }
        return null;
    }

    private static EdmType getEdmType(final EdmMetadata metadata, final Object obj) {
        final EdmType res;

        if (obj == null) {
            res = null;
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            if (((Collection) obj).isEmpty()) {
                res = new EdmType(metadata, "Collection(" + getEdmType(metadata, "Edm.String"));
            } else {
                res = new EdmType(metadata, "Collection("
                        + getEdmType(metadata, ((Collection) obj).iterator().next()).getTypeExpression()
                        + ")");
            }
        } else if (obj.getClass().isAnnotationPresent(ComplexType.class)) {
            final String ns = ClassUtils.getNamespace(obj.getClass());
            final ComplexType ann = obj.getClass().getAnnotation(ComplexType.class);
            res = new EdmType(metadata, ns + "." + ann.value());
        } else {
            final EdmSimpleType simpleType = EdmSimpleType.fromObject(obj);
            res = new EdmType(metadata, simpleType.toString());
        }

        return res;
    }

    public static URI getEditMediaLink(final String name, final ODataEntity entity) {
        for (ODataLink editMediaLink : entity.getEditMediaLinks()) {
            if (name.equalsIgnoreCase(editMediaLink.getName())) {
                return editMediaLink.getLink();
            }
        }

        throw new IllegalArgumentException("Invalid streamed property " + name);
    }
}
