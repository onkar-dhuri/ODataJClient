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
package com.msopentech.odatajclient.engine.data.atom;

import com.msopentech.odatajclient.engine.data.EntryResource;
import com.msopentech.odatajclient.engine.data.LinkResource;
import com.msopentech.odatajclient.engine.data.ODataOperation;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * <p>Java class for entryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="entryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{http://www.w3.org/2005/Atom}author"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}category"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}content"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}contributor"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}id"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}link"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}published"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}rights"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}source"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}summary"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}title"/>
 *           &lt;element ref="{http://www.w3.org/2005/Atom}updated"/>
 *         &lt;/choice>
 *         &lt;group ref="{http://www.w3.org/2005/Atom}extensionElement"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/2005/Atom}atomCommonAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entryType", propOrder = {
    "items",
    "anyOther",
    "anyLocal"
})
public class AtomEntry extends AbstractAtomElement implements EntryResource {

    @XmlElementRefs({
        @XmlElementRef(name = "published", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "summary", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "title", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "content", namespace = "http://www.w3.org/2005/Atom", type = AtomContent.class),
        @XmlElementRef(name = "contributor", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "rights", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "category", namespace = "http://www.w3.org/2005/Atom", type = Category.class),
        @XmlElementRef(name = "source", namespace = "http://www.w3.org/2005/Atom", type = Source.class),
        @XmlElementRef(name = "updated", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "author", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class),
        @XmlElementRef(name = "link", namespace = "http://www.w3.org/2005/Atom", type = AtomLink.class),
        @XmlElementRef(name = "id", namespace = "http://www.w3.org/2005/Atom", type = Id.class)
    })
    protected List<Object> items;

    @XmlAnyElement(lax = true)
    protected List<Object> anyOther;

