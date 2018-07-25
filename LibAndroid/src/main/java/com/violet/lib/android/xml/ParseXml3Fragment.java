package com.violet.lib.android.xml;

import android.content.Intent;
import android.util.Xml;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.ui.fragment.BaseFragment;
import com.violet.lib.android.LibAndroidRouter;
import com.violet.lib.android.R;
import com.violet.lib.android.bean.Student;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by kan212 on 2018/5/14.
 * 三种xml的解析方式
 */
@Route(path = LibAndroidRouter.libRouter.LIB_ANDROID_XML)
public class ParseXml3Fragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.lib_android_fragment_parse;
    }

    @Override
    protected void initView(View parent) {

    }


    /**
     * SAX和Pull的区别：SAX解析器的工作方式是自动将事件推入事件处理器进行处理，
     * 因此你不能控制事件的处理主动结束；而Pull解析器的工作方式为允许你的应用程序代码主动从解析器中获取事件，
     * 正因为是主动获取事件，因此可以在满足了需要的条件后不再获取事件，结束解析
     * @param intent
     */
    @Override
    protected void initData(Intent intent) {

        try {
            dom2xml(getActivity().getResources().getAssets().open("student.xml"));
            pull2xml(getActivity().getResources().getAssets().open("student.xml"));
            sax2xml(getActivity().getResources().getAssets().open("student.xml"));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }


    /**
     * xml的dom解析
     *
     * @param is
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public List<Student> dom2xml(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        List<Student> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //获得Document对象
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        //获得student的List
        NodeList studentList = document.getElementsByTagName("student");
        //遍历student标签
        for (int i = 0; i < studentList.getLength(); i++) {
            Node node = studentList.item(i);
            NodeList childNodes = node.getChildNodes();
            Student student = new Student();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if ("name".equals(childNode.getNodeName())) {
                    String name = childNode.getTextContent();
                    student.setName(name);
                    //获取name的属性
                    NamedNodeMap nnm = childNode.getAttributes();
                    //获取sex属性，由于只有一个属性，所以取0
                    Node n = nnm.item(0);
                    student.setSex(n.getTextContent());
                } else if ("nickName".equals(childNode.getNodeName())) {
                    String nickName = childNode.getTextContent();
                    student.setNickName(nickName);
                }
            }
            list.add(student);
        }
        return list;
    }

    /**
     * SAX是一个解析速度快并且占用内存少的xml解析器，SAX解析XML文件采用的是事件驱动，
     * 它并不需要解析完整个文档，而是按内容顺序解析文档的过程
     * @param is
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public List<Student> sax2xml(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //初始化解析器
        SAXParser parser = spf.newSAXParser();
        MyHandler myHandler = new MyHandler();
        parser.parse(is, myHandler);
        return myHandler.getList();
    }

    public class MyHandler extends DefaultHandler {

        private List<Student> list;
        private Student student;
        //用于存储读取的临时变量
        private String tempString;

        /**
         * 解析到文档开始调用，一般做初始化操作
         *
         * @throws SAXException
         */
        @Override
        public void startDocument() throws SAXException {
            list = new ArrayList<>();
            super.startDocument();
        }

        /**
         * 解析到文档末尾调用，一般做回收操作
         *
         * @throws SAXException
         */
        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        /**
         * 每读到一个元素就调用该方法
         *
         * @param uri
         * @param localName
         * @param qName
         * @param attributes
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("student".equals(qName)) {
                student = new Student();
            } else if ("name".equals(qName)) {
                String sex = attributes.getValue("sex");
                student.setSex(sex);
            }
            super.startElement(uri, localName, qName, attributes);
        }

        /**
         * 读到元素的结尾调用
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("student".equals(qName)) {
                list.add(student);
            }
            if ("name".equals(qName)) {
                student.setName(tempString);
            } else if ("nickName".equals(qName)) {
                student.setName(tempString);
            }
            super.endElement(uri, localName, qName);
        }

        /**
         * 读到属性内容调用
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            tempString = new String(ch, start, length);
            super.characters(ch, start, length);
        }

        /**
         * 获取该List
         */
        public List<Student> getList() {
            return list;
        }
    }

    /**
     * Pull解析器的运行方式与 SAX 解析器相似。它提供了类似的事件，可以使用一个switch对感兴趣的事件进行处理
     * @param is
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<Student> pull2xml(InputStream is) throws XmlPullParserException, IOException {
        List<Student> list = null;
        Student student = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is,"utf-8");
        int type = parser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT){
            switch (type){
                case XmlPullParser.START_TAG:
                    if ("students".equals(parser.getName())){
                        list = new ArrayList<>();
                    }else if ("student".equals(parser.getName())){
                        student = new Student();
                    }else if ("name".equals(parser.getName())){
                        String sex = parser.getAttributeValue(null,"sex");
                        student.setSex(sex);
                        String name = parser.nextText();
                        student.setName(name);
                    }else if ("nickName".equals(parser.getName())){
                        String nickName = parser.nextText();
                        student.setNickName(nickName);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("student".equals(parser.getName())){
                        list.add(student);
                    }
                    break;
            }
            type = parser.next();
        }
        return list;
    }

}
