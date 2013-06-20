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

import com.msopentech.odatajclient.engine.data.LinkResource;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2005/Atom}undefinedContent">
 *       &lt;attGroup ref="{http://www.w3.org/2005/Atom}atomCommonAttributes"/>
 *       &lt;attribute name="href" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="rel" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2005/Atom}atomMediaType" />
 *       &lt;attribute name="hreflang" type="{http://www.w3.org/2005/Atom}atomLanguageTag" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "link")
public class AtomLink extends UndefinedContent implements LinkResource {

    @XmlAttribute(name = "href", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String href;

    @XmlAttribute(name = "rel")
    @XmlSchemaType(name = "anySimpleType")
    protected String rel;

    @XmlAttribute(name = "type")
    protected String type;

    @XmlAttribute(name = "hreflang")
    protected String hreflang;

    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    protected String title;

    @XmlAttribute(name = "length")
    @XmlSchemaType(name = "anySimpleType")
    protected String length;

    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;

    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the href property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    @Override
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the rel property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    @Override
    public String getRel() {
        return rel;
    }

    /**
     * Sets the value of the rel property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    @Override
    public void setRel(String value) {
        this.rel = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param type
     * allowed object is
     * {@link String }
     *
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the value of the hreflang property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    public String getHreflang() {
        return hreflang;
    }

    /**
     * Sets the value of the hreflang property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    public void setHreflang(String value) {
        this.hreflang = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param title
     * allowed object is
     * {@link String }
     *
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the value of the length property.
     *
     * @return
     * possible object is
     * {@link String }
     *
     */
    public String getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    public void setLength(String value) {
        this.length = value;
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
     * Sets the value of the base property.
     *
     * @param value
     * allowed object is
     * {@link String }
     *
     */
    public void setBase(String value) {
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
    public void setLang(String value) {
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
     * Returns in-line element about entry / feed in case of expanded navigation link.
     *
     * @return in-line element.
     */
    @Override
    public Element getContent() {
        return getAnyOther().isEmpty() ? null : (Element) getAnyOther().get(0);
    }

    @Override
    public void setContent(final Element content) {
        getAnyOther().clear();
        getAnyOther().add(content);
    }
}
