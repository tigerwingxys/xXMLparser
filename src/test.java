import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;

public class test {
    public static void main(String[] args){
        xXMLParser xmLparser = new xXMLParser();
        String ss = "<autoanswer>no</autoanswer>\r<blacklist>\r<item>555</item>\r<item>556</item>\r" +
            "<!-- this is a comment line -->\r" +
            "<!-- this is a comment line for 3 lines:one\r" + 
            "two\rthree-->\r" + 
            "</blacklist>\r<tagnull test=\'auto\' value=123/>";
        xXMLElement xconfig = xmLparser.parseXML(ss);

        xXMLElement blacklist = xconfig.getElement("blacklist");
        for (xXMLElement ee : blacklist.getChilds() ) {
            if (ee.sName.equalsIgnoreCase("item")) {
                System.out.println("id:" + ee.sName + ", text:"+ ee.sText);
            }

        }

        ArrayList<String> ll = xconfig.toStringList(0);
        for (String oneline : ll) {
            System.out.println(oneline);
        }
        File fSettings = new File("E:\\Projects\\xXMLparser\\xXMLparser\\src\\examples.xml");
        Long fileLength = fSettings.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try{
            FileInputStream inputStream = new FileInputStream(fSettings);
            inputStream.read(fileContent);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        String sSettings = new String(fileContent);

        xconfig = xmLparser.parseXML(sSettings);
        ArrayList<String> lxml  = xconfig.toStringList(0);
        for (String line : lxml) {
            System.out.println(line);
        }

    }
}