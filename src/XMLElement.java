/*----------------------------------------------------------------------
Copyright 2019 Tigerwing XU (tigerwingxys@qq.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-------------------------------------------------------------------------*/
package com.inmountains.xXMLparser;

import java.util.ArrayList;
import java.util.HashMap;

public class XMLElement {
    public XMLElement(){

    }

    public void clear() {
        attributesMap.clear();
        elementList.clear();
    }

    private HashMap<String ,String> attributesMap = new HashMap<>();
    private ArrayList<XMLElement> elementList = new ArrayList<>();
    //normal properties
    public static enum EnumType {ELEMENT,DOCUMENT};
    public EnumType enumType = EnumType.ELEMENT;
    public String sName = "";
    public String sText = "";

    public void setType(EnumType aType){
        enumType = aType;
    }

    //tempory properties for parsing use
    public XMLElement parentElement = null;
    public static enum EnumState {INITIAL,PARSING,CLOSED};
    public EnumState enumState = EnumState.INITIAL;

    public XMLElement getElement(String sID){
        if (sName == null || sName.equals(sID)){
            return this;
        }
        if (elementList.size()>0){
            for (XMLElement ee: elementList
                 ) {
                XMLElement subs = ee.getElement(sID);
                if (subs != null){
                    return subs;
                }
            }
        }
        return null;
    }

    public ArrayList<XMLElement> getChilds(){
        return elementList;
    }

    public XMLElement addChild(){
        XMLElement ee = new XMLElement();
        ee.parentElement = this;
        elementList.add(ee);
        return ee;
    }
    public XMLElement addChild(XMLElement ee){
        ee.parentElement = this;
        elementList.add(ee);
        return ee;
    }
    public void setAttribute(String key, String value){
        attributesMap.put(key,value);
    }
    public String getAttribute(String key){
        return attributesMap.get(key);
    }
}