    @XmlAnyElement(lax = true)
    protected List<Object> anyLocal;

    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;

    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the items property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a
     * <CODE>set</CODE> method for the items property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItems().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AtomDate }{@code >}
     * {@link JAXBElement }{@code <}{@link AtomText }{@code >}
     * {@link AtomContent }
     * {@link JAXBElement }{@code <}{@link AtomText }{@code >}
     * {@link JAXBElement }{@code <}{@link AtomText }{@code >}
     * {@link JAXBElement }{@code <}{@link AtomPerson }{@code >}
     * {@link Source }
     * {@link Category }
     * {@link JAXBElement }{@code <}{@link AtomPerson }{@code >}
     * {@link JAXBElement }{@code <}{@link AtomDate }{@code >}
     * {@link AtomLink }
     * {@link Id }
     */
    @Override
    public List<Object> getValues() {
        if (items == null) {
            items = new ArrayList<Object>();
        }
        return this.items;
    }

    /**
     * Gets summary.
     *
     * @return summary.
     */
    public String getSummary() {
        final AtomText value = getTextProperty("summary");
        return value == null || value.getContent().isEmpty() ? null : value.getContent().get(0).toString();
    }

    private AtomPerson getPersonProperty(final String name) {
        final List<AtomPerson> prop = getJAXBElements(name, AtomPerson.class);
        return prop.isEmpty() ? null : prop.get(0);
    }

    /**
     * Gets author.
     *
     * @return author.
     */
    public String getAuthor() {
        final AtomPerson author = getPersonProperty("author");
        return author == null ? null : author.getName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LinkResource getEditLink() {
        AtomLink link = getLinkWithRel(ODataConstants.EDIT_LINK_REL);
        return link == null ? null : link;
    }

    private List<AtomLink> getLinksWithRelPrefix(final String relPrefix) {
        List<AtomLink> relLinks = new ArrayList<AtomLink>();

        for (AtomLink link : getLinks()) {
            if (link.getRel().startsWith(relPrefix)) {
                relLinks.add(link);
            }
        }

        return relLinks;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<? extends LinkResource> getAssociationLinks() {
        return getLinksWithRelPrefix(ODataConstants.ASSOCIATION_LINK_REL);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<? extends LinkResource> getNavigationLinks() {
        return getLinksWithRelPrefix(ODataConstants.NAVIGATION_LINK_REL);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<? extends LinkResource> getMediaEditLinks() {
        return getLinksWithRelPrefix(ODataConstants.MEDIA_EDIT_LINK_REL);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getType() {
        final List<Category> categories = getElements(Category.class);
        return categories.isEmpty() ? null : categories.get(0).getTerm();
    }

    /**
     * Gets source.
     *
     * @return source.
     */
    public Source getSource() {
        final List<Source> sources = getElements(Source.class);
        return sources.isEmpty() ? null : sources.get(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Element getContent() {
        final List<AtomContent> contents = getElements(AtomContent.class);
        return contents.isEmpty() ? null : contents.get(0).getXMLContent();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Element getMediaEntryProperties() {
        List<Element> properties = getAnyOtherElementsByName(ODataConstants.ELEM_PROPERTIES);
        return properties.isEmpty() ? null : properties.get(0);
    }

    /**
     * Gets the value of the anyOther property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a
     * <CODE>set</CODE> method for the anyOther property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnyOther().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     *
     *
     */
    public List<Object> getAnyOther() {
        if (anyOther == null) {
            anyOther = new ArrayList<Object>();
        }
        return this.anyOther;
    }

    private List<Element> getAnyOtherElementsByName(final String nodeName) {
        List<Element> found = new ArrayList<Element>();
        for (Object object : getAnyOther()) {
            if (object instanceof Element) {
                final Element element = (Element) object;
                if (nodeName.equals(element.getNodeName())) {
                    found.add(element);
                }
            }
        }

        return found;
    }

    /**
     * Gets the value of the anyLocal property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a
     * <CODE>set</CODE> method for the anyLocal property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnyLocal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     *
     *
     */
    public List<Object> getAnyLocal() {
        if (anyLocal == null) {
            anyLocal = new ArrayList<Object>();
        }
        return this.anyLocal;
    }

    /**
     * Gets the value of the base property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    public String getBase() {
        return base;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getBaseURI() {
        return base == null ? null : URI.create(base);
    }

    /**
     * Sets the value of the base property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    public void setBase(final String value) {
        this.base = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    public void setLang(final String value) {
        this.lang = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     * always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getEtag() {
        String etag = null;

        final QName qname = new QName(ODataConstants.NS_METADATA, "etag", "m");
        if (getOtherAttributes().containsKey(qname)) {
            etag = getOtherAttributes().get(qname);
        }

        return etag;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setType(final String type) {
        getValues().removeAll(getElements(Category.class));

        final Category category = new Category();
        category.setTerm(type);
        category.setScheme(ODataConstants.NS_SCHEME);
        getValues().add(category);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setId(final String uri) {
        getValues().removeAll(getElements(Id.class));

        final Id id = new Id();
        id.setContent(uri);
        getValues().add(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean setSelfLink(final LinkResource selfLink) {
        final AtomLink link = getLinkWithRel(ODataConstants.SELF_LINK_REL);
        if (link != null) {
            getValues().remove(link);
        }

        return (selfLink instanceof AtomLink) && getValues().add(selfLink);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean setEditLink(final LinkResource editLink) {
        final AtomLink link = getLinkWithRel(ODataConstants.EDIT_LINK_REL);
        if (link != null) {
            getValues().remove(link);
        }

        return (editLink instanceof AtomLink) && getValues().add(editLink);
    }

    private void setLinksWithRelPrefix(final String relPrefix, final List<LinkResource> linkResources) {
        getValues().retainAll(getLinksWithRelPrefix(relPrefix));

        for (LinkResource link : linkResources) {
            if (link instanceof AtomLink) {
                getValues().add((AtomLink) link);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAssociationLink(final LinkResource associationLink) {
        return (associationLink instanceof AtomLink) ? getValues().add(associationLink) : false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAssociationLinks(final List<LinkResource> associationLinks) {
        setLinksWithRelPrefix(ODataConstants.ASSOCIATION_LINK_REL, associationLinks);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addNavigationLink(final LinkResource associationLink) {
        return (associationLink instanceof AtomLink) ? getValues().add(associationLink) : false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setNavigationLinks(final List<LinkResource> navigationLinks) {
        setLinksWithRelPrefix(ODataConstants.NAVIGATION_LINK_REL, navigationLinks);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addMediaEditLink(final LinkResource associationLink) {
        return (associationLink instanceof AtomLink) ? getValues().add(associationLink) : false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMediaEditLinks(final List<LinkResource> mediaEditLinks) {
        setLinksWithRelPrefix(ODataConstants.MEDIA_EDIT_LINK_REL, mediaEditLinks);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ODataOperation> getOperations() {
        final List<ODataOperation> result = new ArrayList<ODataOperation>();

        final List<Element> operations = getAnyOtherElementsByName(ODataConstants.ELEM_ACTION);
        operations.addAll(getAnyOtherElementsByName(ODataConstants.ELEM_FUNCTION));
        for (Element elemOperation : operations) {
            final ODataOperation operation = new ODataOperation();
            operation.setMetadataAnchor(elemOperation.getAttribute(ODataConstants.ATTR_METADATA));
            operation.setTitle(elemOperation.getAttribute(ODataConstants.ATTR_TITLE));
            operation.setTarget(URI.create(elemOperation.getAttribute(ODataConstants.ATTR_TARGET)));

            result.add(operation);
        }

        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setContent(final Element content) {
        final AtomContent atomContent = new AtomContent();
        atomContent.setXMLContent(content);
        getValues().add(atomContent);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMediaEntryProperties(final Element content) {
        final Element old = getMediaEntryProperties();
        if (old != null) {
            getAnyOther().remove(old);
        }

        getAnyOther().add(content);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getMediaContentType() {
        final List<AtomContent> contents = getElements(AtomContent.class);
        return contents.isEmpty() ? null
                : contents.get(0).type == null || contents.get(0).type.isEmpty() ? null : contents.get(0).type.get(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getMediaContentSource() {
        final List<AtomContent> contents = getElements(AtomContent.class);
        return contents.isEmpty() || StringUtils.isBlank(contents.get(0).src) ? null : contents.get(0).src;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMediaContent(final String src, final String type) {
        final AtomContent atomContent = new AtomContent();
        if (StringUtils.isNotBlank(src)) {
            atomContent.setSrc(src);
        } else {
            // it seems to be necessary
            atomContent.setSrc(StringUtils.EMPTY);
        }
        if (StringUtils.isNotBlank(type)) {
            atomContent.getType().add(type);
        }
        getValues().add(atomContent);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isMediaEntry() {
        final List<AtomContent> contents = getElements(AtomContent.class);
        return !contents.isEmpty() && StringUtils.isNotBlank(contents.get(0).src);
    }
}
