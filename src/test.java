public class test {
    public static void main(String[] args){
        xXMLParser xmLparser = new xXMLParser();
        String ss = "<autoanswer>no</autoanswer>\n<blacklist>\n<item>555</item>\n<item>556</item>\n</blacklist>\n";
        xXMLElement xconfig = xmLparser.parseXML(ss);

        xXMLElement blacklist = xconfig.getElement("blacklist");
        for (xXMLElement ee : blacklist.getChilds() ) {
            if (ee.sName.equalsIgnoreCase("item")) {
                System.out.println("id:" + ee.sName + ", text:"+ ee.sText);
            }

        }

    }
}