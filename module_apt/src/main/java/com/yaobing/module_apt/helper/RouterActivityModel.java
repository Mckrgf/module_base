package com.yaobing.module_apt.helper;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class RouterActivityModel {
    boolean isNeedBind;
    TypeElement element;
    String actionName;
    String SceneTransitionElementName;
    Element SceneTransitionElement;
    List<Element> ExtraElements;
    List<String> ExtraElementKeys;

    public RouterActivityModel() {
    }

    public String getSceneTransitionElementName() {
        return this.SceneTransitionElementName;
    }

    public void setSceneTransitionElementName(String sceneTransitionElementName) {
        this.SceneTransitionElementName = sceneTransitionElementName;
    }

    public List<String> getExtraElementKeys() {
        return this.ExtraElementKeys;
    }

    public void setExtraElementKeys(List<String> extraElementKeys) {
        this.ExtraElementKeys = extraElementKeys;
    }

    public List<Element> getExtraElements() {
        return this.ExtraElements;
    }

    public void setExtraElements(List<Element> extraElements) {
        this.ExtraElements = extraElements;
    }

    public Element getSceneTransitionElement() {
        return this.SceneTransitionElement;
    }

    public void setSceneTransitionElement(Element sceneTransitionElement) {
        this.SceneTransitionElement = sceneTransitionElement;
    }

    public TypeElement getElement() {
        return this.element;
    }

    public void setElement(TypeElement mElement) {
        this.element = mElement;
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String mActionName) {
        this.actionName = mActionName;
    }

    public boolean isNeedBind() {
        return this.isNeedBind;
    }

    public void setNeedBind(boolean needBind) {
        this.isNeedBind = needBind;
    }
}

