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
package com.msopentech.odatajclient.proxy.api.impl;

import com.msopentech.odatajclient.engine.communication.header.ODataHeaderValues;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchRequest;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchResponseItem;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataChangeset;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataChangesetResponseItem;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataValueRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataBatchResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataLinkType;
import com.msopentech.odatajclient.engine.uri.ODataURIBuilder;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import com.msopentech.odatajclient.proxy.api.AbstractEntitySet;
import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.api.annotations.CompoundKey;
import com.msopentech.odatajclient.proxy.api.annotations.EntitySet;
import com.msopentech.odatajclient.proxy.api.annotations.EntityType;
import com.msopentech.odatajclient.proxy.api.context.AttachedEntityStatus;
import com.msopentech.odatajclient.proxy.api.context.EntityContext;
import com.msopentech.odatajclient.proxy.api.context.EntityLinkDesc;
import com.msopentech.odatajclient.proxy.api.context.EntityUUID;
import com.msopentech.odatajclient.proxy.api.context.NavigationLinks;
import com.msopentech.odatajclient.proxy.api.query.EntityQuery;
import com.msopentech.odatajclient.proxy.api.query.Query;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EntitySetInvocationHandler<T extends Serializable, KEY extends Serializable> extends AbstractInvocationHandler
        implements AbstractEntitySet<T, KEY> {

    private static final long serialVersionUID = 2629912294765040027L;

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(EntitySetInvocationHandler.class);

    private final Class<T> typeRef;

    private final Class<KEY> keyRef;

    private final String entitySetName;

    private final EntityContainerInvocationHandler container;

    private final URI uri;

    static <T extends Serializable, KEY extends Serializable> EntitySetInvocationHandler<T, KEY> getInstance(
            final Class<?> ref,
            final EntityContainerInvocationHandler container) {

        return new EntitySetInvocationHandler<T, KEY>(ref, container);
    }

    @SuppressWarnings("unchecked")
    private EntitySetInvocationHandler(
            final Class<?> ref,
            final EntityContainerInvocationHandler container) {

        super(container.getFactory());

        this.container = container;

        final Annotation annotation = ref.getAnnotation(EntitySet.class);
        if (!(annotation instanceof EntitySet)) {
            throw new IllegalArgumentException("Return type " + ref.getName()
                    + " is not annotated as @" + EntitySet.class.getSimpleName());
        }

        this.entitySetName = ((EntitySet) annotation).name();

        final Type[] abstractEntitySetParams =
                ((ParameterizedType) ref.getGenericInterfaces()[0]).getActualTypeArguments();

        this.typeRef = (Class<T>) abstractEntitySetParams[0];

        if (typeRef.getAnnotation(EntityType.class) == null) {
            throw new IllegalArgumentException("Invalid entity '" + typeRef.getSimpleName() + "'");
        }

        this.keyRef = (Class<KEY>) abstractEntitySetParams[1];

        final ODataURIBuilder uriBuilder = new ODataURIBuilder(container.getFactory().getServiceRoot());

        if (!container.isDefaultEntityContainer()) {
            uriBuilder.appendStructuralSegment(container.getEntityContainerName()).appendStructuralSegment(".");
        }

        uriBuilder.appendEntitySetSegment(entitySetName);
        this.uri = uriBuilder.build();
    }

    public Class<T> getTypeRef() {
        return typeRef;
    }

    public Class<KEY> getKeyRef() {
        return keyRef;
    }

    public String getEntitySetName() {
        return entitySetName;
    }

    public EntityContainerInvocationHandler getContainer() {
        return container;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if ("delete".equals(method.getName())) {
            if (args[0] instanceof Collection) {
                delete((Collection<T>) args[0]);
            } else {
                delete((KEY) args[0]);
            }
        } else if ("count".equals(method.getName())) {
            return count();
        } else if ("exists".equals(method.getName())) {
            return exists((KEY) args[0]);
        } else if ("get".equals(method.getName())) {
            return get((KEY) args[0]);
        } else if ("getAll".equals(method.getName())) {
            return getAll();
        } else if ("newEntity".equals(method.getName())) {
            return newEntity();
        } else if ("flush".equals(method.getName())) {
            flush();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        final Constructor<Void> voidConstructor = Void.class.getDeclaredConstructor();
        voidConstructor.setAccessible(true);
        return voidConstructor.newInstance();
    }

    @Override
    public Long count() {
        final ODataValueRequest req = ODataRetrieveRequestFactory.
                getValueRequest(new ODataURIBuilder(this.uri.toASCIIString()).appendCountSegment().build());
        req.setFormat(ODataValueFormat.TEXT);
        return Long.valueOf(req.execute().getBody().asPrimitive().toString());
    }

    @Override
    public Boolean exists(final KEY key) throws IllegalArgumentException {
        boolean result = false;

        try {
            result = get(key) != null;
        } catch (Exception e) {
            LOG.error("Could not check existence of {}({})", this.entitySetName, key, e);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(final KEY key) throws IllegalArgumentException {

        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }

        final EntityUUID uuid = new EntityUUID(
                Utility.getNamespace(typeRef),
                container.getEntityContainerName(),
                entitySetName,
                Utility.getNamespace(typeRef) + "." + Utility.getEntityTypeName(typeRef),
                key);

        EntityTypeInvocationHandler handler =
                EntityContainerFactory.getContext().entityContext().getEntity(uuid);

        if (handler == null) {
            // not yet attached: search against the service
            try {
                final ODataURIBuilder uriBuilder = new ODataURIBuilder(this.uri.toASCIIString());

                if (key.getClass().getAnnotation(CompoundKey.class) == null) {
                    uriBuilder.appendKeySegment(key);
                } else {
                    uriBuilder.appendKeySegment(getCompoundKey(key));
                }

                handler = EntityTypeInvocationHandler.getInstance(
                        ODataRetrieveRequestFactory.getEntityRequest(uriBuilder.build()).execute().getBody(), this);

            } catch (Exception e) {
                LOG.info("Entity '" + uuid + "' not found", e);
            }
        } else if (isDeleted(handler)) {
            // object deleted
            handler = null;
        }

        return handler == null ? null : (T) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class<?>[] {typeRef},
                handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<T> getAll() {
        final ODataEntitySet entitySet = ODataRetrieveRequestFactory.getEntitySetRequest(this.uri).execute().getBody();

        final List<T> beans = new ArrayList<T>(entitySet.getEntities().size());
        for (ODataEntity entity : entitySet.getEntities()) {
            final EntityTypeInvocationHandler handler = EntityTypeInvocationHandler.getInstance(entity, this);

            EntityTypeInvocationHandler handlerInTheContext =
                    EntityContainerFactory.getContext().entityContext().getEntity(handler.getUUID());

            if (handlerInTheContext == null) {
                beans.add((T) Proxy.newProxyInstance(
                        this.getClass().getClassLoader(),
                        new Class<?>[] {typeRef},
                        handler));
            } else if (!isDeleted(handlerInTheContext)) {
                beans.add((T) Proxy.newProxyInstance(
                        this.getClass().getClassLoader(),
                        new Class<?>[] {typeRef},
                        handlerInTheContext));
            }
        }

        return beans;
    }

    @Override
    public Query createQuery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <E extends Serializable> EntityQuery<E> createQuery(final Class<E> entityClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(final KEY key) throws IllegalArgumentException {
        final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

        EntityTypeInvocationHandler entity = entityContext.getEntity(new EntityUUID(
                Utility.getNamespace(typeRef),
                container.getEntityContainerName(),
                entitySetName,
                Utility.getEntityTypeName(typeRef),
                key));

        if (entity == null) {
            // search for entity
            final T searched = get(key);
            entity = (EntityTypeInvocationHandler) Proxy.getInvocationHandler(searched);
            entityContext.attach(entity, AttachedEntityStatus.DELETED);
        } else {
            entityContext.setStatus(entity, AttachedEntityStatus.DELETED);
        }
    }

    @Override
    public void delete(final Iterable<T> entities) {
        final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

        for (T en : entities) {
            final EntityTypeInvocationHandler entity = (EntityTypeInvocationHandler) Proxy.getInvocationHandler(en);
            if (entityContext.isAttached(entity)) {
                entityContext.setStatus(entity, AttachedEntityStatus.DELETED);
            } else {
                entityContext.attach(entity, AttachedEntityStatus.DELETED);
            }
        }
    }

    /**
     * Transactional changes commit.
     */
    @Override
    public void flush() {
        final ODataBatchRequest request = ODataBatchRequestFactory.getBatchRequest(getFactory().getServiceRoot());
        final ODataBatchRequest.BatchStreamManager streamManager = request.execute();
        final ODataChangeset changeset = streamManager.addChangeset();

        final TransactionItems items = new TransactionItems();
        final List<EntityLinkDesc> delayedUpdates = new ArrayList<EntityLinkDesc>();

        int pos = 1;

        for (AttachedEntity attachedEntity : EntityContainerFactory.getContext().entityContext()) {
            final AttachedEntityStatus status = attachedEntity.getStatus();
            if (status != AttachedEntityStatus.ATTACHED
                    && status != AttachedEntityStatus.LINKED
                    && !items.contains(attachedEntity.getEntity())) {
                pos += processEntityContext(attachedEntity.getEntity(), pos, items, delayedUpdates, changeset);
            }
        }

        int lastPos = items.size();
        for (EntityLinkDesc delayedUpdate : delayedUpdates) {
            lastPos++;
            items.put(delayedUpdate.getSource(), lastPos);

            final ODataEntity changes = ODataFactory.newEntity(delayedUpdate.getSource().getEntity().getName());

            int sourcePos = items.get(delayedUpdate.getSource());

            for (EntityTypeInvocationHandler target : delayedUpdate.getTargets()) {
                int targetPos = items.get(target);

                LOG.debug("'{}' from ${} to ${}", new Object[] {
                    delayedUpdate.getType().name(), sourcePos, targetPos});

                final URI targetURI = URI.create("$" + targetPos);

                changes.addLink(delayedUpdate.getType() == ODataLinkType.ENTITY_NAVIGATION ? ODataFactory.
                        newEntityNavigationLink(delayedUpdate.getSourceName(), targetURI)
                        : ODataFactory.newFeedNavigationLink(delayedUpdate.getSourceName(), targetURI));
            }

            batchUpdate(URI.create("$" + sourcePos), changes, changeset);
        }

        final ODataBatchResponse response = streamManager.getResponse();

        if (response.getStatusCode() != 202) {
            throw new IllegalStateException("Operation failed");
        }

        final Iterator<ODataBatchResponseItem> iter = response.getBody();

        if (!iter.hasNext()) {
            throw new IllegalStateException("Unexpected operation");
        }

        final ODataBatchResponseItem item = iter.next();
        if (!(item instanceof ODataChangesetResponseItem)) {
            throw new IllegalStateException("Unexpected batch response item " + item.getClass().getSimpleName());
        }

        final ODataChangesetResponseItem chgres = (ODataChangesetResponseItem) item;

        for (Integer changesetItemId : items.sortedValues()) {
            LOG.debug("Expected changeset item {}", changesetItemId);
            final ODataResponse res = chgres.next();
            if (res.getStatusCode() >= 400) {
                throw new IllegalStateException("Transaction failed: " + res.getStatusMessage());
            }

            final EntityTypeInvocationHandler handler = items.get(changesetItemId);

            if (res instanceof ODataEntityCreateResponse) {
                LOG.debug("Upgrade created object '{}'", handler);
                handler.setEntity(((ODataEntityCreateResponse) res).getBody());
            } else if (res instanceof ODataEntityUpdateResponse) {
                LOG.debug("Upgrade updated object '{}'", handler);
                handler.setEntity(((ODataEntityUpdateResponse) res).getBody());
            }
        }

        EntityContainerFactory.getContext().detachAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newEntity() {
        final ODataEntity entity =
                ODataFactory.newEntity(container.getSchemaName() + "." + Utility.getEntityTypeName(typeRef));

        final EntityTypeInvocationHandler handler = EntityTypeInvocationHandler.getInstance(entity, this);
        EntityContainerFactory.getContext().entityContext().attachNew(handler);

        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] {typeRef}, handler);
    }

    private boolean isDeleted(final EntityTypeInvocationHandler handler) {
        return EntityContainerFactory.getContext().entityContext().getStatus(handler) == AttachedEntityStatus.DELETED;
    }

    private void batchCreate(
            final EntityTypeInvocationHandler handler, final ODataEntity entity, final ODataChangeset changeset) {
        LOG.debug("Create '{}'", handler);
        final ODataURIBuilder uriBuilder = new ODataURIBuilder(handler.getFactory().getServiceRoot());
        uriBuilder.appendEntitySetSegment(handler.getEntitySetName());
        changeset.addRequest(ODataCUDRequestFactory.getEntityCreateRequest(uriBuilder.build(), entity));
    }

    private void batchUpdate(final ODataEntity changes, final ODataChangeset changeset) {
        LOG.debug("Update '{}'", changes.getEditLink());
        final ODataEntityUpdateRequest req =
                ODataCUDRequestFactory.getEntityUpdateRequest(UpdateType.REPLACE, changes);
        req.setPrefer(ODataHeaderValues.preferReturnContent);
        changeset.addRequest(req);
    }

    private void batchUpdate(final URI uri, final ODataEntity changes, final ODataChangeset changeset) {
        LOG.debug("Update '{}'", uri);
        final ODataEntityUpdateRequest req =
                ODataCUDRequestFactory.getEntityUpdateRequest(uri, UpdateType.PATCH, changes);
        req.setPrefer(ODataHeaderValues.preferReturnContent);
        changeset.addRequest(req);
    }

    private void batchDelete(
            final EntityTypeInvocationHandler handler, final ODataEntity entity, final ODataChangeset changeset) {
        LOG.debug("Delete '{}'", entity.getEditLink());
        changeset.addRequest(ODataCUDRequestFactory.getDeleteRequest(URIUtils.getURI(
                handler.getFactory().getServiceRoot(), entity.getEditLink().toASCIIString())));
    }

    private int processEntityContext(
            final EntityTypeInvocationHandler handler,
            int pos,
            final TransactionItems items,
            final List<EntityLinkDesc> delayedUpdates,
            final ODataChangeset changeset) {

        LOG.debug("Process '{}'", handler);

        items.put(handler, null);

        final ODataEntity entity = SerializationUtils.clone(handler.getEntity());
        populate(handler.getChanges(), entity);

        final NavigationLinks links = EntityContainerFactory.getContext().linkContext().getLinkedEntities(handler);

        for (String name : links.getLinkNames()) {
            final Set<EntityTypeInvocationHandler> toBeLinked = new HashSet<EntityTypeInvocationHandler>();
            final ODataLinkType type = links.getLinkType(name);

            final String serviceRoot = getContainer().getFactory().getServiceRoot();

            for (EntityTypeInvocationHandler target : links.getLinkedEntities(name)) {
                final AttachedEntityStatus status =
                        EntityContainerFactory.getContext().entityContext().getStatus(target);

                final URI editLink = target.getEntity().getEditLink();

                if (status == AttachedEntityStatus.ATTACHED || status == AttachedEntityStatus.LINKED) {
                    entity.addLink(buildNavigationLink(
                            name, URIUtils.getURI(serviceRoot, editLink.toASCIIString()), type));
                } else {
                    if (!items.contains(target)) {
                        pos = processEntityContext(target, pos, items, delayedUpdates, changeset);
                        pos++;
                    }

                    if (status == AttachedEntityStatus.CHANGED) {
                        entity.addLink(buildNavigationLink(
                                name, URIUtils.getURI(serviceRoot, editLink.toASCIIString()), type));
                    } else {
                        final Integer targetPos = items.get(target);
                        if (targetPos == null) {
                            // schedule update for the current object
                            LOG.debug("Schedule '{}' from '{}' to '{}'", new Object[] {type.name(), handler, target});
                            toBeLinked.add(target);
                        } else {
                            // create the link for the current object
                            LOG.debug("'{}' from '{}' to (${}) '{}'",
                                    new Object[] {type.name(), handler, targetPos, target});

                            entity.addLink(buildNavigationLink(name, URI.create("$" + targetPos), type));

                        }
                    }
                }
            }

            if (!toBeLinked.isEmpty()) {
                delayedUpdates.add(new EntityLinkDesc(name, handler, toBeLinked, type));
            }
        }

        // insert into the batch
        LOG.debug("{}: Insert '{}' into the batch", pos, handler);
        batch(handler, entity, changeset);

        items.put(handler, pos);

        return pos;
    }

    private ODataLink buildNavigationLink(final String name, final URI uri, final ODataLinkType type) {
        switch (type) {
            case ENTITY_NAVIGATION:
                return ODataFactory.newEntityNavigationLink(name, uri);
            case ENTITY_SET_NAVIGATION:
                return ODataFactory.newFeedNavigationLink(name, uri);
            default:
                throw new IllegalArgumentException("Invalid link type " + type.name());
        }
    }

    private void batch(
            final EntityTypeInvocationHandler handler, final ODataEntity entity, final ODataChangeset changeset) {
        switch (EntityContainerFactory.getContext().entityContext().getStatus(handler)) {
            case NEW:
                batchCreate(handler, entity, changeset);
                break;
            case CHANGED:
                batchUpdate(entity, changeset);
                break;
            case DELETED:
                batchDelete(handler, entity, changeset);
                break;
            default:
            // ignore
        }
    }

    private class TransactionItems {

        private final List<EntityTypeInvocationHandler> keys = new ArrayList<EntityTypeInvocationHandler>();

        private final List<Integer> values = new ArrayList<Integer>();

        public EntityTypeInvocationHandler get(final Integer value) {
            if (value != null && values.contains(value)) {
                return keys.get(values.indexOf(value));
            } else {
                return null;
            }
        }

        public Integer get(final EntityTypeInvocationHandler key) {
            if (key != null && keys.contains(key)) {
                return values.get(keys.indexOf(key));
            } else {
                return null;
            }
        }

        public void remove(final EntityTypeInvocationHandler key) {
            if (keys.contains(key)) {
                values.remove(keys.indexOf(key));
                keys.remove(key);
            }
        }

        public void put(final EntityTypeInvocationHandler key, final Integer value) {
            // replace just in case of null current value; otherwise add the new entry
            if (keys.contains(key) && values.get(keys.indexOf(key)) == null) {
                remove(key);
            }
            keys.add(key);
            values.add(value);
        }

        public List<Integer> sortedValues() {
            final List<Integer> sortedValues = new ArrayList<Integer>(values);
            Collections.<Integer>sort(sortedValues);
            return sortedValues;
        }

        public boolean contains(final EntityTypeInvocationHandler key) {
            return keys.contains(key);
        }

        public int size() {
            return keys.size();
        }
    }
}
