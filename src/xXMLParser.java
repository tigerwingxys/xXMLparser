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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class xXMLParser {
    private final static Logger logger = Logger.getLogger("javaLog");

    //match Tag begin or end or nullTag
    //private static String catchTagBegin = "<\\w+[^\\n/<]*[/]?>";
    private static String catchTag = "(?<=<)\\w+\\b";
    private static String catchText = "(?<=>).*(?=<)";
    private static String  catchTagEnd = "(?<=</)(\\w+(?=[\\s]*))";
    private static String catchTagNull = "/>";
    //match all Tags, <a> something </a> or <a/> or <a xx="ss"> or </a>...
    private static String catchAllElements = "((<\\w+[^\\n/<]*[/]?>).*(</\\w+[\\s]*>))|<\\w+[^\\n/<]*[/]?>|</\\w+[\\s]*>";
    //match key="value" attributes
    private static String catchAttributes = "(?=[\\s]?)\\w+=\\\".*?\\\"(?<=[\\s]?)";

    private static Pattern pTag = Pattern.compile(catchTag);
    private static Pattern pAttribute = Pattern.compile(catchAttributes);
    private static Pattern pText = Pattern.compile(catchText);
    private static Pattern pTagEnd = Pattern.compile(catchTagEnd);
    private static Pattern pTagNull = Pattern.compile(catchTagNull);

    private xXMLElement document = new xXMLElement();
    public xXMLParser(){
        document.setType(xXMLElement.EnumType.DOCUMENT);
    }

    public xXMLElement parseXML(String sXml)
    {
        if (sXml == null || sXml.isEmpty()){
            return null;
        }
        logger.log(Level.INFO,"parseXML, begin parse..."+sXml);
        document.clear();
        document.setType(xXMLElement.EnumType.DOCUMENT);

        Pattern pattern = Pattern.compile(catchAllElements);
        Matcher mElements = pattern.matcher(sXml);
        xXMLElement xmlElement = document.addChild();
        document.enumState = xXMLElement.EnumState.PARSING;
        String ss = null;
        while( mElements.find() ) {
            ss = mElements.group();
            logger.log(Level.INFO,"parseXML, in while, parsing["+ss+"].");
            xmlElement = parseOneElement(ss,xmlElement);
            if (xmlElement == null){
                break;
            }
        }
        document.enumState = xXMLElement.EnumState.CLOSED;

        logger.log(Level.INFO,"parse end.");
        return document;
    }

    public final static boolean parseMatchRules(String sRule,String sSource){
        logger.log(Level.INFO,"parseMatchRules, rule["+sRule+"], source["+sSource+"].");
        Pattern pattern = Pattern.compile(sRule);
        Matcher matcher = pattern.matcher(sSource);
        if(matcher.find()){
            logger.log(Level.INFO,"parseMatchRules, matched.");
            return true;
        }
        logger.log(Level.INFO,"parseMatchRules, not matched.");
        return false;
    }

    public xXMLElement parseOneElement(String ss,xXMLElement oneElement){

        logger.log(Level.INFO,"parseOneElement beginning...");
        logger.log(Level.INFO,"parseOneElement, current element["+oneElement.sName+"]'s enumState:"+oneElement.enumState);

        if (oneElement.enumState == xXMLElement.EnumState.INITIAL) {
            oneElement.enumState = xXMLElement.EnumState.PARSING;

            //parse Element name
            Matcher mTag = pTag.matcher(ss);
            if (mTag.find()) {
                oneElement.sName = mTag.group();
                logger.log(Level.INFO,"INITIAL branch-- sName:"+oneElement.sName);
            }

            //parse Element text
            Matcher mText = pText.matcher(ss);
            if (mText.find()) {
                oneElement.sText = mText.group();
                logger.log(Level.INFO,"INITIAL branch-- sText:"+oneElement.sText);
            }

            //parse attributes
            Matcher mAttribute = pAttribute.matcher(ss);
            while (mAttribute.find()) {
                String[] atts = mAttribute.group().split("=");
                if (atts.length > 1) {
                    oneElement.setAttribute(atts[0], atts[1]);
                    logger.log(Level.INFO,"INITIAL branch-- attribute key:"+atts[0]+",value:"+atts[1]);
                }
            }

            //parse tag end, if found continue, else , the next element is its subElement
            Matcher mTagNull = pTagNull.matcher(ss);
            Matcher mTagEnd = pTagEnd.matcher(ss);

            if (mTagNull.find()) {//this element finished, brother
                oneElement.enumState = xXMLElement.EnumState.CLOSED;
                return oneElement;
            }
            else if (mTagEnd.find()) {
                String sTagEnd = mTagEnd.group();
                if (sTagEnd.equals( oneElement.sName)) {
                    oneElement.enumState = xXMLElement.EnumState.CLOSED;
                    return oneElement;
                } else {
                    //error, current element not closed, but met its parents's END tag.
                    logger.log(Level.SEVERE,"INITIAL branch-- sName:"+oneElement.sName+
                            " still not closed, but met a tagEND:"+sTagEnd);
                }
            } else {
                return oneElement;
            }
        }
        else if (oneElement.enumState == xXMLElement.EnumState.PARSING) {
            //parse Element name
            Matcher mTag = pTag.matcher(ss);
            if (mTag.find()) {
                String sname = mTag.group();
                xXMLElement subElement = oneElement.addChild();
                subElement.sName = sname;
                oneElement = subElement;
                oneElement.enumState = xXMLElement.EnumState.PARSING;
                logger.log(Level.INFO,"PARSING branch-- add child element["+oneElement.sName+"], "+
                        "parent element["+oneElement.parentElement.sName+"].");
            }
            logger.log(Level.INFO,"PARSING branch-- sName:"+oneElement.sName);

            //parse Element text
            Matcher mText = pText.matcher(ss);
            if (mText.find()) {
                oneElement.sText = mText.group();
                logger.log(Level.INFO,"PARSING branch-- sText:"+oneElement.sText);
            }

            //parse attributes
            Matcher mAttribute = pAttribute.matcher(ss);
            while (mAttribute.find()) {
                String[] atts = mAttribute.group().split("=");
                if (atts.length > 1) {
                    oneElement.setAttribute(atts[0], atts[1]);
                    logger.log(Level.INFO,"PARSING branch-- attribute key:"+atts[0]+",value:"+atts[1]);
                }
            }

            //parse tag end, if found continue, else , the next element is its subElement
            Matcher mTagNull = pTagNull.matcher(ss);
            Matcher mTagEnd = pTagEnd.matcher(ss);

            if (mTagNull.find()) {//this element finished, brother
                oneElement.enumState = xXMLElement.EnumState.CLOSED;
                return oneElement;
            }
            else if (mTagEnd.find()) {
                String sTagEnd = mTagEnd.group();
                if (sTagEnd.equals( oneElement.sName)) {
                    oneElement.enumState = xXMLElement.EnumState.CLOSED;
                    return oneElement;
                } else {
                    //error, current element not closed, but met its parents's END tag.
                    logger.log(Level.SEVERE,"PARSING branch-- sName:"+oneElement.sName+
                            " still not closed, but met a tagEND:"+sTagEnd);
                }
            } else {
                return oneElement;
            }
        }else {//one element is closed.
            //first check if met parents's END tag
            Matcher mTagEnd = pTagEnd.matcher(ss);
            String sTagEnd = "";
            if (mTagEnd.find()){
                sTagEnd = mTagEnd.group();
                if( sTagEnd.equals(oneElement.parentElement.sName))
                {
                    oneElement.parentElement.enumState = xXMLElement.EnumState.CLOSED;
                    logger.log(Level.INFO,"CLOSE branch-- sName:"+oneElement.sName+
                            ", parent's sName:"+oneElement.parentElement.sName+
                            ",tagEND:"+sTagEnd);
                    return oneElement.parentElement;
                }
            }

            xXMLElement brotherElement = oneElement.parentElement.addChild();
            brotherElement.enumState = xXMLElement.EnumState.PARSING;
            oneElement = brotherElement;
            logger.log(Level.INFO,"CLOSE branch-- add brother element["+oneElement.sName+"].");

            //parse Element name
            Matcher mTag = pTag.matcher(ss);
            if (mTag.find()) {
                oneElement.sName = mTag.group();
                logger.log(Level.INFO,"CLOSE branch-- sName:"+oneElement.sName);
            }

            //parse Element text
            Matcher mText = pText.matcher(ss);
            if (mText.find()) {
                oneElement.sText = mText.group();
                logger.log(Level.INFO,"CLOSE branch-- sText:"+oneElement.sText);
            }

            //parse attributes
            Matcher mAttribute = pAttribute.matcher(ss);
            while (mAttribute.find()) {
                String[] atts = mAttribute.group().split("=");
                if (atts.length > 1) {
                    oneElement.setAttribute(atts[0], atts[1]);
                    logger.log(Level.INFO,"CLOSE branch-- attribute key:"+atts[0]+",value:"+atts[1]);
                }
            }

            //parse tag end, if found continue, else , the next element is its subElement
            Matcher mTagNull = pTagNull.matcher(ss);

            if (mTagNull.find() || sTagEnd.equals(oneElement.sName )) {//this element finished, brother
                oneElement.enumState = xXMLElement.EnumState.CLOSED;
                return oneElement;
            }else {
                return oneElement;
            }

        }
        logger.log(Level.INFO,"parseOneElement, the source["+ss+"] had parsed ok.");
        return oneElement;
    }

}
