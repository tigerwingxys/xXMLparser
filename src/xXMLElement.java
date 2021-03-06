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
import java.util.ArrayList;
import java.util.HashMap;

public class xXMLElement {
    public xXMLElement(){

    }

    public void clear() {
        attributesMap.clear();
        elementList.clear();
        listContent.clear();
    }

    private HashMap<String ,String> attributesMap = new HashMap<String ,String>();
    private ArrayList<xXMLElement> elementList = new ArrayList<xXMLElement>();
    private static ArrayList<String> listContent = new ArrayList<String>();
    //normal properties
    public String sName = "";
    public String sText = "";

    //tempory properties for parsing use
    public xXMLElement parentElement = null;
    public static enum EnumState {INITIAL,PARSING,CLOSED};
    public EnumState enumState = EnumState.INITIAL;

    public xXMLElement getElement(String sID){
        if (sName == null || sName.equals(sID)){
            return this;
        }
        if (elementList.size()>0){
            for (xXMLElement ee: elementList
                 ) {
                xXMLElement subs = ee.getElement(sID);
                if (subs != null){
                    return subs;
                }
            }
        }
        return null;
    }

    public ArrayList<xXMLElement> getChilds(){
        return elementList;
    }

    public xXMLElement addChild(){
        xXMLElement ee = new xXMLElement();
        ee.parentElement = this;
        elementList.add(ee);
        return ee;
    }
    public xXMLElement addChild(xXMLElement ee){
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
    public ArrayList<String> toStringList(int treeLevel ){
        String astr = "";
        for(int i = 0 ; i < treeLevel ; i ++){
            astr += "    ";
        }
        astr += "[" + sName + "]";
        if( !sText.equals("") ){
            astr += ", Text[" + sText + "]";
        }
        int iSize = attributesMap.size();
        if( iSize>0 ){
            int i = 0;
            astr += ", Attributes[";
            for( String sKey : attributesMap.keySet() ){
                astr += sKey + "=" + attributesMap.get(sKey);
                if( i != iSize-1 ){
                    astr += ",";
                }
                i ++;
            }
            astr += "]";
        }
        listContent.add(astr);

        for( xXMLElement oneItem : elementList){
            oneItem.toStringList(treeLevel + 1);
        }
        return listContent;
    }
}
